package com.chess.cryptobot.model.response.binance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Symbols {
    @SerializedName("symbol")
    @Expose
    val symbol: String? = null

    @SerializedName("filters")
    @Expose
    val filters: List<Filter>?  = null

    class Filter {
        @SerializedName("filterType")
        @Expose
        val filterType: String? = null

        @SerializedName("minQty")
        @Expose
        val minQty: Double = 0.0

        @SerializedName("stepSize")
        @Expose
        val stepSize: Double = 0.0
    }
}