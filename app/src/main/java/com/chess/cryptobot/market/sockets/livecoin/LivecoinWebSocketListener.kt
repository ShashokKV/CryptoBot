package com.chess.cryptobot.market.sockets.livecoin

import android.util.Log
import com.chess.cryptobot.market.Market.Companion.LIVECOIN_MARKET
import com.chess.cryptobot.market.sockets.livecoin.proto.LcWsApi.*
import com.chess.cryptobot.model.Pair
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import java.util.function.Consumer

class LivecoinWebSocketListener(private val livecoinWebSocket: LivecoinWebSocket): WebSocketAdapter() {

    override fun onBinaryMessage(ws: WebSocket?, binary: ByteArray?) {
        try {
            parseMessage(binary)
        } catch (e: Exception) {
            Log.e("LivecoinWebSocketListener", e.message?:e.stackTraceToString(), e)
        }
    }

    private fun parseMessage(data: ByteArray?) {
        val response: WsResponse = WsResponse.parseFrom(data)
       if (response.meta.responseType == WsResponseMetaData.WsResponseMsgType.TICKER_NOTIFY) {
            val message = TickerNotification.parseFrom(response.msg)
            message.dataList.forEach(Consumer { t: TickerEvent? ->
                val bid = t?.bestBid?.toDouble()?: Double.MAX_VALUE
                val ask = t?.bestAsk?.toDouble()?:0.0
                val pair = Pair.normalizeFromMarketPairName(message.currencyPair, LIVECOIN_MARKET)
                Log.d("LivecoinWebSocketListener", "pair: $pair, bid: $bid, ask: $ask")
                livecoinWebSocket.passToOrchestrator(pair, bid, ask)
            })
        } else if (response.meta.responseType == WsResponseMetaData.WsResponseMsgType.ERROR) {
            val message = ErrorResponse.parseFrom(response.msg)
           Log.e("LivecoinWebSocketListener", message.message)
        }
    }
}