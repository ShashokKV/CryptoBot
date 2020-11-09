package com.chess.cryptobot.market.sockets

import android.util.Log
import com.chess.cryptobot.model.Pair
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketListener
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

abstract class MarketWebSocket(private val orchestrator: WebSocketOrchestrator) {
    abstract val socketUrl: String
    private lateinit var webSocket: WebSocket
    lateinit var webSocketListener: MarketWebSocketListener
    lateinit var subscribeMessage: String
    lateinit var unsubscribeMessage: String
    abstract val marketName: String
    var isSubscribed = false


    fun initWebSocket(webSocketListener: MarketWebSocketListener): WebSocket {
        webSocket = WebSocketFactory().createSocket(socketUrl, 10000)
        webSocket.addListener(webSocketListener)
        return webSocket
    }

    abstract fun initWebSocketListener(): WebSocketListener

    fun connect() {
        val es: ExecutorService = Executors.newSingleThreadExecutor()
        val future: Future<WebSocket> = webSocket.connect(es)
        try{
            future.get()
        }catch (e: ExecutionException) {
            Log.e("future error", e.message?:e.stackTraceToString(), e)
        }
    }

    fun subscribe(pairs: List<Pair>) {
        if (webSocket.isOpen) {
            prepareSubscribeMessage(pairs)
            webSocket.sendText(subscribeMessage)
            isSubscribed = true
        }
    }

    abstract fun prepareSubscribeMessage(pairs: List<Pair>)

    fun unsubscribe(pairs: List<Pair>) {
        if (webSocket.isOpen) {
            prepareUnsubscribeMessage(pairs)
            webSocket.sendText(unsubscribeMessage)
            isSubscribed = false
        }
    }

    fun passToOrchestrator(pairName: String, bid: Double, ask: Double) {
        synchronized(orchestrator) {
            orchestrator.updateBidsMap(pairName, marketName, bid)
            orchestrator.updateAsksMap(pairName, marketName, ask)
            orchestrator.checkPair(pairName)
        }
    }

    abstract fun prepareUnsubscribeMessage(pairs: List<Pair>)

    fun disconnect() {
        webSocket.disconnect()
        isSubscribed=false
    }

    fun isConnected(): Boolean {
        return webSocket.isOpen
    }
}
