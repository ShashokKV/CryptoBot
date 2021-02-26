package com.chess.cryptobot.market.sockets.poloniex

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter

class PoloniexWebSocketListener(private val poloniexWebSocket: PoloniexWebSocket) : WebSocketAdapter() {
    private val tag = PoloniexWebSocketListener::class.qualifiedName
    private val pairIdMap: MutableMap<Int, String> = HashMap()

    override fun onTextMessage(ws: WebSocket?, message: String?) {
        try {
            parseMessage(message)
        } catch (e: Exception) {
            Log.e(tag, e.message ?: e.stackTraceToString(), e)
        }
    }

    private fun parseMessage(message: String?) {
        if (message == null) return
        val response: JsonArray = JsonParser().parse(message).asJsonArray

        val channelId = response[0].asInt
        val orders = response[2].asJsonArray

        orders.forEach { order ->
            val orderObj = order.asJsonArray
            var ask = 0.0
            var bid = 0.0
            if (orderObj[0].asString == "i") {
                val initialDump = orderObj[1].asJsonObject
                pairIdMap[channelId] = initialDump["currencyPair"].asString
                val orderBook = initialDump["orderBook"].asJsonArray
                ask = orderBook[0].asJsonObject.keySet().first().toDouble()
                bid = orderBook[1].asJsonObject.keySet().first().toDouble()
            } else if (orderObj[0].asString == "o") {
                if (orderObj[3].asDouble > 0) {
                    if (orderObj[1].asInt == 0) {
                        ask = orderObj[2].asDouble
                    } else {
                        bid = orderObj[2].asDouble
                    }
                }
            }
            val pairName = pairIdMap[channelId]
            if (pairName!=null && ask+bid>0) {
                poloniexWebSocket.passToOrchestrator(pairName.replace("_", "/"), bid, ask)
            }
        }


    }
}