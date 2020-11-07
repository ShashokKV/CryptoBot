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

abstract class MarketWebSocket {
    abstract val socketUrl: String
    lateinit var webSocket: WebSocket
    lateinit var webSocketListener: MarketWebSocketListener
    lateinit var subscribeMessage: String
    lateinit var unsubscribeMessage: String
    lateinit var pairs: List<Pair>
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
            Log.e("future error", e.message)
        }
    }

    fun subscribe(pairs: List<Pair>) {
        if (webSocket.isOpen) {
            this.pairs = pairs
            prepareSubscribeMessage(pairs)
            webSocket.sendText(subscribeMessage)
            isSubscribed = true
        }
    }

    abstract fun prepareSubscribeMessage(pairs: List<Pair>)

    fun unsubscribe() {
        this.unsubscribe(pairs)
    }


    fun unsubscribe(pairs: List<Pair>) {
        if (webSocket.isOpen) {
            prepareUnsubscribeMessage(pairs)
            webSocket.sendText(unsubscribeMessage)
            isSubscribed = false
        }
    }

    abstract fun prepareUnsubscribeMessage(pairs: List<Pair>)

    fun disconnect() {
        webSocket.disconnect()
    }

    fun isConnected(): Boolean {
        return webSocket.isOpen
    }
}
