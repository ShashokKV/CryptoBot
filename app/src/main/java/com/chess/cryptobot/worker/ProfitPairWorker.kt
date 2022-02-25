package com.chess.cryptobot.worker

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.*
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.content.pairs.AllPairsPreferences
import com.chess.cryptobot.enricher.PairResponseEnricher
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.MarketClient
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.OrderBookResponse
import com.chess.cryptobot.util.MarketInfoReader
import com.chess.cryptobot.view.notification.NotificationBuilder
import java.util.*
import java.util.stream.Collectors


class ProfitPairWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private lateinit var markets: List<MarketClient?>
    private lateinit var marketInfoReader: MarketInfoReader
    private lateinit var preferences: SharedPreferences
    private var autoTrade = false
    private lateinit var pairs: MutableList<Pair>
    private var minPercent = 0.0f
    private val tag = ProfitPairWorker::class.qualifiedName

    override fun doWork() : Result {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        marketInfoReader = MarketInfoReader(ctx)
        markets = MarketFactory.getInstance(ctx).getMarkets()
        autoTrade = preferences.getBoolean(ctx.getString(R.string.auto_trade), false)
        pairs = initPairsFromPrefs()
        Log.d(tag, "Starting on pairs: " + pairs.map { pair -> pair.name })
        minPercent = preferences.getString(ctx.getString(R.string.min_profit_percent), "3")
        ?.toFloat()?: 3.0f

        val profitPairs = getProfitPairs(pairs, markets)
        if (profitPairs.isNotEmpty() && !autoTrade) {
            makeNotification("Profitable pairs found", getNotificationText(profitPairs))
        }

        return Result.success()
    }

    @Synchronized
    private fun getProfitPairs(pairs: List<Pair>?, markets: List<Market?>): List<Pair> {
        val profitPairs: MutableList<Pair> = ArrayList()
        for (pair in pairs!!) {
            if (marketInfoReader.checkCoinStatus(pair.baseName) &&
                    marketInfoReader.checkCoinStatus(pair.marketName)) {
                val profitPair = profitPercentForPair(pair, markets)
                if (profitPair != null && profitPair.percent > minPercent) {
                    if (autoTrade) beginTrade(profitPair)
                    profitPairs.add(profitPair)
                }
            }
        }
        return profitPairs
    }

    private fun initPairsFromPrefs(): MutableList<Pair> {
        val coinNames = BalancePreferences(ctx).items
        val allPairNames = AllPairsPreferences(ctx).items
        val pairs = ArrayList<Pair>()
        if (coinNames != null) {
            for (baseName in coinNames) {
                for (marketName in coinNames) {
                    if (baseName != marketName) {
                        val pair = Pair(baseName, marketName)
                        val pairName = pair.name
                        if (allPairNames != null) {
                            if (allPairNames.contains(pairName)) {
                                pairs.add(pair)
                            }
                        }
                    }
                }
            }
        }
        return pairs
    }

    private fun profitPercentForPair(pair: Pair, markets: List<Market?>): Pair? {
        val enricher = PairResponseEnricher(pair)
        if (isTradingNow(pair.name)) return null
        var isError = false

        markets.parallelStream()
                .forEach { market ->
                    val response: OrderBookResponse = try {
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
            if (TradingWorker.workingOnPair == pairName) {
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
        val dataBuilder = Data.Builder()
        dataBuilder.putString(Pair::class.java.name, pair.toJson())
        dataBuilder.putDouble("minQuantity", marketInfoReader.getMinQuantity(pair))
        if (pair.askMarketName == Market.BINANCE_MARKET || pair.bidMarketName == Market.BINANCE_MARKET) {
            dataBuilder.putDouble("stepSize", marketInfoReader.getStepSize(pair))
            dataBuilder.putDouble("priceFilter", marketInfoReader.getPriceFilter(pair))
        }

        val tradingWorker = OneTimeWorkRequest.Builder(TradingWorker::class.java)
            .setInputData(dataBuilder.build())
            .build()

        WorkManager.getInstance(ctx).enqueue(tradingWorker)
    }

    private val isNotificationShown: Boolean
        get() {
            val notifications = ctx.getSystemService(NotificationManager::class.java).activeNotifications
            for (notification in notifications) {
                if (notification.id == NOTIFICATION_ID) {
                    return true
                }
            }
            return false
        }

    private fun makeNotification(title: String, text: String?) {
        NotificationBuilder(ctx)
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

    companion object {
        private const val NOTIFICATION_ID = 100500
        private const val CHANNEL_ID = "profit_pairs_channel_id"
    }

}