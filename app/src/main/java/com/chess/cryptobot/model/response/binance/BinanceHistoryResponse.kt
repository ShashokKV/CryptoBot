package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.roundToLong

class BinanceHistoryResponse : BinanceResponse(), HistoryResponse {
    @SerializedName("type")
    @Expose
    override val historyAction: String? = null
    @SerializedName("date")
    @Expose
    private val date: Long? = null
    @SerializedName("amount")
    @Expose
    override val historyAmount: Double? = null
    @SerializedName("variableAmount")
    @Expose
    private val variableAmount: Double? = null
    @SerializedName("fixedCurrency")
    @Expose
    private val fixedCurrency: String? = null
    @SerializedName("taxCurrency")
    @Expose
    private val taxCurrency: String? = null

    override val historyTime: LocalDateTime
        get() = LocalDateTime.ofEpochSecond((date!! / 1000.toFloat()).roundToLong(), 0,
                ZoneOffset.systemDefault().rules.getOffset(Instant.now()))

    override val historyName: String?
        get() {
            return if ((fixedCurrency == taxCurrency)) fixedCurrency else "$taxCurrency/$fixedCurrency"
        }

    override val historyMarket: String
        get() {
            return "binance"
        }

    override val historyPrice: Double?
        get() {
            return if (variableAmount == null) null else variableAmount / (historyAmount)!!
        }

    override val progress: Int
        get() {
            return 0
        }
}