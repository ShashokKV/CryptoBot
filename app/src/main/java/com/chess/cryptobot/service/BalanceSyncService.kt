package com.chess.cryptobot.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import android.preference.PreferenceManager
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.exceptions.SyncServiceException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.util.CoinInfo
import com.chess.cryptobot.view.notification.NotificationBuilder
import com.chess.cryptobot.view.notification.NotificationID
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class BalanceSyncService : IntentService("BalanceSyncService") {
    private var resultInfo = ""
    private var coinInfo: CoinInfo? = null
    private val marketsMap: MutableMap<String, Market?> = HashMap()
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        val coinNames: List<String> = intent.getStringArrayListExtra("coinNames")
        val marketFactory = MarketFactory()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val markets = marketFactory.getMarkets(this, preferences)
        markets.forEach { market: Market? -> marketsMap[market!!.getMarketName()] = market }
        coinInfo = try {
            CoinInfo(markets)
        } catch (e: MarketException) {
            updateInfo("BalanceSync", "Can't init coinInfo: " + e.message)
            makeNotification()
            return
        }
        coinNames.forEach { coinName: String ->
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
        val minBalance = BalancePreferences(this).getMinBalance(coinName)
        if (minBalance == 0.0) {
            throw SyncServiceException("Min balance not set")
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
        val coinMover = CoinMover(minBalance, coinName)
        coinMover.setAmounts(marketAmounts)
        coinMover.setDirection(Market.BITTREX_MARKET, Market.BINANCE_MARKET)
        if (!coinMover.checkAndMove()) {
            coinMover.setDirection(Market.BINANCE_MARKET, Market.BITTREX_MARKET)
            coinMover.checkAndMove()
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

    internal inner class CoinMover(private val minBalance: Double?, private val coinName: String) {
        private var amounts: Map<String, Double>? = null
        private var moveFrom: String? = null
        private var moveTo: String? = null
        fun setAmounts(amounts: Map<String, Double>?) {
            this.amounts = amounts
        }

        internal fun setDirection(moveFrom: String, moveTo: String) {
            this.moveFrom = moveFrom
            this.moveTo = moveTo
        }

        @Throws(SyncServiceException::class)
        internal fun checkAndMove(): Boolean {
            if (moveFrom == null || moveTo == null) throw SyncServiceException("Init of coin move direction failed!")
            val fromAmount = getAmount(amounts, moveFrom)
            val toAmount = getAmount(amounts, moveTo)
            val fee = coinInfo!!.getFee(moveFrom as String, coinName)
            val moveFromMarket: Market = marketsMap[moveFrom as String]!!
            val moveToMarket: Market = marketsMap[moveTo as String]!!
            var delta = getDelta(toAmount, minBalance)
            if (needSync(delta)) {
                checkAmount(fromAmount, fee)
                delta = formatAmount(recalculateDelta(fromAmount, toAmount, fee))
                return try {
                    moveBalances(moveFromMarket, moveToMarket, coinName, delta)
                    true
                } catch (e: MarketException) {
                    throw SyncServiceException(e.message)
                }
            }
            return false
        }

        private fun needSync(delta: Double): Boolean {
            return delta > 0
        }

        private fun getDelta(amount: Double, minBalance: Double?): Double {
            return minBalance!! - amount
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

        @Throws(MarketException::class)
        private fun moveBalances(moveFrom: Market, moveTo: Market, coinName: String, amount: Double) {
            val address = moveTo.getAddress(coinName)
            moveFrom.sendCoins(coinName, amount, address!!)
            updateInfo(coinName, String.format("%s sent from %s", amount.toString(), moveFrom.getMarketName()))
        }

    }

    companion object {
        private const val CHANNEL_ID = "balance_sync_channel"
    }
}