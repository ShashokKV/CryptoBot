package com.chess.cryptobot.model.response.bittrex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexLimits: BittrexMarketResponse() {

    @SerializedName("symbol")
    @Expose
    val symbol: String? = null

    @SerializedName("minTradeSize")
    @Expose
    val minTradeSize: Double? = null
}