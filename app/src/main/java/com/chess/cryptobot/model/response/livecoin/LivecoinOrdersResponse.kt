package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.*
import kotlin.math.roundToLong

class LivecoinOrdersResponse : LivecoinResponse() {
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
        override val historyTime: ZonedDateTime?
            get() = ZonedDateTime.ofLocal(LocalDateTime.ofEpochSecond((issueTime!! / 1000.toFloat()).roundToLong(),
                    0,
                    ZoneOffset.UTC),
                    ZoneId.of("Z"), ZoneOffset.UTC)

        override val historyName: String
            get() {
                val currencies: Array<String> = currencyPair!!.split("/".toRegex()).toTypedArray()
                return currencies[1] + "/" + currencies[0]
            }

        override val historyMarket: String
            get() {
                return "livecoin"
            }

        override val progress: Int
            get() {
                if (historyAmount == 0.0) return 0
                return (((historyAmount!! - (remainingQuantity)!!) / (historyAmount)!!) * 100).toInt()
            }
    }
}