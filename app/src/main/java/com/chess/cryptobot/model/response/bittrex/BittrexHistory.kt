package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.HistoryResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class BittrexHistory : HistoryResponse, BittrexMarketResponse() {

    @SerializedName("createdAt")
    @Expose
    private val created: String? = null

    @SerializedName("closedAt")
    @Expose
    private val closed: String? = null

    @SerializedName("completedAt")
    @Expose
    private val completed: String? = null

    @SerializedName("updatedAt")
    @Expose
    private val updated: String? = null

    @SerializedName("marketSymbol")
    @Expose
    private val marketSymbol: String? = null

    @SerializedName("currencySymbol")
    @Expose
    private val currencySymbol: String? = null

    @SerializedName("quantity")
    @Expose
    private val quantity: Double? = null

    @SerializedName("limit")
    @Expose
    private val limit: Double = 0.0

    @SerializedName("direction")
    @Expose
    private val direction: String? = null

    @SerializedName("fillQuantity")
    @Expose
    private val fillQuantity: Double? = null

    @SerializedName("confirmations")
    @Expose
    private val confirmations: Int? = null

    override val historyTime: ZonedDateTime?
        get() {
            val timeString = closed ?: created ?: completed ?: updated
            return ZonedDateTime.ofLocal(LocalDateTime.parse(timeString), ZoneId.of("Z"), ZoneOffset.UTC)
        }

    override val historyName: String?
        get() = marketSymbol?.replace("-", "/") ?: currencySymbol

    override val historyMarket: String
        get() = "bittrex"

    override val historyAmount: Double?
        get() = quantity

    override val historyPrice: Double?
        get() = limit

    override val historyAction: String?
        get() {
            if (direction != null) return direction
            return if (confirmations != null) "deposit" else "withdraw"
        }

    override val progress: Int?
        get() {
            if (quantity == null || fillQuantity == null) return 0
            return if (quantity == 0.0) 0 else ((quantity - fillQuantity) / quantity * 100).toInt()
        }
}