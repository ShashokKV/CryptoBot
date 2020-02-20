package com.chess.cryptobot.market

import com.chess.cryptobot.api.BinanceMarketService
import com.chess.cryptobot.exceptions.BinanceException
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.response.*
import com.chess.cryptobot.model.response.binance.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class BinanceMarket internal constructor(url: String, apiKey: String?, secretKey: String?) : MarketRequest(url, apiKey, secretKey) {
    private val service: BinanceMarketService
    override fun getMarketName(): String {
        return Market.BINANCE_MARKET
    }

    override fun initGson(): Gson {
        return GsonBuilder().create()
    }

    override fun initService(retrofit: Retrofit): Any {
        return retrofit.create(BinanceMarketService::class.java)
    }

    @Throws(BinanceException::class)
    override fun getAmount(coinName: String): Double {
        if (keysIsEmpty()) return 0.0
        val params = TreeMap<String, String>()
        params["currency"] = coinName
        val hash = makeHash(params)
        val headers = TreeMap<String, String>()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val response: BinanceBalanceResponse
        val call = service.getBalance(params, headers)
        response = try {
            execute(call) as BinanceBalanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return response.amount
    }

    @Throws(BinanceException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: BinanceOrderBookResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currencyPair"] = pairName
        params["groupByPrice"] = "true"
        params["depth"] = "10"
        val call = service.getOrderBook(params)
        response = try {
            execute(call) as BinanceOrderBookResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return response
    }

    @Throws(MarketException::class)
    override fun getTicker(): List<TickerResponse> {
        val responses: List<BinanceTickerResponse?>?
        val call = service.ticker
        try {
            val result = call.execute()
            responses = result.body()
            if (responses == null) {
                throw BinanceException("No response")
            }
        } catch (e: IOException) {
            throw BinanceException(e.message!!)
        }
        return responses
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        val response: BinanceCurrenciesListResponse
        val call = service.currencies
        response = try {
            execute(call) as BinanceCurrenciesListResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return response.getCurrencies()
    }

    @Throws(MarketException::class)
    override fun getMinQuantity(): TradeLimitResponse {
        val response: BinanceTradeLimitResponse
        val call = service.minTradeSize
        response = try {
            execute(call) as BinanceTradeLimitResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
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
        val response: BinanceAddressResponse
        val call = service.getAddress(params, headers)
        response = try {
            execute(call) as BinanceAddressResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
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
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val call = service.payment(
                params["amount"]!!,
                params["currency"]!!,
                params["wallet"]!!,
                headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
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
                params["currencyPair"]!!,
                params["price"]!!,
                params["quantity"]!!,
                headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
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
                params["currencyPair"]!!,
                params["price"]!!,
                params["quantity"]!!,
                headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun getOpenOrders(): List<History> {
        if (keysIsEmpty()) return listOf(History())
        val params: MutableMap<String, String> = LinkedHashMap()
        params["openClosed"] = "OPEN"
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val response: BinanceOrdersResponse
        val call = service.getOpenOrders(params, headers)
        response = try {
            execute(call) as BinanceOrdersResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return HistoryResponseFactory(response.data).history
    }

    @Throws(MarketException::class)
    override fun getHistory(): List<History> {
        if (keysIsEmpty()) return listOf(History())
        val startTime = LocalDateTime.now().minusDays(29)
        val endTime = LocalDateTime.now()
        val params: MutableMap<String, String> = LinkedHashMap()
        params["end"] = (endTime.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) * 1000).toString()
        params["start"] = (startTime.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) * 1000).toString()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["API-key"] = apiKey
        headers["Sign"] = hash
        val responses: List<BinanceHistoryResponse?>?
        val call = service.getHistory(params, headers)
        try {
            val result = call.execute()
            responses = result.body()
            if (responses == null) {
                throw BinanceException("No response")
            }
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        } catch (e: IOException) {
            throw BinanceException(e.message!!)
        }
        return HistoryResponseFactory(responses).history
    }

    init {
        algorithm = "HmacSHA256"
        path = ""
        service = initService(initRetrofit(initGson())) as BinanceMarketService
    }
}