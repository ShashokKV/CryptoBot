package com.chess.cryptobot.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import androidx.preference.PreferenceManager
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.enricher.PairResponseEnricher
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.exceptions.SyncServiceException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.WithdrawalMarketFactory
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.response.TradeLimitResponse
import com.chess.cryptobot.model.room.BalanceSyncTicker
import com.chess.cryptobot.model.room.CryptoBotDatabase
import com.chess.cryptobot.util.CoinInfo
import com.chess.cryptobot.view.notification.NotificationBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BalanceSyncService : IntentService("BalanceSyncService") {
    private var resultInfo = ""
    private var coinInfo: CoinInfo? = null
    private val marketsMap: MutableMap<String, Market?> = HashMap()
    private var minBalance = 0.0
    private var minBtcAmount = 0.0005
    private var minQuantityMap: MutableMap<String, TradeLimitResponse?> = HashMap()
    private var tickersMap: MutableMap<String, List<TickerResponse>> = HashMap()
    private var makeNotifications = false

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        makeNotifications = intent.getBooleanExtra("makeNotifications", false)
        if (makeNotifications) makeNotification("Balance sync in progress...")
        val balancePreferences = BalancePreferences(this)
        minBtcAmount = balancePreferences.getMinBtcAmount()
        val coinNames = intent.getStringArrayListExtra("coinNames")
        val marketFactory = WithdrawalMarketFactory()
        val markets = marketFactory.getMarkets(this, PreferenceManager.getDefaultSharedPreferences(this))
        markets.forEach { marketsMap[it!!.getMarketName()] = it }
        coinInfo = try {
            CoinInfo(markets)
        } catch (e: MarketException) {
            updateInfo("BalanceSync", "Can't init coinInfo: " + e.message)
            makeNotification(resultInfo)
            return
        }
        var syncExecuted = false
        coinNames?.forEach { coinName: String ->
            try {
                if (sync(coinName, markets)) {
                    syncExecuted = true
                }
            } catch (e: SyncServiceException) {
                updateInfo(coinName, e.message)
            }
        }
        if (!syncExecuted && resultInfo.isEmpty() && makeNotifications) {
            resultInfo = "No sync needed"
        }
        makeNotification(resultInfo)
    }

    @Throws(SyncServiceException::class)
    private fun sync(coinName: String, markets: List<Market?>): Boolean {
        try {
            initMinBalance(coinName, markets)
        } catch (e: MarketException) {
            throw SyncServiceException(e.message)
        }
        if (!coinInfo!!.checkCoinStatus(coinName)) {
            throw SyncServiceException("Not active")
        }
        val marketAmounts: Map<String, Double>
        marketAmounts = try {
            getMarketsAmounts(markets, coinName)
        } catch (e: MarketException) {
            throw SyncServiceException(e.message)
        }
        val coinMover = CoinMover(coinName)
        coinMover.setAmounts(marketAmounts)
        coinMover.computeDirection()
        return coinMover.checkAndMove()
    }

    @Throws(MarketException::class)
    private fun initMinBalance(coinName: String, markets: List<Market?>) {
        if (coinName == "BTC") {
            minBalance = minBtcAmount
            return
        }
        var pair: Pair
        pair = if (coinName == "USDT") {
            Pair("USDT", "BTC")
        } else {
            Pair("BTC", coinName)
        }
        val minBalances = ArrayList<Double>(3)
        markets.forEach { market ->
            if (market != null) {
                val marketName = market.getMarketName()
                if (minQuantityMap[marketName] == null) {
                    minQuantityMap[marketName] = market.getMinQuantity()
                }
                if (tickersMap[marketName] == null) {
                    tickersMap[marketName] = market.getTicker()
                }
                val pairName = pair.name
                minBalances.add(minQuantityMap[marketName]?.getTradeLimitByName(pair.getPairNameForMarket(marketName))
                        ?: 0.0)
                pair = PairResponseEnricher(pair).enrichFromTicker(tickersMap[marketName]!!
                        .filter { it.tickerName == pairName }[0], marketName).pair
            }
        }

        val minBalanceByTicker = (pair.bidMap.values.max() ?: 0.0) * minBtcAmount
        val minBalanceByMarket = minBalances.max() ?: 0.0
        minBalance = if (minBalanceByTicker > minBalanceByMarket) {
            minBalanceByTicker
        } else {
            minBalanceByMarket
        }
    }

    @Throws(MarketException::class)
    private fun getMarketsAmounts(markets: List<Market?>, coinName: String): Map<String, Double> {
        val marketAmounts: MutableMap<String, Double> = HashMap()
        for (market in markets) {
            marketAmounts[market!!.getMarketName()] = market.getAmount(coinName)
        }
        return marketAmounts
    }

    private fun updateInfo(coinName: String, message: String?) {
        resultInfo += String.format("%s: %s%s", coinName, message, System.lineSeparator())
    }

    private fun makeNotification(notificationText: String) {
        if (notificationText.isEmpty()) return
        NotificationBuilder(this)
                .setNotificationId(NOTIFICATION_ID)
                .setChannelId(CHANNEL_ID)
                .setNotificationText(notificationText)
                .setChannelName("Balance sync service")
                .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .setTitle("Balance synchronization")
                .buildAndNotify()
        resultInfo = ""
    }

    internal inner class CoinMover(private val coinName: String) {
        private var amounts: Map<String, Double>? = null
        private var moveFrom: String? = null
        private var moveTo: String? = null
        fun setAmounts(amounts: Map<String, Double>?) {
            this.amounts = amounts
        }

        internal fun computeDirection() {
            val maxValue = amounts?.values?.max()
            val minValue = amounts?.values?.min()
            moveFrom = amounts?.filterValues { amount -> amount == maxValue }?.keys?.first()
            moveTo = amounts?.filterValues { amount -> amount == minValue }?.keys?.first()
        }

        @Throws(SyncServiceException::class)
        internal fun checkAndMove(): Boolean {
            if (moveFrom == null || moveTo == null) throw SyncServiceException("Init of coin move direction failed!")
            val fromAmount = getAmount(amounts, moveFrom)
            val toAmount = getAmount(amounts, moveTo)
            val fee = coinInfo!!.getFee(moveFrom as String, coinName)
            val moveFromMarket: Market = marketsMap[moveFrom as String]!!
            val moveToMarket: Market = marketsMap[moveTo as String]!!
            var delta = getDelta(toAmount)
            if (needSync(delta)) {
                checkAmount(fromAmount, fee)
                checkHistory(moveToMarket)
                delta = formatAmount(recalculateDelta(fromAmount, toAmount, fee))
                return try {
                    moveBalances(moveFromMarket, moveToMarket, coinName, delta)
                    createSyncTicker(moveFromMarket)
                    true
                } catch (e: MarketException) {
                    throw SyncServiceException(e.message)
                }
            }
            return false
        }

        @Throws(MarketException::class)
        private fun createSyncTicker(moveFromMarket: Market) {
            val history = moveFromMarket.getWithdrawHistory()
            if (history.isEmpty()) return
            val historyItem = history.sortedByDescending { it.dateTime }[0]
            val database = CryptoBotDatabase.getInstance(applicationContext)
            val dao = database?.balanceSyncDao
            val ticker = BalanceSyncTicker()
            ticker.coinName = historyItem.currencyName
            ticker.marketName = moveTo
            ticker.dateCreated = historyItem.dateTime
            ticker.amount = historyItem.amount
            dao?.insert(ticker)
        }

        private fun needSync(delta: Double): Boolean {
            return delta > 0
        }

        private fun getDelta(amount: Double): Double {
            return minBalance - amount
        }

        @Throws(SyncServiceException::class)
        private fun getAmount(amounts: Map<String, Double>?, marketName: String?): Double {
            return amounts!![marketName] ?: throw SyncServiceException("Can't get amount")
        }

        private fun recalculateDelta(fromAmount: Double, toAmount: Double, fee: Double): Double {
            return (fromAmount - fee + toAmount) / 2 - toAmount
        }

        private fun formatAmount(amount: Double): Double {
            val bd: BigDecimal = if (amount > 10.0) {
                BigDecimal(amount).setScale(0, RoundingMode.UP)
            } else {
                BigDecimal(amount).setScale(8, RoundingMode.HALF_UP)
            }
            return bd.toDouble()
        }

        @Throws(SyncServiceException::class)
        private fun checkAmount(fromAmount: Double, fee: Double) {
            if (fromAmount < fee) throw SyncServiceException("Not enough coins")
        }

        @Throws(SyncServiceException::class)
        private fun checkHistory(moveToMarket: Market) {
            val database = CryptoBotDatabase.getInstance(applicationContext)
            val dao = database?.balanceSyncDao
            val balanceSyncTickers: List<BalanceSyncTicker> =
                    dao?.getByCoinNameAndMarket(coinName, moveToMarket.getMarketName())
                            ?.sortedByDescending { it.dateCreated } ?: return
            if (balanceSyncTickers.isEmpty()) return
            val ticker = balanceSyncTickers[0]

            val history = ArrayList<History>()
            marketsMap.values.forEach { history.addAll(it!!.getDepositHistory()) }
            val filteredHistory = history.filter {
                        it.action?.toLowerCase(Locale.ROOT) == "deposit"
                                && it.currencyName.equals(coinName)
                                && it.amount == ticker.amount
                    }

            if (filteredHistory.isNotEmpty()) {
                dao.deleteAll(balanceSyncTickers)
            } else {
                throw SyncServiceException("Deposit in progress")
            }
        }

        @Throws(MarketException::class)
        private fun moveBalances(moveFrom: Market, moveTo: Market, coinName: String, amount: Double) {
            val address = moveTo.getAddress(coinName)
                    ?: throw SyncServiceException("Could not get address for " + moveTo.getMarketName())
            moveFrom.sendCoins(coinName, amount, address)
            updateInfo(coinName, "$amount sent from ${moveFrom.getMarketName()} to ${moveTo.getMarketName()}")
        }

    }

    companion object {
        private const val CHANNEL_ID = "balance_sync_channel"
        private const val NOTIFICATION_ID: Int = 12385264
    }
}