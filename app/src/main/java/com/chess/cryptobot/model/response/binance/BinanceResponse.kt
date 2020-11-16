package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.*
import kotlin.math.roundToLong

const val MIN_QTY_FILTER: String = "LOT_SIZE"
const val PRICE_FILTER: String = "PRICE_FILTER"

open class BinanceResponse : MarketResponse, CurrenciesListResponse,
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

    override fun success(): Boolean {
        return success ?: (code==null)
    }

    override fun message(): String {
        return msg?:message?:"No error message"
    }

    val balances: MutableList<BinanceBalance> = ArrayList()

    @SerializedName("withdrawList")
    @Expose
    val withdrawList: List<Withdraw>? = null

    @SerializedName("symbols")
    @Expose
    private val symbols: List<Symbols>? = null

    override fun getTradeLimitByName(pairName: String?): Double {
        if (symbols == null) return 0.0
        for (symbol in symbols) {
            if (symbol.symbol.equals(pairName)) {
                if (symbol.filters == null) return 0.0
                for (filter in symbol.filters) {
                    if (filter.filterType.equals(MIN_QTY_FILTER)) {
                        return filter.minQty
                    }
                }
            }
        }
        return 0.0
    }

    fun getStepSizeByName(pairName: String?): Double? {
        if (symbols == null) return null
        for (symbol in symbols) {
            if (symbol.symbol.equals(pairName)) {
                if (symbol.filters == null) return null
                for (filter in symbol.filters) {
                    if (filter.filterType.equals(MIN_QTY_FILTER)) {
                        return filter.stepSize
                    }
                }
            }
        }
        return null
    }

    fun getPriceFilterByName(pairName: String?): Double? {
        if (symbols == null) return null
        for (symbol in symbols) {
            if (symbol.symbol.equals(pairName)) {
                if (symbol.filters == null) return null
                for (filter in symbol.filters) {
                    if (filter.filterType.equals(PRICE_FILTER)) {
                        return filter.tickSize
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

    var assetDetails: MutableList<AssetDetail> = ArrayList()

    override fun getCurrencies(): List<CurrenciesResponse> {
        return assetDetails
    }

    val tickers: MutableList<BinanceTicker> = ArrayList()

    val orders: MutableList<BinanceOrder> = ArrayList()

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