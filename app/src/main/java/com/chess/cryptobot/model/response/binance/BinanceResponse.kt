package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.*
import kotlin.math.roundToLong

const val MIN_QTY_FILTER: String = "LOT_SIZE"

open class BinanceResponse : MarketResponse, TickerResponse, HistoryResponse, CurrenciesListResponse,
        AddressResponse, OrderBookResponse, TradeLimitResponse {
    @SerializedName("success")
    @Expose
    private var success: Boolean? = null

    @SerializedName("msg")
    @Expose
    private var msg: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("code")
    @Expose
    private var code: Int? = null

    var responsesList: List<BinanceResponse>? = null

    override fun success(): Boolean {
        return success ?: (code==null)
    }

    override fun message(): String {
        return msg?:message?:"No error message"
    }

    @SerializedName("coin")
    @Expose
    val coinName: String? = null

    @SerializedName("free")
    @Expose
    val amount: Double = 0.0

    @SerializedName("withdrawList")
    @Expose
    val withdrawList: List<Withdraw>? = null

    @SerializedName("symbols")
    @Expose
    private val symbols: List<Symbols>? = null

    override fun getTradeLimitByName(pairName: String?): Double? {
        if (symbols == null) return null
        for (symbol in symbols) {
            if (symbol.symbol.equals(pairName)) {
                if (symbol.filters == null) return null
                for (filter in symbol.filters) {
                    if (filter.filterType.equals(MIN_QTY_FILTER)) {
                        return filter.minQty
                    }
                }
            }
        }
        return null
    }

    @SerializedName("asks")
    @Expose
    private val asks: List<List<String>>? = null

    @SerializedName("bids")
    @Expose
    private val bids: List<List<String>>? = null

    override fun bids(): List<Price?>? {
        return parseValues(bids)
    }

    override fun asks(): List<Price?>? {
        return parseValues(asks)
    }

    private fun parseValues(values: List<List<String>>?): List<Price?> {
        val prices: MutableList<Price?> = ArrayList()
        values!!.forEach{ value: List<String> -> prices.add(Price(value[0].toDouble(), value[1].toDouble())) }
        return prices
    }

    @SerializedName("depositList")
    @Expose
    val depositList: List<Deposit>? = null

    @SerializedName("address")
    @Expose
    override val address: String? = null

    var assetDetails: MutableList<AssetDetail>? = null

    override fun getCurrencies(): List<CurrenciesResponse> {
        return assetDetails ?: ArrayList()
    }

    @SerializedName("symbol")
    @Expose
    private val symbol: String? = null

    @SerializedName("bidPrice")
    @Expose
    override val tickerBid: Double? = null

    @SerializedName("askPrice")
    @Expose
    override val tickerAsk: Double? = null

    @SerializedName("volume")
    @Expose
    override val volume: Double = 0.0

    override val marketName: String
        get() {
            return symbolToPairName(symbol)
        }

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

    override val historyTime: ZonedDateTime
        get() = longToTime(time)

    override val historyName: String
        get() {
            return symbolToPairName(symbol)
        }

    override val historyMarket = "binance"

    override val progress: Int
        get() {
            if (historyAmount == 0.0) return 0
            return (((historyAmount - executedQuantity) / (historyAmount)) * 100).toInt()
        }

    @SerializedName("status")
    @Expose
    val status: String? = null

    private fun symbolToPairName(symbol: String?) : String {
        if (symbol==null) return ""
        for (baseName in listOf("BTC","ETH","USDT")) {
            val index = symbol.indexOf(baseName, symbol.length - baseName.length)
            if (index>0) {
                return "$baseName/" + symbol.substring(0, index)
            }
        }
        return symbol
    }

    companion object {
        fun longToTime(longTime: Long) : ZonedDateTime {
            return ZonedDateTime.ofLocal(
                        LocalDateTime.ofEpochSecond((longTime / 1000.toFloat()).roundToLong(),
                        0,
                        ZoneOffset.UTC),
                    ZoneId.of("Z"),
                    ZoneOffset.UTC)
        }
    }
}