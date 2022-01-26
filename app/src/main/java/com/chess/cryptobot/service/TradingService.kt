package com.chess.cryptobot.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import android.util.Log
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.view.notification.NotificationBuilder
import com.chess.cryptobot.view.notification.NotificationID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap

class
TradingService : IntentService("TradingService") {
    private var resultInfo = ""
    private lateinit var pair: Pair
    private var marketsMap = HashMap<String, Market>()
    private var bidMarketAmount: Double = 0.0
    private var askBaseAmount: Double = 0.0
    private var minMarketQuantity: Double = 0.0
    private var stepSize: Double = 0.0
    private var priceFilter: Double = 0.0
    private var minBtcAmount = 0.0005
    private var minEthAmount = 0.025
    private var minUsdtAmount = 10.0
    private val scope = CoroutineScope(SupervisorJob())
    private val tag = TradingService::class.qualifiedName

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        initFromIntent(intent)
        initMarkets()
        if (isApiKeysEmpty) {
            makeNotification("Api keys are empty", "Please fill api keys in settings or turn AutoTrade off")
            return
        }
        try {
            initAmounts()
        } catch (e: MarketException) {
            makeNotification("Init amounts exception", e.message)
            return
        }
        val trader = Trader(pair)
        if (trader.quantity <= minMarketQuantity) {
            Log.d(tag, pair.name + " quantity to low, aborting..")
            return
        }
        val buyResult = scope.async { trader.buy() }
        val sellResult = scope.async { trader.sell() }
        runBlocking {
            trader.updateInfo(buyResult.await())
            trader.updateInfo(sellResult.await())
        }
        if (!trader.success) {
            makeNotification("Trade exception", resultInfo)
            return
        } else {
            makeNotification("Trading results", resultInfo)
            trader.syncBalance()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workingOnPair = null
    }

    private fun initFromIntent(intent: Intent) {
        pair = intent.getSerializableExtra(Pair::class.java.name) as Pair
        Log.d(tag, "Start trading on pair: " + pair.name)
        workingOnPair = pair.name
        minMarketQuantity = intent.getDoubleExtra("minQuantity", 0.0)
        stepSize = intent.getDoubleExtra("stepSize", 0.00000001)
        priceFilter = intent.getDoubleExtra("priceFilter", 0.00000001)
    }

    private fun initMarkets() {
        MarketFactory.getInstance(this).getMarkets()
                .forEach { market -> if (market != null) marketsMap[market.getMarketName()] = market }
    }

    private val isApiKeysEmpty: Boolean
        get() = marketsMap.values.stream().allMatch { market: Market? ->
            market?.keysIsEmpty() ?: false
        }

    @Throws(MarketException::class)
    private fun initAmounts() {
        val askBaseDeferred = scope.async {
            marketsMap[pair.askMarketName]?.getAmount(pair.baseName) ?: 0.0
        }
        val bidMarketDeferred = scope.async {
            marketsMap[pair.bidMarketName]?.getAmount(pair.marketName) ?: 0.0
        }
        var exception: MarketException? = null
        runBlocking {
            try {
                askBaseAmount = askBaseDeferred.await()
                bidMarketAmount = bidMarketDeferred.await()
            }catch (e: MarketException) {
                exception = e
            }
        }
        if (exception!=null) {throw exception as MarketException }
        val balancePreferences = BalancePreferences(this)
        minBtcAmount = balancePreferences.getMinBtcAmount()
        minEthAmount = balancePreferences.getMinEthAmount()
        minUsdtAmount = balancePreferences.getMinUsdtAmount()

    }

    private fun makeNotification(title: String, message: String?) {
        if (message!!.isEmpty()) return
        
        NotificationBuilder(this)
                .setNotificationId(NotificationID.id)
                .setChannelId(CHANNEL_ID)
                .setNotificationText(resultInfo)
                .setChannelName("Trading service")
                .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                .setTitle(title)
                .setNotificationText(message)
                .buildAndNotify()
        resultInfo = ""
    }

    private inner class Trader(pair: Pair) {
        var bidPrice: Double = 0.0
        var askPrice: Double = 0.0
        var quantity: Double = 0.0
        private var sellMarket: Market? = null
        private var buyMarket: Market? = null
        private var baseAmount: Double = 0.0
        private var marketAmount: Double = 0.0
        private var sellPairName: String
        var buyPairName: String
        var success: Boolean = false

        init {
            bidPrice = pair.bid
            askPrice = pair.ask
            sellMarket = marketsMap[pair.bidMarketName]
            buyMarket = marketsMap[pair.askMarketName]
            baseAmount = askBaseAmount / askPrice
            marketAmount = bidMarketAmount
            quantity = countMinQuantity(pair.bidQuantity, pair.askQuantity)
            sellPairName = pair.getPairNameForMarket(sellMarket!!.getMarketName())
            buyPairName = pair.getPairNameForMarket(buyMarket!!.getMarketName())
        }

        private fun countMinQuantity(bidQuantity: Double, askQuantity: Double): Double {
            val minAvailableAmount = if (baseAmount < marketAmount) baseAmount else marketAmount
            var quantity = if (bidQuantity < askQuantity) bidQuantity else askQuantity
            if (quantity > minAvailableAmount) quantity = minAvailableAmount
            //-1% for trading fee
            quantity -= quantity / 100
            val minBaseAmount = when(pair.baseName) {
                "BTC" -> minBtcAmount
                "ETH" -> minEthAmount
                "USDT" -> minUsdtAmount
                else -> 0.0
            }
            val minMarketAmount = when(pair.marketName) {
                "BTC" -> minBtcAmount
                "ETH" -> minEthAmount
                "USDT" -> minUsdtAmount
                else -> 0.0
            }

            if (quantity * askPrice < minBaseAmount) return 0.0
            if (quantity * bidPrice < minMarketAmount) return 0.0

            return quantity.toBigDecimal().setScale(computeScale(stepSize), RoundingMode.DOWN).toDouble()
        }

        private fun computeScale(testValue: Double): Int {
            var scale = 0
            var testStep = 1.00000000
            while (scale<=8 && testStep!=testValue) {
                testStep /= 10
                scale++
            }
            return scale
        }

        fun buy(): String {
            val price = formatAmount(askPrice)
            try {
                buyMarket!!.buy(buyPairName, price, quantity)
            } catch (e: MarketException) {
                success = false
                return String.format(Locale.US, "%.8f%s bid %.8f; ask %.8f; error: %s",
                        quantity,
                        buyPairName,
                        bidPrice,
                        askPrice,
                        e.message)
            }
            success = true
            return String.format(Locale.getDefault(), "buy %.8f%s for %.8f on %s", quantity, buyPairName,
                    price, buyMarket!!.getMarketName())
        }

        fun sell(): String {
            val price = formatAmount(bidPrice)
            try {
                sellMarket!!.sell(sellPairName, price, quantity)
            } catch (e: MarketException) {
                success = false
                return String.format(Locale.US, "%.8f%s bid %.8f; ask %.8f; error: %s",
                        quantity,
                        buyPairName,
                        bidPrice,
                        askPrice,
                        e.message)
            }
            success = true
            return String.format(Locale.getDefault(), "sell %.8f%s for %.8f on %s", quantity, sellPairName,
                    price, sellMarket!!.getMarketName())
        }

        private fun formatAmount(amount: Double): Double {
            return amount.toBigDecimal().setScale(computeScale(priceFilter), RoundingMode.DOWN).toDouble()
        }

        fun updateInfo(text: String) {
            resultInfo = resultInfo + text + System.lineSeparator()
        }

        fun syncBalance() {
            val coinNames = ArrayList<String>()
            coinNames.add(pair.baseName)
            coinNames.add(pair.marketName)
            val intent = Intent(this@TradingService, BalanceSyncService::class.java)
            intent.putExtra("coinNames", coinNames)
            startService(intent)
        }
    }

    companion object {
        var workingOnPair: String? = null
        private const val CHANNEL_ID = "trading_chanel"
    }
}