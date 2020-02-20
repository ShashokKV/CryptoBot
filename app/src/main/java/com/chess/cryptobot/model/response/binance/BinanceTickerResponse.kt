package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.TickerResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BinanceTickerResponse : BinanceResponse(), TickerResponse {
    @SerializedName("symbol")
    @Expose
    private val symbol: String? = null
    @SerializedName("best_bid")
    @Expose
    override val tickerBid: Double? = null
    @SerializedName("best_ask")
    @Expose
    override val tickerAsk: Double? = null
    @SerializedName("volume")
    @Expose
    override val volume: Double = 0.0

    override val marketName: String
        get() {
            val split = symbol!!.split("/").toTypedArray()
            return split[1] + "/" + split[0]
        }

}