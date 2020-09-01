package com.chess.cryptobot.market

import android.content.Context
import com.chess.cryptobot.api.BittrexMarketService
import com.chess.cryptobot.exceptions.BittrexException
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.response.*
import com.chess.cryptobot.model.response.bittrex.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Retrofit

class BittrexMarket internal constructor(url: String, apiKey: String?, secretKey: String?) : MarketRequest(url, apiKey, secretKey) {
    private val service: BittrexMarketService

    init {
        algorithm = "HmacSHA512"
        service = initService(initRetrofit(initGson())) as BittrexMarketService
    }

    override fun getMarketName(): String {
        return Market.BITTREX_MARKET
    }

    override fun initGson(): Gson {
        return GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
    }

    override fun initService(retrofit: Retrofit): Any {
        return retrofit.create(BittrexMarketService::class.java)
    }

    @Throws(BittrexException::class)
    override fun getAmount(coinName: String): Double {
        if (keysIsEmpty()) return 0.0
        path = url + "balances/" + coinName
        val response: BittrexBalance
        response = try {
            val call = service.getBalance(coinName, signHeaders("", "GET"))
            execute(call) as BittrexBalance
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.amount
    }

    @Throws(MarketException::class)
    override fun getAddress(coinName: String): String? {
        if (keysIsEmpty()) return null
        path = url + "addresses/" + coinName
        val response: BittrexAddress
        response = try {
            val call = service.getAddress(coinName, signHeaders("", "GET"))
            execute(call) as BittrexAddress
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.address
    }

    @Throws(BittrexException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: BittrexOrderBook
        response = try {
            val call = service.getOrderBook(pairName)
            execute(call) as BittrexOrderBook
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response
    }

    @Throws(MarketException::class)
    override fun getTicker(): List<TickerResponse> {
        val response: List<*>
        val call = service.getTicker()
        response = try {
            executeList(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.filterIsInstance<TickerResponse>().filter { it.tickerAsk > 0.0 && it.tickerBid > 0.0 }
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        val response: List<*>
        val call = service.getCurrencies()
        response = try {
            executeList(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.filterIsInstance<CurrenciesResponse>()
    }

    @Throws(MarketException::class)
    override fun getMinQuantity(): TradeLimitResponse {
        val response: List<*>
        val call = service.getMarkets()
        response = try {
            executeList(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        val limit = BittrexTradeLimit()
        limit.limits = response.filterIsInstance<BittrexLimits>()
        return limit
    }

    @Throws(MarketException::class)
    override fun sendCoins(coinName: String, amount: Double, address: String) {
        if (keysIsEmpty()) return
        path = url + "withdrawals"
        val body = JsonObject()
        body.addProperty("currency", coinName)
        body.addProperty("quantity", amount)
        body.addProperty("address", address)
        val call = service.payment(body, signHeaders(body.asString, "POST"))
        val response: BittrexWithdraw
        response = try {
            execute(call) as BittrexWithdraw
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        if (response.status == "ERROR_INVALID_ADDRESS") throw BittrexException("invalid address")
    }

    @Throws(MarketException::class)
    override fun buy(pairName: String, price: Double, amount: Double) {
        order("BUY", pairName, price, amount)
    }

    @Throws(MarketException::class)
    override fun sell(pairName: String, price: Double, amount: Double) {
        order("SELL", pairName, price, amount)
    }

    private fun order(direction: String, pairName: String, price: Double, amount: Double) {
        if (keysIsEmpty()) return
        path = url + "orders"
        val body = JsonObject()
        body.addProperty("marketSymbol", pairName)
        body.addProperty("direction", direction)
        body.addProperty("type", "LIMIT")
        body.addProperty("quantity", amount)
        body.addProperty("limit", price)
        body.addProperty("useAwards", true)
        val call = service.order(body, signHeaders(body.asString, "POST"))
        try {
            execute(call) as BittrexWithdraw
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun getOpenOrders(): List<History> {
        if (keysIsEmpty()) return listOf(History())
        path = url + "orders/open"
        val response: List<*>
        val call = service.getOpenOrders(signHeaders("", "GET"))
        response = try {
            executeList(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return HistoryResponseFactory(response.filterIsInstance<BittrexHistory>()).history
    }

    @Throws(MarketException::class)
    override fun getHistory(context: Context?): List<History> {
        if (keysIsEmpty()) return listOf(History())
        path = url + "orders/closed"
        val response: List<*>
        val call = service.getOrderHistory(signHeaders("", "GET"))
        val historyList = ArrayList<History>()
        try {
            response = executeList(call)
            historyList.addAll(HistoryResponseFactory(response.filterIsInstance<BittrexHistory>()).history)
            historyList.addAll(getWithdrawHistory())
            historyList.addAll(getDepositHistory())
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return historyList
    }

    override fun getDepositHistory(): List<History> {
        if (keysIsEmpty()) return ArrayList()
        path = url + "deposits/closed"
        val response: List<*>
        val call = service.getDepositHistory(signHeaders("", "GET"))
        response = try {
            executeList(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return HistoryResponseFactory(response.filterIsInstance<BittrexHistory>()).history
    }

    override fun getWithdrawHistory(): List<History> {
        if (keysIsEmpty()) return ArrayList()
        path = url + "withdrawals/closed"
        val response: List<*>
        val call = service.getWithdrawHistory(signHeaders("", "GET"))
        response = try {
            executeList(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return HistoryResponseFactory(response.filterIsInstance<BittrexHistory>()).history
    }

    private fun signHeaders(body: String, method: String): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Api-Key"] = apiKey
        val timestamp = timestamp()
        headers["Api-Timestamp"] = timestamp
        val contentHash = encode(body)
        headers["Api-Content-Hash"] = contentHash
        headers["Api-Signature"] = encode(timestamp+path+method+contentHash)
        return headers
    }
}