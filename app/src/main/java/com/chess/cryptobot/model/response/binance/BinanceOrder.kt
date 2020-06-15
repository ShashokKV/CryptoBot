package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime

class BinanceOrder: HistoryResponse {
    @SerializedName("symbol")
    @Expose
    private val symbol: String? = null

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

    @SerializedName("status")
    @Expose
    val status: String? = null

    override val historyTime: ZonedDateTime
        get() = BinanceResponse.longToTime(time)

    override val historyName: String
        get() {
            return BinanceDeserializer.symbolToPairName(symbol)
        }

    override val historyMarket = "binance"

    override val progress: Int
        get() {
            if (historyAmount == 0.0) return 0
            return ((executedQuantity / historyAmount) * 100).toInt()
        }
}