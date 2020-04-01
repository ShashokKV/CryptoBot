package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.chess.cryptobot.model.response.binance.BinanceResponse.Companion.longToTime
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class Withdraw : HistoryResponse {

    @SerializedName("applyTime")
    @Expose
    private val applyTime: Long = 0

    override val historyTime: LocalDateTime
        get() = longToTime(applyTime)

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