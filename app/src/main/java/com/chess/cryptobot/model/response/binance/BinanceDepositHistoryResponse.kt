package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class BinanceDepositHistoryResponse : BinanceResponse() {

    @SerializedName("depositList")
    @Expose
    val depositList: List<Deposit>? = null

    inner class Deposit : HistoryResponse {

        @SerializedName("insertTime")
        @Expose
        private val insertTime: Long = 0

        override val historyTime: LocalDateTime
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
}