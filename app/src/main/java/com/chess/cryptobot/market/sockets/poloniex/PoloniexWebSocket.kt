package com.chess.cryptobot.market.sockets.poloniex

import android.util.Log
import com.chess.cryptobot.market.Market.Companion.POLONIEX_MARKET
import com.chess.cryptobot.market.sockets.MarketWebSocket
import com.chess.cryptobot.market.sockets.WebSocketOrchestrator
import com.chess.cryptobot.model.Pair
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketState
import org.json.JSONObject
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class PoloniexWebSocket(orchestrator: WebSocketOrchestrator) : MarketWebSocket(orchestrator) {
    private val tag = PoloniexWebSocket::class.qualifiedName
    override val socketUrl = "wss://api2.poloniex.com"
    private var webSocket: WebSocket?
    override val isConnected: Boolean
        get() = webSocket?.isOpen ?: false

    override val marketName: String
        get() = POLONIEX_MARKET

    init {
        webSocket = createWebSocket()
    }

    private fun createWebSocket(): WebSocket {
        val webSocket = WebSocketFactory().createSocket(socketUrl, 10000)
        webSocket.addListener(PoloniexWebSocketListener(this))
        return webSocket
    }

    override fun connectAndSubscribe(pairs: List<Pair>) {
        if (webSocket == null) webSocket = createWebSocket()
        if (webSocket!!.state != WebSocketState.CREATED) {
            webSocket = webSocket!!.recreate()
        }
        val es: ExecutorService = Executors.newSingleThreadExecutor()

        val future: Future<WebSocket> = webSocket!!.connect(es)
        try {
            future.get()
            Log.d(tag, "CONNECTED")
            subscribe(pairs)
        } catch (e: ExecutionException) {
            Log.e(tag, e.message ?: e.stackTraceToString(), e)
        }
    }

    private fun subscribe(pairs: List<Pair>) {
        if (isConnected) {
            pairs.forEach { pair ->
                val request = JSONObject()
                request.put("command", "subscribe")
                request.put("channel",  pair.getPairNameForMarket(POLONIEX_MARKET))

                webSocket?.sendText(request.toString())
            }
        }
    }

    override fun disconnect() {
        webSocket?.disconnect()
        webSocket = null
    }
}