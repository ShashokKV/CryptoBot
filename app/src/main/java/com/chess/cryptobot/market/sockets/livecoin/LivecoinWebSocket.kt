package com.chess.cryptobot.market.sockets.livecoin

import android.util.Log
import com.chess.cryptobot.market.Market.Companion.POLONIEX_MARKET
import com.chess.cryptobot.market.sockets.MarketWebSocket
import com.chess.cryptobot.market.sockets.WebSocketOrchestrator
import com.chess.cryptobot.market.sockets.livecoin.proto.LcWsApi.*
import com.chess.cryptobot.model.Pair
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketState
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class LivecoinWebSocket(orchestrator: WebSocketOrchestrator) : MarketWebSocket(orchestrator) {
    private val tag = LivecoinWebSocket::class.qualifiedName
    override val socketUrl = "wss://ws.api.livecoin.net/ws/beta2"
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
        webSocket.addListener(LivecoinWebSocketListener(this))
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
                subscribe(pair.getPairNameForMarket(marketName))
            }
        }
    }

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun subscribe(symbol: String) {
        val builder = SubscribeTickerChannelRequest
                .newBuilder()
                .setCurrencyPair(symbol)
        val msg = builder.build().toByteString()
        val meta = WsRequestMetaData
                .newBuilder()
                .setRequestType(WsRequestMetaData.WsRequestMsgType.SUBSCRIBE_TICKER)
                .build()
        val request = WsRequest.newBuilder().setMeta(meta).setMsg(msg).build()
        webSocket?.sendBinary(request.toByteArray())
    }

    override fun disconnect() {
        webSocket?.disconnect()
        webSocket = null
    }
}