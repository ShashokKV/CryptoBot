package com.chess.cryptobot.market.sockets

import com.chess.cryptobot.model.Pair

abstract class MarketWebSocket(private val orchestrator: WebSocketOrchestrator) {
    abstract val socketUrl: String
    abstract val marketName: String
    var isSubscribed: Boolean = false
    var isConnected: Boolean = false

    fun passToOrchestrator(pairName: String, bid: Double, ask: Double) {
        synchronized(orchestrator) {
            orchestrator.updateBidsMap(pairName, marketName, bid)
            orchestrator.updateAsksMap(pairName, marketName, ask)
            orchestrator.checkPair(pairName)
        }
    }

    abstract fun connect()

    abstract fun disconnect()

    abstract fun subscribe(pairs: List<Pair>)

    abstract fun unsubscribe(pairs: List<Pair>)
}
