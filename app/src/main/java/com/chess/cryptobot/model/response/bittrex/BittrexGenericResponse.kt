package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.CurrenciesResponse
import com.chess.cryptobot.model.response.HistoryResponse
import com.chess.cryptobot.model.response.TickerResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.*

class BittrexGenericResponse : TickerResponse, CurrenciesResponse, HistoryResponse {
    @SerializedName("Available")
    @Expose
    val available: Double = 0.0
    @SerializedName("buy")
    @Expose
    val buy: List<BittrexPrice>? = null
    @SerializedName("sell")
    @Expose
    val sell: List<BittrexPrice>? = null
    @SerializedName("MarketName")
    @Expose
    val pairName: String? = null
    @SerializedName("Bid")
    @Expose
    override val tickerBid: Double? = null
    @SerializedName("Ask")
    @Expose
    override val tickerAsk: Double? = null
    @SerializedName("Volume")
    @Expose
    override val volume: Double = 0.0
    @SerializedName("Currency")
    @Expose
    override val currencyName: String? = null
    @SerializedName("TxFee")
    @Expose
    private val txFee: Double? = null
    @SerializedName("IsActive")
    @Expose
    override val isActive: Boolean? = null
        get() = field ?: false
    @SerializedName("Address")
    @Expose
    val address: String? = null
    @SerializedName("MinTradeSize")
    @Expose
    val minTradeSize: Double? = null
    @SerializedName("Exchange")
    @Expose
    private val exchange: String? = null
    @SerializedName("Closed")
    @Expose
    private val closed: String? = null
    @SerializedName("OrderType")
    @Expose
    private val orderType: String? = null
    @SerializedName("Quantity")
    @Expose
    private val quantity: Double? = null
    @SerializedName("QuantityRemaining")
    @Expose
    private val quantityRemaining: Double? = null
    @SerializedName("PricePerUnit")
    @Expose
    private val pricePerUnit: Double? = null
    @SerializedName("Limit")
    @Expose
    private val limit: Double? = null
    @SerializedName("Amount")
    @Expose
    private val amount: Double? = null
    @SerializedName("Opened")
    @Expose
    private val opened: String? = null
    @SerializedName("LastUpdated")
    @Expose
    private val lastUpdated: String? = null
    @SerializedName("MarketCurrency")
    @Expose
    val marketCurrency: String? = null
    @SerializedName("LogoUrl")
    @Expose
    val logoUrl: String? = null

    override val marketName: String
        get() = pairName?.replace("-", "/") ?: ""

    override val fee: Double
        get() = txFee ?: 0.0

    override val historyTime: LocalDateTime
        get() {
            var timeString = closed ?: opened
            timeString = timeString ?: lastUpdated
            return LocalDateTime.parse(timeString).plusHours(3)
        }

    override val historyName: String
        get() = currencyName ?: exchange!!.replace("-", "/")

    override val historyMarket: String
        get() = "bittrex"

    override val historyAmount: Double?
        get() = quantity ?: amount

    override val historyPrice: Double?
        get() = pricePerUnit ?: limit

    override val historyAction: String
        get() = orderType?.toLowerCase(Locale.getDefault())?.replace("limit_", "")
                ?: if (lastUpdated == null) "withdraw" else "deposit"

    override val progress: Int
        get() {
            if (quantity == null || quantityRemaining == null) return 0
            return if (quantity == 0.0) 0 else ((quantity - quantityRemaining) / quantity * 100).toInt()
        }

}