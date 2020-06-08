package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.*
import java.util.function.Consumer

class BittrexResponse : MarketResponse, BalanceResponse, OrderBookResponse, CurrenciesListResponse, AddressResponse, TradeLimitResponse {
    private var success: Boolean? = null
    private var message: String? = null
    val results: Array<BittrexGenericResponse?>

    internal constructor(results: Array<BittrexGenericResponse?>) {
        this.results = results
    }

    internal constructor(result: BittrexGenericResponse?) {
        results = arrayOfNulls(1)
        results[0] = result
    }

    fun setSuccess(success: Boolean?) {
        this.success = success
    }

    override fun success(): Boolean {
        return success!!
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    override fun message(): String? {
        return message
    }

    override val amount: Double
        get() = results[0]!!.available

    override fun bids(): List<Price?>? {
        return parsePrices(results[0]!!.buy)
    }

    override fun asks(): List<Price?>? {
        return parsePrices(results[0]!!.sell)
    }

    val tickers: List<TickerResponse>
        get() = results.toList().filterIsInstance<TickerResponse>()

    private fun parsePrices(prices: List<BittrexPrice>?): List<Price?> {
        val parsedPrices = ArrayList<Price?>()
        prices!!.forEach(Consumer { price: BittrexPrice -> parsedPrices.add(Price(price.rate!!, price.quantity!!)) })
        return parsedPrices
    }

    override fun getCurrencies(): List<CurrenciesResponse> {
       return results.toList().filterIsInstance<CurrenciesResponse>()
    }

    override val address: String?
        get() = results[0]!!.address

    override fun getTradeLimitByName(pairName: String?): Double? {
        for (result in results) {
            if (result!!.pairName == pairName) {
                return result.minTradeSize
            }
        }
        return null
    }

    val history: List<History>
        get() = HistoryResponseFactory(results.toList().filterIsInstance<HistoryResponse>()).history

}