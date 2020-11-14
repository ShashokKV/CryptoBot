package com.chess.cryptobot.market.sockets

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.preference.PreferenceManager
import com.chess.cryptobot.R
import com.chess.cryptobot.market.Market.Companion.BINANCE_MARKET
import com.chess.cryptobot.market.Market.Companion.BITTREX_MARKET
import com.chess.cryptobot.market.Market.Companion.LIVECOIN_MARKET
import com.chess.cryptobot.market.sockets.binance.BinanceWebSocket
import com.chess.cryptobot.market.sockets.bittrex.BittrexWebSocket
import com.chess.cryptobot.market.sockets.livecoin.LivecoinWebSocket
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.service.ProfitPairService
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
        webSockets[LIVECOIN_MARKET] = LivecoinWebSocket(this)
    }

    fun subscribeAll() {
        webSockets.values.forEach {
            if (!it.isConnected) it.connect()
        }
        webSockets.values.forEach {
            if (it.isConnected) it.subscribe(pairs)
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

    fun checkPair(pairName: String) {
        val bids = bidsMap[pairName]
        val asks = asksMap[pairName]

        val bid: Double = bids?.values?.maxOrNull() ?: 0.0
        val ask: Double = asks?.values?.minOrNull() ?: Double.MAX_VALUE

        if (((bid - ask) / bid * 100) > minPercent) {
            runServiceByWebSocketSignal(pairName)
        }
    }

    private fun runServiceByWebSocketSignal(pairName: String) {
        if (stopFlag) return
        stopFlag = true
        disconnectAll()
        Log.d("WebSocketOrchestrator", "pairName: $pairName")
        val intent = Intent(context, ProfitPairService::class.java)
        intent.putExtra("pairName", pairName)
        context.startService(intent)
    }

}