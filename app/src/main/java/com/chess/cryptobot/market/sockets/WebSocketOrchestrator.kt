package com.chess.cryptobot.market.sockets

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.chess.cryptobot.R
import com.chess.cryptobot.market.Market.Companion.BINANCE_MARKET
import com.chess.cryptobot.market.Market.Companion.BITTREX_MARKET
import com.chess.cryptobot.market.Market.Companion.POLONIEX_MARKET
import com.chess.cryptobot.market.sockets.binance.BinanceWebSocket
import com.chess.cryptobot.market.sockets.bittrex.BittrexWebSocket
import com.chess.cryptobot.market.sockets.poloniex.PoloniexWebSocket
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.worker.ProfitPairWorker
import java.util.concurrent.ConcurrentHashMap

class WebSocketOrchestrator(val context: Context, val pairs: MutableList<Pair>) {
    private var minPercent: Float = 0.0f
    private var webSockets = HashMap<String, MarketWebSocket>()
    private val bidsMap = ConcurrentHashMap<String, ConcurrentHashMap<String, Double>>()
    private val asksMap = ConcurrentHashMap<String, ConcurrentHashMap<String, Double>>()
    private var stopFlag = false

    init {
        minPercent = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.min_profit_percent), "3")?.toFloat()
                ?: 3.0f
        webSockets[BINANCE_MARKET] = BinanceWebSocket(this)
        webSockets[BITTREX_MARKET] = BittrexWebSocket(this)
        webSockets[POLONIEX_MARKET] = PoloniexWebSocket(this)
    }

    fun subscribeAll() {
        webSockets.values.parallelStream().forEach {
            if (!it.isConnected) it.connectAndSubscribe(pairs)
        }
        stopFlag = false
    }

    fun disconnectAll() {
        webSockets.values.forEach {
            if (it.isConnected) it.disconnect()
        }
        bidsMap.clear()
        asksMap.clear()
    }

    fun updateBidsMap(pairName: String, marketName: String, bid: Double) {
        var marketsMap = bidsMap[pairName]
        if (marketsMap == null) {
            marketsMap = ConcurrentHashMap()
        }
        marketsMap[marketName] = bid
        bidsMap[pairName] = marketsMap
    }

    fun updateAsksMap(pairName: String, marketName: String, ask: Double) {
        var marketsMap = asksMap[pairName]
        if (marketsMap == null) {
            marketsMap = ConcurrentHashMap()
        }
        marketsMap[marketName] = ask
        asksMap[pairName] = marketsMap
    }

    fun checkPairs() {
        bidsMap.keys.forEach { pairName ->
            val bids = bidsMap[pairName]
            val asks = asksMap[pairName]

            val bid: Double = bids?.values?.maxOrNull() ?: 0.0
            val ask: Double = asks?.values?.minOrNull() ?: Double.MAX_VALUE

            if (((bid - ask) / bid * 100) > minPercent) {
                runServiceByWebSocketSignal()
            }
        }
    }

    private fun runServiceByWebSocketSignal() {
        if (stopFlag) return
        stopFlag = true
        disconnectAll()

        val profitPairWorker = OneTimeWorkRequest.Builder(ProfitPairWorker::class.java).build()
        WorkManager.getInstance(context).enqueue(profitPairWorker)
    }

}