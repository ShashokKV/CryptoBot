package com.chess.cryptobot.service

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import androidx.preference.PreferenceManager
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.view.notification.NotificationBuilder
import com.chess.cryptobot.view.notification.NotificationID
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.HashMap

class TradingService : IntentService("TradingService") {
    private var resultInfo = ""
    private lateinit var pair: Pair
    private var marketsMap = HashMap<String, Market>()
    private var bidBaseAmount: Double? = null
    private var bidMarketAmount: Double? = null
    private var askBaseAmount: Double? = null
    private var askMarketAmount: Double? = null
    private var minMarketQuantity: Double = 0.0
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
        if (trader.quantity!! <= minMarketQuantity) return
        try {
            trader.buy()
            trader.sell()
        } catch (e: MarketException) {
            trader.updateInfo(String.format(Locale.US, "%.8f%s bid %.8f; ask %.8f; error: %s",
                    trader.quantity,
                    trader.buyPairName,
                    trader.bidPrice,
                    trader.askPrice,
                    e.message))
            makeNotification("Trade exception", resultInfo)
            return
        }
        makeNotification("Trading results", resultInfo)
        trader.syncBalance()
    }

    override fun onDestroy() {
        super.onDestroy()
        workingOnPair = null
    }

    private fun initFromIntent(intent: Intent) {
        pair = intent.getSerializableExtra(Pair::class.java.name) as Pair
        workingOnPair = pair.name
        minMarketQuantity = intent.getDoubleExtra("minQuantity", 0.0)
    }

    private fun initMarkets() {
        MarketFactory().getMarkets(this,
                PreferenceManager.getDefaultSharedPreferences(this))
                .forEach { market ->  if (market!=null) marketsMap[market.getMarketName()] = market }
    }

    private val isApiKeysEmpty: Boolean
        get() = marketsMap.values.stream().allMatch { market: Market? -> market?.keysIsEmpty()?:false }

    @Throws(MarketException::class)
    private fun initAmounts() {
        bidBaseAmount = marketsMap[pair.bidMarketName]?.getAmount(pair.baseName)
        askBaseAmount = marketsMap[pair.askMarketName]?.getAmount(pair.baseName)
        bidMarketAmount = marketsMap[pair.bidMarketName]?.getAmount(pair.marketName)
        askMarketAmount = marketsMap[pair.askMarketName]?.getAmount(pair.marketName)
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

    private inner class Trader internal constructor(pair: Pair) {
        var bidPrice: Double? = null
        var askPrice: Double? = null
        var quantity: Double? = null
        private var sellMarket: Market? = null
        private var buyMarket: Market? = null
        private var baseAmount: Double? = null
        private var marketAmount: Double? = null
        private var sellPairName: String? = null
        var buyPairName: String? = null

        private fun countMinQuantity(bidQuantity: Double?, askQuantity: Double?): Double? {
            val minAvailableAmount = if (baseAmount!! < marketAmount!!) baseAmount else marketAmount
            quantity = if (bidQuantity!! < askQuantity!!) bidQuantity else askQuantity
            if (quantity!! > minAvailableAmount!!) quantity = minAvailableAmount
            //-1% for trading fee
            quantity = formatAmount(quantity!! - quantity!! / 100)
            if (pair.baseName == "BTC") {
                if (quantity!! * askPrice!! < minBtcAmount) quantity = 0.0
            } else if (pair.baseName == "ETH") {
                if (quantity!! * askPrice!! < minEthAmount) quantity = 0.0
            }
            return quantity
        }

        @Throws(MarketException::class)
        fun buy() {
            val price = formatAmount(askPrice)
            buyMarket!!.buy(buyPairName!!, price, quantity!!)
            updateInfo(String.format(Locale.getDefault(), "buy %.8f%s for %.8f on %s", quantity, buyPairName,
                    price, buyMarket!!.getMarketName()))
        }

        @Throws(MarketException::class)
        fun sell() {
            val price = formatAmount(bidPrice)
            sellMarket!!.sell(sellPairName!!, price, quantity!!)
            updateInfo(String.format(Locale.getDefault(), "sell %.8f%s for %.8f on %s", quantity, sellPairName,
                    price, sellMarket!!.getMarketName()))
        }

        private fun formatAmount(amount: Double?): Double {
            val bd = BigDecimal(amount!!).setScale(8, RoundingMode.HALF_UP)
            return bd.toDouble()
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

        init {
                bidPrice = pair.bid
                askPrice = pair.ask
                sellMarket = marketsMap[pair.bidMarketName]
                buyMarket = marketsMap[pair.askMarketName]
                baseAmount = marketsMap[pair.askMarketName]?.getAmount(pair.baseName)?:0.0 / askPrice!!
                marketAmount = marketsMap[pair.bidMarketName]?.getAmount(pair.marketName)?:0.0
                quantity = countMinQuantity(pair.bidQuantity, pair.askQuantity)
                sellPairName = pair.getPairNameForMarket(sellMarket!!.getMarketName())
                buyPairName = pair.getPairNameForMarket(buyMarket!!.getMarketName())
        }
    }

    companion object {
        var workingOnPair: String? = null
        private const val CHANNEL_ID = "trading_chanel"
        private const val minBtcAmount = 0.0005
        private const val minEthAmount = 0.025
    }
}