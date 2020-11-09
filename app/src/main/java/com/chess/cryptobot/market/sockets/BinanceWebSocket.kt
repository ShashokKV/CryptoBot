package com.chess.cryptobot.market.sockets

import com.chess.cryptobot.market.Market.Companion.BINANCE_MARKET
import com.chess.cryptobot.model.websocket.binance.BinanceSubscribe
import com.google.gson.Gson
import java.util.*
import com.chess.cryptobot.model.Pair

class BinanceWebSocket(orchestrator: WebSocketOrchestrator) : MarketWebSocket(orchestrator) {
    override val socketUrl = "wss://stream.binance.com:9443/ws"
    override val marketName: String
        get() = BINANCE_MARKET

    init {
        initWebSocket(initWebSocketListener())
    }

    override fun initWebSocketListener(): BinanceWebSocketListener {
        webSocketListener = BinanceWebSocketListener(this)
        return webSocketListener as BinanceWebSocketListener
    }

    override fun prepareSubscribeMessage(pairs: List<Pair>) {
        subscribeMessage = Gson().toJson(BinanceSubscribe(params = pairs.map {
            pair -> pair.getPairNameForMarket(BINANCE_MARKET).toLowerCase(Locale.ROOT).plus("@bookTicker")
        }, id = 100))
    }

    override fun prepareUnsubscribeMessage(pairs: List<Pair>) {
        unsubscribeMessage = Gson().toJson(BinanceSubscribe(method = "UNSUBSCRIBE", params = pairs.map {
            pair -> pair.getPairNameForMarket(BINANCE_MARKET).toLowerCase(Locale.ROOT).plus("@bookTicker")
        }, id = 100))
    }
}