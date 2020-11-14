package com.chess.cryptobot.market.sockets.binance

import android.util.Log
import com.chess.cryptobot.market.Market.Companion.BINANCE_MARKET
import com.chess.cryptobot.market.sockets.MarketWebSocket
import com.chess.cryptobot.market.sockets.WebSocketOrchestrator
import com.google.gson.Gson
import java.util.*
import com.chess.cryptobot.model.Pair
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class BinanceWebSocket(orchestrator: WebSocketOrchestrator): MarketWebSocket(orchestrator)  {
    override val socketUrl = "wss://stream.binance.com:9443/ws"
    private var webSocket: WebSocket?
    private lateinit var subscribeMessage: String
    override val isConnected: Boolean
        get() = webSocket?.isOpen?:false
    override val marketName: String
        get() = BINANCE_MARKET

    init {
        webSocket = createWebSocket()
    }

    private fun createWebSocket(): WebSocket {
        val webSocket = WebSocketFactory().createSocket(socketUrl, 10000)
        webSocket.addListener(BinanceWebSocketListener(this))
        return webSocket
    }

    override fun connect() {
        if (webSocket==null) webSocket = createWebSocket()
        val es: ExecutorService = Executors.newSingleThreadExecutor()
        val future: Future<WebSocket> = webSocket!!.connect(es)
        try{
            future.get()
        }catch (e: ExecutionException) {
            Log.e("future error", e.message?:e.stackTraceToString(), e)
        }
    }

    override fun subscribe(pairs: List<Pair>) {
        if (isConnected) {
            prepareSubscribeMessage(pairs)
            webSocket?.sendText(subscribeMessage)
        }
    }

    private fun prepareSubscribeMessage(pairs: List<Pair>) {
        subscribeMessage = Gson().toJson(BinanceSubscribe(params = pairs.map {
            pair -> pair.getPairNameForMarket(BINANCE_MARKET).toLowerCase(Locale.ROOT).plus("@bookTicker")
        }, id = 100))
    }

    override fun disconnect() {
        webSocket?.disconnect()
        webSocket = null
    }
}