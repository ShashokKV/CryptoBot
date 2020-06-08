package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.*

class LivecoinHistoryResponse : LivecoinResponse(), HistoryResponse {
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
    override val historyTime: ZonedDateTime?
        get() = ZonedDateTime.ofLocal(LocalDateTime.ofEpochSecond((date!! / 1000.toFloat()).toLong(), 0,
                ZoneOffset.systemDefault().rules.getOffset(Instant.now())), ZoneId.of("Z"), ZoneOffset.UTC)

    override val historyName: String?
        get() {
            return if ((fixedCurrency == taxCurrency)) fixedCurrency else "$taxCurrency/$fixedCurrency"
        }

    override val historyMarket: String
        get() {
            return "livecoin"
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