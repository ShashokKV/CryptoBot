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
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.response.TradeLimitResponse
import com.chess.cryptobot.model.room.BalanceSyncTicker
import com.chess.cryptobot.model.room.CryptoBotDatabase
import com.chess.cryptobot.util.CoinInfo
import com.chess.cryptobot.view.notification.NotificationBuilder
import com.chess.cryptobot.view.notification.NotificationID
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

class BalanceSyncService : IntentService("BalanceSyncService") {
    private var resultInfo = ""
    private var coinInfo: CoinInfo? = null
    private val marketsMap: MutableMap<String, Market?> = HashMap()
    private var minBalance = 0.0
    private var minBtcAmount = 0.0005
    private var minQuantityMap: MutableMap<String, TradeLimitResponse?> = HashMap()
    private var tickersMap: MutableMap<String, List<TickerResponse>> = HashMap()

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
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
            makeNotification()
            return
        }
        coinNames?.forEach { coinName: String ->
            try {
                sync(coinName, markets)
            } catch (e: SyncServiceException) {
                updateInfo(coinName, e.message)
            }
        }
        makeNotification()
    }

    @Throws(SyncServiceException::class)
    private fun sync(coinName: String, markets: List<Market?>) {
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
        coinMover.checkAndMove()
    }

    @Throws(MarketException::class)
    private fun initMinBalance(coinName: String, markets: List<Market?>) {
        if (coinName == "BTC") {
            minBalance = minBtcAmount
            return
        }
        var pair = Pair("BTC", coinName)
        val minBalances = ArrayList<Double>(3)
        markets.forEach { market ->
            if (market != null) {
                val marketName = market.getMarketName()
                if (minQuantityMap[marketName]==null) {
                    minQuantityMap[marketName] = market.getMinQuantity()
                }
                if (tickersMap[marketName]==null) {
                    tickersMap[marketName] = market.getTicker()
                }
                val pairName = pair.name
                minBalances.add(minQuantityMap[marketName]?.getTradeLimitByName(pair.getPairNameForMarket(marketName))?: 0.0)
                pair = PairResponseEnricher(pair).enrichFromTicker(tickersMap[marketName]!!
                        .filter { it.tickerName == pairName }[0], marketName).pair
            }
        }

        val minBalanceByTicker = pair.bidMap.values.max() ?: 0.0 * minBtcAmount
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

    private fun makeNotification() {
        if (resultInfo.isEmpty()) return
        NotificationBuilder(this)
                .setNotificationId(NotificationID.id)
                .setChannelId(CHANNEL_ID)
                .setNotificationText(resultInfo)
                .setChannelName("Balance sync service")
                .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .setTitle("Balance sync result")
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
                    createSyncTicker(delta)
                    true
                } catch (e: MarketException) {
                    throw SyncServiceException(e.message)
                }
            }
            return false
        }

        private fun createSyncTicker(amount: Double) {
            val database = CryptoBotDatabase.getInstance(applicationContext)
            val dao = database?.balanceSyncDao
            val ticker = BalanceSyncTicker()
            ticker.coinName = coinName
            ticker.marketName = moveTo
            ticker.dateCreated = LocalDateTime.now()
            ticker.amount = amount
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

            val history = moveToMarket.getHistory(applicationContext)
                    .filter { it.action.equals("deposit")
                            && it.currencyName.equals(coinName)
                            && it.amount==ticker.amount}

            if (history.isNotEmpty()) {
                dao.deleteAll(balanceSyncTickers)
            }else{
                throw SyncServiceException("Deposit in progress")
            }
        }

        @Throws(MarketException::class)
        private fun moveBalances(moveFrom: Market, moveTo: Market, coinName: String, amount: Double) {
            val address = moveTo.getAddress(coinName)
                    ?: throw SyncServiceException("Could not get address for "+moveTo.getMarketName())
            moveFrom.sendCoins(coinName, amount, address)
            updateInfo(coinName, "$amount sent from ${moveFrom.getMarketName()} to ${moveTo.getMarketName()}")
        }

    }

    companion object {
        private const val CHANNEL_ID = "balance_sync_channel"
    }
}