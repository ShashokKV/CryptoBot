package com.chess.cryptobot.market.sockets

import com.chess.cryptobot.exceptions.BinanceException
import com.chess.cryptobot.model.response.binance.BinanceDeserializer
import com.jayway.jsonpath.JsonPath

class BinanceWebSocketListener(marketWebSocket: MarketWebSocket): MarketWebSocketListener(marketWebSocket) {

    override fun parseMessage(message: String?) {
        if (message==null || message.isEmpty()) return
        var pairName: String? = JsonPath.read<String>(message, "$.s") ?: return
        pairName = BinanceDeserializer.symbolToPairName(pairName)
        val bid = JsonPath.read<String>(message, "$.b").toDouble()
        val ask = JsonPath.read<String>(message, "$.a").toDouble()
        marketWebSocket.passToOrchestrator(pairName, bid, ask)
    }

    @Throws(BinanceException::class)
    override fun checkError(message: String?) {
        val errorText = JsonPath.read<String?>(message, "$.result")
        if (errorText != null) {
            throw BinanceException(errorText)
        }
    }


}