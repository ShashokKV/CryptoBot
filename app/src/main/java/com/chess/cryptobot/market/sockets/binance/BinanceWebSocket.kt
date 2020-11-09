package com.chess.cryptobot.market.sockets.binance

import android.util.Log
import com.chess.cryptobot.market.Market.Companion.BINANCE_MARKET
import com.chess.cryptobot.market.sockets.MarketWebSocket
import com.chess.cryptobot.market.sockets.WebSocketOrchestrator
import com.chess.cryptobot.model.websocket.binance.BinanceSubscribe
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
    private var webSocket: WebSocket
    private lateinit var subscribeMessage: String
    private lateinit var unsubscribeMessage: String
    override val marketName: String
        get() = BINANCE_MARKET

    init {
        webSocket = WebSocketFactory().createSocket(socketUrl, 10000)
        webSocket.addListener(BinanceWebSocketListener(this))
    }

    override fun connect() {
        val es: ExecutorService = Executors.newSingleThreadExecutor()
        val future: Future<WebSocket> = webSocket.connect(es)
        try{
            future.get()
        }catch (e: ExecutionException) {
            Log.e("future error", e.message?:e.stackTraceToString(), e)
        }
        isConnected = webSocket.isOpen
    }

    override fun subscribe(pairs: List<Pair>) {
        if (webSocket.isOpen) {
            prepareSubscribeMessage(pairs)
            webSocket.sendText(subscribeMessage)
            isSubscribed = true
        }
    }

    override fun unsubscribe(pairs: List<Pair>) {
        if (webSocket.isOpen) {
            prepareUnsubscribeMessage(pairs)
            webSocket.sendText(unsubscribeMessage)
            isSubscribed = false
        }
    }

    private fun prepareSubscribeMessage(pairs: List<Pair>) {
        subscribeMessage = Gson().toJson(BinanceSubscribe(params = pairs.map {
            pair -> pair.getPairNameForMarket(BINANCE_MARKET).toLowerCase(Locale.ROOT).plus("@bookTicker")
        }, id = 100))
    }

    private fun prepareUnsubscribeMessage(pairs: List<Pair>) {
        unsubscribeMessage = Gson().toJson(BinanceSubscribe(method = "UNSUBSCRIBE", params = pairs.map {
            pair -> pair.getPairNameForMarket(BINANCE_MARKET).toLowerCase(Locale.ROOT).plus("@bookTicker")
        }, id = 100))
    }

    override fun disconnect() {
        webSocket.disconnect()
        isSubscribed=false
    }
}