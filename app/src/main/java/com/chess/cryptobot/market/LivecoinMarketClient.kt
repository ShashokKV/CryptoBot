package com.chess.cryptobot.market

import android.content.Context
import com.chess.cryptobot.api.LivecoinMarketService
import com.chess.cryptobot.exceptions.LivecoinException
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.response.*
import com.chess.cryptobot.model.response.livecoin.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class LivecoinMarketClient internal constructor(url: String?, apiKey: String?, secretKey: String?) : MarketClient(url!!, apiKey, secretKey) {
    private val service: LivecoinMarketService
    override fun getMarketName(): String {
        return Market.LIVECOIN_MARKET
    }

    override fun initGson(): Gson {
        return GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setLenient()
                .create()
    }

    override fun initService(retrofit: Retrofit): Any {
        return retrofit.create(LivecoinMarketService::class.java)
    }

    override fun initWebSocket() {
        TODO("Not yet implemented")
    }

    @Throws(LivecoinException::class)
    override fun getAmount(coinName: String): Double {
        if (keysIsEmpty()) return 0.0
        val params = TreeMap<String, String>()
        params["currency"] = coinName
        val hash = makeHash(params)
        val headers = TreeMap<String, String>()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val response: LivecoinBalanceResponse
        val call = service.getBalance(params, headers)
        response = try {
            execute(call!!) as LivecoinBalanceResponse
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
        return response.amount
    }

    @Throws(LivecoinException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: LivecoinOrderBookResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currencyPair"] = pairName
        params["groupByPrice"] = "true"
        params["depth"] = "10"
        val call = service.getOrderBook(params)
        response = try {
            execute(call) as LivecoinOrderBookResponse
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
        return response
    }

    @Throws(MarketException::class)
    override fun getTicker(): List<TickerResponse> {
        val responses: List<LivecoinTickerResponse>?
        val call: Call<List<LivecoinTickerResponse>> = service.ticker
        try {
            val result = call.execute()
            responses = result.body()
            if (responses == null) {
                throw LivecoinException("No response")
            }
        } catch (e: IOException) {
            throw LivecoinException(e.message!!)
        }
        return responses
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        val response: LivecoinCurrenciesListResponse
        val call: Call<LivecoinCurrenciesListResponse> = service.currencies
        response = try {
            execute(call) as LivecoinCurrenciesListResponse
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
        return response.getCurrencies()
    }

    @Throws(MarketException::class)
    override fun getMinQuantity(): TradeLimitResponse {
        val response: LivecoinTradeLimitResponse
        val call: Call<LivecoinTradeLimitResponse> = service.minTradeSize
        response = try {
            execute(call) as LivecoinTradeLimitResponse
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
        return response
    }

    @Throws(MarketException::class)
    override fun getAddress(coinName: String): String? {
        if (keysIsEmpty()) return null
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currency"] = coinName
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val response: LivecoinAddressResponse
        val call = service.getAddress(params, headers)
        response = try {
            execute(call!!) as LivecoinAddressResponse
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
        return response.address
    }

    @Throws(MarketException::class)
    override fun sendCoins(coinName: String, amount: Double, address: String) {
        if (keysIsEmpty()) return
        val params: MutableMap<String, String> = LinkedHashMap()
        params["amount"] = String.format(Locale.US, "%.8f", amount)
        params["currency"] = coinName
        params["wallet"] = address
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val call = service.payment(
                params["amount"],
                params["currency"],
                params["wallet"],
                headers)
        try {
            execute(call!!)
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun buy(pairName: String, price: Double, amount: Double) {
        if (keysIsEmpty()) return
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currencyPair"] = pairName
        params["price"] = String.format(Locale.US, "%.8f", price)
        params["quantity"] = String.format(Locale.US, "%.8f", amount)
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val call = service.buy(
                params["currencyPair"],
                params["price"],
                params["quantity"],
                headers)
        try {
            execute(call!!)
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun sell(pairName: String, price: Double, amount: Double) {
        if (keysIsEmpty()) return
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currencyPair"] = pairName
        params["price"] = String.format(Locale.US, "%.8f", price)
        params["quantity"] = String.format(Locale.US, "%.8f", amount)
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val call = service.sell(
                params["currencyPair"],
                params["price"],
                params["quantity"],
                headers)
        try {
            execute(call!!)
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun getOpenOrders(): List<History> {
        if (keysIsEmpty()) return ArrayList()
        val params: MutableMap<String, String> = LinkedHashMap()
        params["openClosed"] = "OPEN"
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val response: LivecoinOrdersResponse
        val call = service.getOpenOrders(params, headers)
        response = try {
            execute(call!!) as LivecoinOrdersResponse
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        }
        return HistoryResponseFactory(response.data).history
    }

    @Throws(MarketException::class)
    override fun getHistory(context: Context?): List<History>    {
        return getHistoryByType(null)
    }

    @Throws(MarketException::class)
    override fun getDepositHistory(): List<History> {
        return getHistoryByType("DEPOSIT")
    }

    @Throws(MarketException::class)
    override fun getWithdrawHistory(): List<History> {
        return getHistoryByType("WITHDRAWAL")
    }

    private fun getHistoryByType(historyType: String?) : List<History> {
        if (keysIsEmpty()) return ArrayList()
        val startTime = LocalDateTime.now().minusDays(29)
        val endTime = LocalDateTime.now()
        val params: MutableMap<String, String> = LinkedHashMap()
        params["end"] = (endTime.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        params["start"] = (startTime.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        if (historyType!=null) {
            params["types"] = historyType
        }

        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val responses: List<LivecoinHistoryResponse>?
        val call = service.getHistory(params, headers)
        try {
            val result = call.execute()
            responses = result.body()
            if (responses == null) {
                throw LivecoinException("No response")
            }
        } catch (e: MarketException) {
            throw LivecoinException(e.message!!)
        } catch (e: IOException) {
            throw LivecoinException(e.message!!)
        }
        return HistoryResponseFactory(responses).history
    }

    init {
        algorithm = "HmacSHA256"
        path = ""
        service = initService(initRetrofit(initGson())) as LivecoinMarketService
    }
}