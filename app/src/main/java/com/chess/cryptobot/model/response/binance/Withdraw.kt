package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.chess.cryptobot.model.response.binance.BinanceResponse.Companion.longToTime
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime

class Withdraw : HistoryResponse {

    @SerializedName("applyTime")
    @Expose
    private val applyTime: Long = 0

    override val historyTime: ZonedDateTime
        get() = longToTime(applyTime)

    @SerializedName("coin")
    @Expose
    override val historyName: String? = null

    override val historyMarket = "binance"

    @SerializedName("amount")
    @Expose
    override val historyAmount: Double = 0.0

    override val historyPrice: Double? = null

    override val historyAction = "withdraw"

    override val progress = 0
}