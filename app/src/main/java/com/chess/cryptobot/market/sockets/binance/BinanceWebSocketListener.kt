package com.chess.cryptobot.market.sockets.binance

import android.util.Log
import com.chess.cryptobot.exceptions.BinanceException
import com.chess.cryptobot.model.response.binance.BinanceDeserializer
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFrame

class BinanceWebSocketListener(private val binanceWebSocket: BinanceWebSocket) : WebSocketAdapter() {
    private val tag = BinanceWebSocketListener::class.qualifiedName

    override fun onTextMessage(websocket: WebSocket?, message: String?) {
        if (message == null || message.isEmpty()) return
        try {
            val jsonMessage: JsonObject = JsonParser().parse(message).asJsonObject
            checkError(jsonMessage)
            parseMessage(jsonMessage)
        } catch (e: Exception) {
            Log.e(tag, Log.getStackTraceString(e))
        }
    }

    override fun onPingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        websocket?.sendPong()
    }

    private fun parseMessage(jsonMessage: JsonObject) {
        val symbol = jsonMessage.get("s")
        if (symbol != null) {
            var pairName: String? = symbol.asString
            pairName = BinanceDeserializer.symbolToPairName(pairName)
            val bid = jsonMessage["b"].asDouble
            val ask = jsonMessage["a"].asDouble
            binanceWebSocket.passToOrchestrator(pairName, bid, ask)
        }
    }

    @Throws(BinanceException::class)
    fun checkError(jsonMessage: JsonObject) {
        val result = jsonMessage["result"]
        if (result != null && result !is JsonNull) {
            throw BinanceException(result.asString)
        }
    }
}