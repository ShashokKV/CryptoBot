package com.chess.cryptobot.market

import android.content.Context
import com.chess.cryptobot.api.PoloniexMarketService
import com.chess.cryptobot.exceptions.PoloniexException
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

class PoloniexMarketClient internal constructor(url: String?, apiKey: String?, secretKey: String?) : MarketClient(url!!, apiKey, secretKey) {
    private val service: PoloniexMarketService
    override fun getMarketName(): String {
        return Market.POLONIEX_MARKET
    }

    override fun initGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(PoloniexResponse::class.java, PoloniexDeserializer())
                .create()
    }

    override fun initService(retrofit: Retrofit): Any {
        return retrofit.create(PoloniexMarketService::class.java)
    }

    @Throws(PoloniexException::class)
    override fun getAmount(coinName: String): Double {
        if (keysIsEmpty()) return 0.0
        val params = TreeMap<String, String>()
        params["command"] = "returnBalances"
        params["nonce"] = timestamp()
        val hash = makeHash(params)
        val headers = TreeMap<String, String>()
        headers["Key"] = apiKey
        headers["Sign"] = hash
        val response: PoloniexResponse
        val call = service.getBalance(params, headers)
        response = try {
            execute(call!!) as PoloniexResponse
        } catch (e: MarketException) {
            throw PoloniexException(e.message!!)
        }

        return response.objectData?.get(coinName)?.asDouble ?: 0.0
    }

    @Throws(PoloniexException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: PoloniexResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnOrderBook"
        params["currencyPair"] = pairName
        params["depth"] = "10"
        val call = service.getOrderBook(params)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            throw PoloniexException(e.message!!)
        }
        return PoloniexOrderBookResponse(response)
    }

    @Throws(MarketException::class)
    override fun getTicker(): List<TickerResponse> {
        val response: PoloniexResponse?
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnTicker"
        val call: Call<PoloniexResponse> = service.ticker(params)
        try {
            val result = call.execute()
            response = result.body()
            if (response == null) {
                throw PoloniexException("No response")
            }
        } catch (e: IOException) {
            throw PoloniexException(e.message!!)
        }
        val result = mutableListOf<PoloniexTickerResponse>()
        val tickers = response.objectData
        if (tickers!=null) {
            tickers.keySet()?.forEach { symbol ->
                result.add(PoloniexTickerResponse(symbol,
                        tickers[symbol].asJsonObject["highestBid"].asDouble,
                        tickers[symbol].asJsonObject["lowestAsk"].asDouble))
            }
        }
        return result
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        val response: PoloniexResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnCurrencies"
        val call: Call<PoloniexResponse> = service.currencies(params)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            throw PoloniexException(e.message!!)
        }
        val result = mutableListOf<PoloniexCurrenciesResponse>()
        val currencies = response.objectData
        currencies?.keySet()?.forEach{ symbol ->
            val currency = currencies[symbol].asJsonObject
            result.add(PoloniexCurrenciesResponse(symbol,
                    !(currency["disabled"].asBoolean && currency["delisted"].asBoolean && currency["frozen"].asBoolean),
                    currency["txFee"].asDouble))}
        return result
    }

    @Throws(MarketException::class)
    override fun getMinQuantity(): TradeLimitResponse {
        val response: LivecoinTradeLimitResponse
        val call: Call<LivecoinTradeLimitResponse> = service.minTradeSize
        response = try {
            execute(call) as LivecoinTradeLimitResponse
        } catch (e: MarketException) {
            throw PoloniexException(e.message!!)
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
            throw PoloniexException(e.message!!)
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
            throw PoloniexException(e.message!!)
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
            throw PoloniexException(e.message!!)
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
            throw PoloniexException(e.message!!)
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
            throw PoloniexException(e.message!!)
        }
        return HistoryResponseFactory(response.data).history
    }

    @Throws(MarketException::class)
    override fun getHistory(context: Context): List<History>    {
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
                throw PoloniexException("No response")
            }
        } catch (e: MarketException) {
            throw PoloniexException(e.message!!)
        } catch (e: IOException) {
            throw PoloniexException(e.message!!)
        }
        return HistoryResponseFactory(responses).history
    }

    init {
        algorithm = "HmacSHA512"
        path = ""
        service = initService(initRetrofit(initGson())) as PoloniexMarketService
    }
}