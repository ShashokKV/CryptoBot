package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.TickerResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BinanceTickerResponse : BinanceResponse(), TickerResponse {
    @SerializedName("symbol")
    @Expose
    private val symbol: String? = null
    @SerializedName("bidPrice")
    @Expose
    override val tickerBid: Double? = null
    @SerializedName("askPrice")
    @Expose
    override val tickerAsk: Double? = null
    @SerializedName("volume")
    @Expose
    override val volume: Double = 0.0

    override val marketName: String
        get() {
            return symbolToPairName(symbol)
        }
}