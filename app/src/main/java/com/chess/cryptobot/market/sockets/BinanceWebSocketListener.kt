package com.chess.cryptobot.market.sockets

import android.util.Log
import com.chess.cryptobot.exceptions.BinanceException
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.binance.BinanceDeserializer
import com.google.gson.JsonParser
import com.jayway.jsonpath.JsonPath
import com.neovisionaries.ws.client.WebSocket
import kotlin.jvm.Throws

class BinanceWebSocketListener: MarketWebSocketListener() {

    override fun onTextMessage(websocket: WebSocket?, message: String?) {
        Log.d("BinanceSocketListener", "onTextMessage: $message")
        super.onTextMessage(websocket, message)
    }

    override fun parseMessage(message: String?) {
        if (message==null || message.isEmpty()) return
        var pairName: String? = JsonPath.read<String>(message, "$.s") ?: return
        pairName = BinanceDeserializer.symbolToPairName(pairName)
        val bid = JsonPath.read<String>(message, "$.b").toDouble()
        val ask = JsonPath.read<String>(message, "$.a").toDouble()

    }

    @Throws(BinanceException::class)
    override fun checkError(message: String?) {
        val errorText = JsonPath.read<String?>(message, "$.result")
        if (errorText != null) {
            throw BinanceException(errorText)
        }
    }


}