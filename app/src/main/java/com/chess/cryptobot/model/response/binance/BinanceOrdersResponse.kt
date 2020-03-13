package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class BinanceOrdersResponse : BinanceResponse(), HistoryResponse {
        @SerializedName("symbol")
        @Expose
        var symbol: String? = null
        @SerializedName("side")
        @Expose
        override var historyAction: String? = null
        @SerializedName("time")
        @Expose
        var time: Long = 0
        @SerializedName("price")
        @Expose
        override var historyPrice: Double = 0.0
        @SerializedName("origQty")
        @Expose
        override var historyAmount: Double = 0.0
        @SerializedName("executedQty")
        @Expose
        var executedQuantity: Double = 0.0

        override val historyTime: LocalDateTime
            get() =longToTime(time)

        override val historyName: String
            get() {
                return symbolToPairName(symbol)
            }

        override val historyMarket = "binance"

        override val progress: Int
            get() {
                if (historyAmount == 0.0) return 0
                return (((historyAmount - executedQuantity) / (historyAmount)) * 100).toInt()
            }

}