package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.TickerResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BinanceTicker: TickerResponse {
    @SerializedName("symbol")
    @Expose
    private val symbol: String? = null

    @SerializedName("bidPrice")
    @Expose
    override val tickerBid: Double = 0.0

    @SerializedName("askPrice")
    @Expose
    override val tickerAsk: Double = 0.0

    override val tickerName: String
        get() {
            return BinanceDeserializer.symbolToPairName(symbol)
        }
}