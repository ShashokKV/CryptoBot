package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToLong

class BinanceOrdersResponse : BinanceResponse() {
    @SerializedName("data")
    @Expose
    val data: List<Datum>? = null

    inner class Datum : HistoryResponse {
        @SerializedName("currencyPair")
        @Expose
        var currencyPair: String? = null
        @SerializedName("type")
        @Expose
        override var historyAction: String? = null
        @SerializedName("issueTime")
        @Expose
        var issueTime: Long? = null
        @SerializedName("price")
        @Expose
        override var historyPrice: Double? = null
        @SerializedName("quantity")
        @Expose
        override var historyAmount: Double? = null
        @SerializedName("remainingQuantity")
        @Expose
        var remainingQuantity: Double? = null

        override val historyTime: LocalDateTime
            get() = LocalDateTime.ofEpochSecond((issueTime!! / 1000.toFloat()).roundToLong(), 0,
                    ZoneOffset.systemDefault().rules.getOffset(Instant.now()))

        override val historyName: String
            get() {
                val currencies: Array<String> = currencyPair!!.split("/").toTypedArray()
                return currencies[1] + "/" + currencies[0]
            }

        override val historyMarket: String
            get() {
                return "binance"
            }

        override val progress: Int
            get() {
                if (historyAmount == 0.0) return 0
                return java.lang.Double.valueOf(((historyAmount!! - (remainingQuantity)!!) / (historyAmount)!!) * 100).toInt()
            }
    }
}