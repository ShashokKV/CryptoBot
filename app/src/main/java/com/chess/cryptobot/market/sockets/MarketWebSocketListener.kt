package com.chess.cryptobot.market.sockets

import com.chess.cryptobot.exceptions.MarketException
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketFrame

abstract class MarketWebSocketListener: WebSocketAdapter() {
    override fun onTextMessage(websocket: WebSocket?, message: String?) {
        try {
            checkError(message)
            parseMessage(message)
        } catch (e: Exception) {
            websocket?.disconnect()
        }
    }

    override fun onPingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
        websocket?.sendPong()
    }

    abstract fun parseMessage(message: String?)

    @Throws(MarketException::class)
    abstract fun checkError(message: String?)
}