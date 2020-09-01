package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.TickerResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexTicker : TickerResponse, BittrexMarketResponse() {
    @SerializedName("symbol")
    @Expose
    val pairName: String? = null
    @SerializedName("bidRate")
    @Expose
    override val tickerBid: Double = 0.0
    @SerializedName("askRate")
    @Expose
    override val tickerAsk: Double = 0.0

    override val tickerName: String
        get() = pairName?.replace("-", "/") ?: ""


}