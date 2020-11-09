package com.chess.cryptobot.market.sockets.bittrex

import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.sockets.BittrexExample
import com.chess.cryptobot.market.sockets.MarketWebSocket
import com.chess.cryptobot.market.sockets.SocketResponse
import com.chess.cryptobot.market.sockets.WebSocketOrchestrator
import com.chess.cryptobot.model.Pair
import com.github.signalr4j.client.ConnectionState
import com.github.signalr4j.client.hubs.HubConnection
import com.github.signalr4j.client.hubs.HubProxy

class BittrexWebSocket(orchestrator: WebSocketOrchestrator): MarketWebSocket(orchestrator) {
    override val socketUrl = "https://socket-v3.bittrex.com/signalr"
    private var hubConnection: HubConnection
    private var hubProxy: HubProxy

    override val marketName: String
        get() {
            return Market.BITTREX_MARKET
        }

    init {
        hubConnection = HubConnection(socketUrl)
        hubProxy = hubConnection.createHubProxy("c3")
    }

    override fun connect() {
        hubConnection.start().get()
        isConnected = hubConnection.state == ConnectionState.Connected
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun subscribe(pairs: List<Pair>) {
        val channels = pairs.map { pair -> "trade_"+pair.getPairNameForMarket(marketName) }

        val msgHandler: Any = object : Any() {
            fun heartbeat() {
                println("<heartbeat>")
            }

            fun trade(compressedData: String?) {
                // If subscribed to multiple market's trade streams,
                // use the marketSymbol field in the message to differentiate
                BittrexExample.printSocketMessage("Trade", compressedData)
            }

            fun balance(compressedData: String?) {
                BittrexExample.printSocketMessage("Balance", compressedData)
            }
        }

        client.setMessageHandler(msgHandler)
        try {
            val response: Array<SocketResponse> = client.subscribe(channels)
            for (i in channels.indices) {
                println(channels[i] + ": " + if (response[i].Success) "Success" else response[i].ErrorCode)
            }
        } catch (e: Exception) {
            println("Failed to subscribe: $e")
        }
    }

    override fun unsubscribe(pairs: List<Pair>) {
        TODO("Not yet implemented")
    }
}