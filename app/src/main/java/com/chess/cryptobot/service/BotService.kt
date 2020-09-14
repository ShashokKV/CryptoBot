package com.chess.cryptobot.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.content.pairs.AllPairsPreferences
import com.chess.cryptobot.enricher.PairResponseEnricher
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.OrderBookResponse
import com.chess.cryptobot.model.response.TradeLimitResponse
import com.chess.cryptobot.model.response.binance.BinanceResponse
import com.chess.cryptobot.util.CoinInfo
import com.chess.cryptobot.view.notification.NotificationBuilder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class BotService : Service() {
    private var botTimer: Timer? = null
    private var runPeriod: Int = 5
    private var minPercent: Float = 3.0F
    private val botBinder: IBinder = BotBinder()
    private var autoTrade = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Toast.makeText(this, "Bot starting", Toast.LENGTH_SHORT).show()
        initFields()
        startTimer()
        startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification())
        isRunning = true
        return START_STICKY
    }

    private fun initFields() {
        botTimer = Timer()
        initFieldsFromPrefs()
    }

    private fun initFieldsFromPrefs() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        minPercent = preferences.getString(getString(R.string.min_profit_percent), "3")?.toFloat()
                ?: 3.0f
        runPeriod = preferences.getString(getString(R.string.service_run_period), "5")?.toInt() ?: 5
        autoTrade = preferences.getBoolean(getString(R.string.auto_trade), false)
    }

    private fun startTimer() {
        val marketFactory = MarketFactory()
        val markets = marketFactory.getMarkets(this, PreferenceManager.getDefaultSharedPreferences(this))
        runTimer(markets)
    }

    private fun runTimer(markets: List<Market?>) {
        val period = runPeriod * 1000 * 60.toLong()
        val botTimerTask = BotTimerTask(markets)
        botTimer!!.scheduleAtFixedRate(botTimerTask, period, period)
        Log.d(TAG, "timer started")
    }

    private fun buildForegroundNotification(): Notification {
        return NotificationBuilder(this)
                .setTitle("Bot is running")
                .setImportance(NotificationManager.IMPORTANCE_LOW)
                .setChannelName(applicationContext.getString(R.string.foreground_channel_name))
                .setChannelId(FOREGROUND_CHANNEL_ID)
                .setNotificationId(FOREGROUND_NOTIFICATION_ID)
                .setColor(R.color.colorPrimary)
                .setExtraFlag("openPairs")
                .build()
    }

    fun update() {
        if (botTimer != null) {
            botTimer!!.cancel()
            botTimer!!.purge()
        }
        initFields()
        startTimer()
    }

    override fun onDestroy() {
        Toast.makeText(this, "Bot stopping", Toast.LENGTH_SHORT).show()
        if (botTimer != null) botTimer!!.cancel()
        stopForeground(true)
        isRunning = false
        Log.d(TAG, "timer stopped")
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return botBinder
    }

    private inner class BotTimerTask(private val markets: List<Market?>) : TimerTask() {
        private var pairs: MutableList<Pair>? = null
        private val tradeLimits: MutableMap<String, TradeLimitResponse?> = ConcurrentHashMap(3)
        private var coinInfo: CoinInfo? = null
        override fun run() {
            Log.d(TAG, "timer running")
            initPairsFromPrefs()
            if (!autoTrade && isNotificationShown) {
                Log.d(TAG, "notification shown, do nothing")
                return
            }
            try {
                coinInfo = CoinInfo(markets)
                if (autoTrade) {
                    initMinQuantities()
                }
            } catch (e: MarketException) {
                if (!isNotificationShown) makeNotification("Init min quantities exception", e.message)
                return
            }
            val profitPairs = getProfitPairs(pairs, markets)
            if (profitPairs.isNotEmpty() && !autoTrade) {
                makeNotification("Profitable pairs found", getNotificationText(profitPairs))
            }
        }

        private fun initPairsFromPrefs() {
            val coinNames = BalancePreferences(this@BotService).items
            val allPairNames = AllPairsPreferences(this@BotService).items
            pairs = ArrayList()
            if (coinNames != null) {
                for (baseName in coinNames) {
                    for (marketName in coinNames) {
                        if (baseName != marketName) {
                            val pair = Pair(baseName, marketName)
                            val pairName = pair.name
                            if (allPairNames != null) {
                                if (allPairNames.contains(pairName)) {
                                    (pairs as ArrayList<Pair>).add(pair)
                                }
                            }
                        }
                    }
                }
            }
        }

        @Synchronized
        @Throws(MarketException::class)
        private fun initMinQuantities() {
            var exception: MarketException? = null
            markets.parallelStream().forEach { market ->
                var minQuantity: TradeLimitResponse? = null
                try {
                    minQuantity = market!!.getMinQuantity()
                } catch (e: MarketException) {
                    exception = e
                }
                if (minQuantity != null) tradeLimits[market!!.getMarketName()] = minQuantity
            }
            if (exception != null) throw exception as MarketException
        }

        @Synchronized
        private fun getProfitPairs(pairs: List<Pair>?, markets: List<Market?>): List<Pair> {
            val profitPairs: MutableList<Pair> = ArrayList()
            for (pair in pairs!!) {
                if (coinInfo!!.checkCoinStatus(pair.baseName) &&
                        coinInfo!!.checkCoinStatus(pair.marketName)) {
                    val profitPair = profitPercentForPair(pair, markets)
                    if (profitPair != null && profitPair.percent > minPercent) {
                        if (autoTrade) beginTrade(profitPair)
                        profitPairs.add(profitPair)
                    }
                }
            }
            return profitPairs
        }

        private fun profitPercentForPair(pair: Pair, markets: List<Market?>): Pair? {
            val enricher = PairResponseEnricher(pair)
            if (isTradingNow(pair.name)) return null
            var isError = false

            markets.parallelStream()
                    .forEach { market ->
                        val response: OrderBookResponse
                        response = try {
                            market!!.getOrderBook(pair.getPairNameForMarket(market.getMarketName()))
                        } catch (e: MarketException) {
                            if (!isNotificationShown) makeNotification("Get order book exception", e.message)
                            isError = true
                            return@forEach
                        }
                        synchronized(enricher) {
                            enricher.enrichWithResponse(response)
                        }
                    }

            if (isError) return null
            return enricher.enrichWithMinPercent(minPercent).pair
        }

        private fun isTradingNow(pairName: String): Boolean {
            for (i in 0..9) {
                if (TradingService.workingOnPair == pairName) {
                    try {
                        Thread.sleep(1000)
                    } catch (ignored: InterruptedException) {
                        return false
                    }
                } else {
                    return false
                }
            }
            return true
        }

        private fun beginTrade(pair: Pair) {
            val intent = Intent(this@BotService, TradingService::class.java)
            intent.putExtra(Pair::class.java.name, pair)
            intent.putExtra("minQuantity", getMinQuantity(pair))
            if (pair.askMarketName == Market.BINANCE_MARKET || pair.bidMarketName == Market.BINANCE_MARKET) {
                intent.putExtra("stepSize", getStepSize(pair))
            }
            startService(intent)
        }

        private fun getMinQuantity(pair: Pair): Double? {
            var resultQuantity: Double? = null
            for (market in markets) {
                val marketName = market!!.getMarketName()
                val response = tradeLimits[marketName]
                if (response != null) {
                    val quantity = response.getTradeLimitByName(pair.getPairNameForMarket(marketName))
                    if (resultQuantity == null) {
                        resultQuantity = quantity
                    } else {
                        if (quantity!! > resultQuantity) {
                            resultQuantity = quantity
                        }
                    }
                }
            }
            return resultQuantity
        }

        private fun getStepSize(pair: Pair): Double? {
            val binanceResponse = tradeLimits[Market.BINANCE_MARKET] as BinanceResponse
            return binanceResponse.getStepSizeByName(pair.getPairNameForMarket(Market.BINANCE_MARKET))
        }

        private val isNotificationShown: Boolean
            get() {
                val notifications = getSystemService(NotificationManager::class.java).activeNotifications
                for (notification in notifications) {
                    if (notification.id == NOTIFICATION_ID) {
                        return true
                    }
                }
                return false
            }

        private fun makeNotification(title: String, text: String?) {
            NotificationBuilder(this@BotService)
                    .setTitle(title)
                    .setImportance(NotificationManager.IMPORTANCE_DEFAULT)
                    .setChannelName(applicationContext.getString(R.string.channel_name))
                    .setChannelId(CHANNEL_ID)
                    .setNotificationId(NOTIFICATION_ID)
                    .setExtraFlag("openPairs")
                    .setNotificationText(text)
                    .buildAndNotify()
        }

        private fun getNotificationText(profitPairs: List<Pair>): String {
            return profitPairs.stream()
                    .map { pair: Pair -> String.format(Locale.getDefault(), "%s - %.2f%s", pair.name, pair.percent, System.lineSeparator()) }
                    .collect(Collectors.joining())
        }

    }

    inner class BotBinder : Binder() {
        val service: BotService
            get() = this@BotService
    }

    companion object {
        private const val NOTIFICATION_ID = 100500
        private const val FOREGROUND_NOTIFICATION_ID = 200500
        var isRunning = false
        private const val CHANNEL_ID = "profit_pairs_channel_id"
        private const val FOREGROUND_CHANNEL_ID = "bot_channel_id"
        private val TAG = BotService::class.java.simpleName
    }
}