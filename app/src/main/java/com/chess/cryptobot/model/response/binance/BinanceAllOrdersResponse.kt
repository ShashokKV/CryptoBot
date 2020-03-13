package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class BinanceAllOrdersResponse : BinanceResponse(), HistoryResponse {
    @SerializedName("time")
    @Expose
    private val time: Long = 0

    override val historyTime: LocalDateTime
        get() = longToTime(time)

    @SerializedName("symbol")
    @Expose
    private val symbol: String? = null

    override val historyName: String?
        get() = symbolToPairName(symbol)

    override val historyMarket = "binance"

    @SerializedName("origQty")
    @Expose
    override val historyAmount: Double = 0.0

    @SerializedName("price")
    @Expose
    override val historyPrice: Double = 0.0

    @SerializedName("side")
    @Expose
    override val historyAction: String? = null

    override val progress = 0

    @SerializedName("status")
    @Expose
    val status: String? = null
}