package com.chess.cryptobot.market.sockets

import com.chess.cryptobot.model.Pair

abstract class MarketWebSocket(private val orchestrator: WebSocketOrchestrator) {
    abstract val socketUrl: String
    abstract val marketName: String
    abstract val isConnected: Boolean

    fun passToOrchestrator(pairName: String, bid: Double, ask: Double) {
        synchronized(orchestrator) {
            if (bid > 0) orchestrator.updateBidsMap(pairName, marketName, bid)
            if (ask > 0) orchestrator.updateAsksMap(pairName, marketName, ask)
            orchestrator.checkPairs()
        }
    }

    abstract fun connectAndSubscribe(pairs: List<Pair>)

    abstract fun disconnect()
}
