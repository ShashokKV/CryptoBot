package com.chess.cryptobot.market.sockets

import android.content.Context
import android.content.Intent
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.service.ProfitPairService

class WebSocketOrchestrator(val context: Context) {
    var webSockets = ArrayList<MarketWebSocket>()
    var pairs = ArrayList<Pair>()
    var pairNames = ArrayList<String>()
    var marketNames = ArrayList<String>()

    fun runServiceByWebSocketSignal() {
        unsubscribe()
        val intent = Intent(context, ProfitPairService::class.java)
        intent.putExtra("pairNames", pairNames.toTypedArray())
        intent.putExtra("marketNames", marketNames.toTypedArray())
        context.startService(intent)
    }

    private fun unsubscribe() {
        webSockets.forEach{webSocket -> webSocket.unsubscribe() }
    }
}