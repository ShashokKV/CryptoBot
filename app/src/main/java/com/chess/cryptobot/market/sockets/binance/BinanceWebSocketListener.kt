package com.chess.cryptobot.market.sockets.binance

import android.util.Log
import com.chess.cryptobot.exceptions.BinanceException
import com.chess.cryptobot.model.response.binance.BinanceDeserializer
import com.jayway.jsonpath.JsonPath
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFrame

class BinanceWebSocketListener(private val binanceWebSocket: BinanceWebSocket): WebSocketAdapter() {

    override fun onTextMessage(websocket: WebSocket?, message: String?) {
        try {
            checkError(message)
            parseMessage(message)
        } catch (e: Exception) {
            Log.e("WebSocketListener", e.message ?: e.stackTraceToString(), e)
            binanceWebSocket.disconnect()
        }
    }

    override fun onPingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        websocket?.sendPong()
    }

    private fun parseMessage(message: String?) {
        if (message==null || message.isEmpty()) return
        var pairName: String? = JsonPath.read<String>(message, "$.s") ?: return
        pairName = BinanceDeserializer.symbolToPairName(pairName)
        val bid = JsonPath.read<String>(message, "$.b").toDouble()
        val ask = JsonPath.read<String>(message, "$.a").toDouble()
        binanceWebSocket.passToOrchestrator(pairName, bid, ask)
    }

    @Throws(BinanceException::class)
    fun checkError(message: String?) {
        val errorText = JsonPath.read<String?>(message, "$.result")
        if (errorText != null) {
            throw BinanceException(errorText)
        }
    }
}