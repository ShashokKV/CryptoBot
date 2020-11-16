package com.chess.cryptobot.market.sockets

import com.chess.cryptobot.model.Pair

abstract class MarketWebSocket(private val orchestrator: WebSocketOrchestrator) {
    abstract val socketUrl: String
    abstract val marketName: String
    abstract val isConnected: Boolean

    fun passToOrchestrator(pairName: String, bid: Double, ask: Double) {
        synchronized(orchestrator) {
            orchestrator.updateBidsMap(pairName, marketName, bid)
            orchestrator.updateAsksMap(pairName, marketName, ask)
            orchestrator.checkPairs()
        }
    }

    abstract fun connect()

    abstract fun disconnect()

    abstract fun subscribe(pairs: List<Pair>)
}
