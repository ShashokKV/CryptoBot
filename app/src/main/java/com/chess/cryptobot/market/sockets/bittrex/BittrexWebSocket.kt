package com.chess.cryptobot.market.sockets.bittrex

import android.util.Log
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.market.sockets.MarketWebSocket
import com.chess.cryptobot.market.sockets.WebSocketOrchestrator
import com.chess.cryptobot.model.Pair
import com.github.signalr4j.client.ConnectionState
import com.github.signalr4j.client.hubs.HubConnection
import com.github.signalr4j.client.hubs.HubProxy
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.UnsupportedEncodingException
import java.net.ConnectException
import java.nio.charset.Charset
import java.util.*
import java.util.zip.DataFormatException
import java.util.zip.Inflater


class BittrexWebSocket(orchestrator: WebSocketOrchestrator) : MarketWebSocket(orchestrator) {
    private val tag = BittrexWebSocket::class.qualifiedName
    override val socketUrl = "https://socket-v3.bittrex.com/signalr"
    private var hubConnection: HubConnection = HubConnection(socketUrl)
    private var hubProxy: HubProxy = hubConnection.createHubProxy("c3")
    override val isConnected: Boolean
        get() = hubConnection.state == ConnectionState.Connected

    override val marketName: String
        get() {
            return Market.BITTREX_MARKET
        }

    override fun connectAndSubscribe(pairs: List<Pair>) {
        try {
            hubConnection.start().get()
            subscribe(pairs)
        } catch (e: Throwable) {
            Log.e(tag, e.message?:e.stackTraceToString(), e)
        }

    }

    override fun disconnect() {
        try {
            hubConnection.stop()
        } catch (e: ConnectException) {
            Log.e(tag, e.message?:e.stackTraceToString(), e)
        }
    }

    private fun subscribe(pairs: List<Pair>) {
        if (!isConnected) return
        val channels = pairs.map { pair -> "ticker_" + pair.getPairNameForMarket(marketName) }

        val msgHandler = MsgHandler()
        hubProxy.subscribe(msgHandler)
        try {
            val response: Array<SocketResponse> = hubProxy.invoke(Array<SocketResponse>::class.java, "Subscribe", channels).get()
            for (i in channels.indices) {
                if (response[i].Success == true) {
                    Log.d(tag, channels[i] + ": " + "Success")
                } else {
                    Log.e(tag, channels[i] + ": " + response[i].ErrorCode)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, e.message ?: e.stackTraceToString(), e)
        }
    }

    inner class MsgHandler {
        fun ticker(compressedData: String?) {
            try {
                val msg = DataConverter.decodeMessage(compressedData)
                val pairName = msg.get("symbol").asString
                passToOrchestrator(Pair.normalizeFromMarketPairName(pairName, marketName), msg.get("bidRate").asDouble, msg.get("askRate").asDouble)
            } catch (e: Exception) {
                Log.e(tag, "Error decompressing message - $e - $compressedData", e)
            }
        }
    }

    internal object DataConverter {
        @Throws(DataFormatException::class, UnsupportedEncodingException::class)
        fun decodeMessage(encodedData: String?): JsonObject {
            val compressedData = Base64.getDecoder().decode(encodedData)
            val inflater = Inflater(true)
            inflater.setInput(compressedData)
            var buffer = ByteArray(1024)
            val resultBuilder = StringBuilder()
            while (inflater.inflate(buffer) > 0) {
                resultBuilder.append(String(buffer, Charset.forName("UTF-8")))
                buffer = ByteArray(1024)
            }
            inflater.end()
            val text = resultBuilder.toString().trim { it <= ' ' }
            return JsonParser().parse(text).asJsonObject
        }
    }
}