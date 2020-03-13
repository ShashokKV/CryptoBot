package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.TradeLimitResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val MIN_QTY_FILTER: String = "LOT_SIZE"

class BinanceTradeLimitResponse : BinanceResponse(), TradeLimitResponse {
    @SerializedName("symbols")
    @Expose
    private val symbols: List<Symbols>? = null

    override fun getTradeLimitByName(pairName: String?): Double? {
        if (symbols == null) return null
        for (symbol in symbols) {
            if (symbol.symbol.equals(pairName)) {
                if (symbol.filters == null) return null
                for (filter in symbol.filters) {
                    if (filter.filterType.equals(MIN_QTY_FILTER)) {
                        return filter.minQty
                    }
                }
            }
        }
        return null
    }

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
        }
    }
}