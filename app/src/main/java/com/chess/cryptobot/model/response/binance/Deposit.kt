package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.chess.cryptobot.model.response.binance.BinanceResponse.Companion.longToTime
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.ZonedDateTime

class Deposit : HistoryResponse {

    @SerializedName("insertTime")
    @Expose
    private val insertTime: Long = 0

    override val historyTime: ZonedDateTime
        get() = longToTime(insertTime)

    @SerializedName("asset")
    @Expose
    override val historyName: String? = null

    override val historyMarket = "binance"

    @SerializedName("amount")
    @Expose
    override val historyAmount: Double = 0.0

    override val historyPrice: Double = 0.0

    override val historyAction = "deposit"

    override val progress = 0
}