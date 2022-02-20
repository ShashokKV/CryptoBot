package com.chess.cryptobot.market

import android.content.Context
import android.util.Log
import com.chess.cryptobot.api.PoloniexMarketService
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.exceptions.PoloniexException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.response.*
import com.chess.cryptobot.model.response.poloniex.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import java.io.IOException
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class PoloniexMarketClient internal constructor(url: String?, apiKey: String?, secretKey: String?) : MarketClient(url!!, apiKey, secretKey) {
    private val service: PoloniexMarketService
    private val tag = PoloniexMarketClient::class.qualifiedName
    private var balances = JsonObject()

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
        if (!resetBalance) {
            return if (balances.has(coinName)) {
                balances.get(coinName).asDouble
            } else {
                0.0
            }
        }
        val params = TreeMap<String, String>()
        params["command"] = "returnBalances"
        params["timestamp"] = timestamp()
        val hash = makeHash(params)
        val headers = TreeMap<String, String>()
        headers["Key"] = apiKey
        headers["Sign"] = hash
        val response: PoloniexResponse
        val call = service.privateApi(params, headers)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }

        balances = response.data
        return if (balances.has(coinName)) {
            resetBalance = false
            balances.get(coinName).asDouble
        } else {
            0.0
        }
    }

    @Throws(PoloniexException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: PoloniexResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnOrderBook"
        params["currencyPair"] = pairName
        params["depth"] = "10"
        val call = service.publicApi(params)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
        return PoloniexOrderBookResponse(response)
    }

    @Throws(MarketException::class)
    override fun getTicker(): List<TickerResponse> {
        val response: PoloniexResponse?
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnTicker"
        val call: Call<PoloniexResponse> = service.publicApi(params)
        try {
            val result = call.execute()
            response = result.body()
            if (response == null) {
                throw PoloniexException("No response")
            }
        } catch (e: IOException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
        val result = mutableListOf<PoloniexTickerResponse>()
        val tickers = response.data
            tickers.keySet()?.forEach { symbol ->
                result.add(PoloniexTickerResponse(symbol,
                        tickers[symbol].asJsonObject["highestBid"].asDouble,
                        tickers[symbol].asJsonObject["lowestAsk"].asDouble))
            }
        return result
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        val response: PoloniexResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnCurrencies"
        val call: Call<PoloniexResponse> = service.publicApi(params)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
        val result = mutableListOf<PoloniexCurrenciesResponse>()
        val currencies = response.data
        currencies.keySet()?.forEach { symbol ->
            val currency = currencies[symbol].asJsonObject
            result.add(PoloniexCurrenciesResponse(symbol,
                    !(currency["disabled"].asBoolean && currency["delisted"].asBoolean && currency["frozen"].asBoolean),
                    currency["txFee"].asDouble))
        }
        return result
    }

    override fun getMinQuantity(): TradeLimitResponse {
        return PoloniexTradeLimitResponse()
    }

    @Throws(MarketException::class)
    override fun getAddress(coinName: String): String? {
        if (keysIsEmpty()) return null
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnDepositAddresses"
        params["timestamp"] = timestamp()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["Key"] = apiKey
        headers["Sign"] = hash
        val response: PoloniexResponse
        val call = service.privateApi(params, headers)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
        val addresses = response.data
        return if (addresses.has(coinName)) {
            addresses[coinName].asString
        } else {
            null
        }
    }

    @Throws(MarketException::class)
    override fun sendCoins(coinName: String, amount: Double, address: String) {
        if (keysIsEmpty()) return
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "withdraw"
        params["currency"] = coinName
        params["amount"] = String.format(Locale.US, "%.8f", amount)
        params["address"] = address
        params["timestamp"] = timestamp()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["Key"] = apiKey
        headers["Sign"] = hash
        val call = service.privateApi(params, headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun buy(pairName: String, price: Double, amount: Double) {
        makeTrade("buy", pairName, price, amount)
    }

    @Throws(MarketException::class)
    override fun sell(pairName: String, price: Double, amount: Double) {
        makeTrade("sell", pairName, price, amount)
    }

    private fun makeTrade(command: String, pairName: String, price: Double, amount: Double) {
        if (keysIsEmpty()) return
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = command
        params["currencyPair"] = pairName
        params["rate"] = String.format(Locale.US, "%.8f", price)
        params["amount"] = String.format(Locale.US, "%.8f", amount)
        params["timestamp"] = timestamp()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["Key"] = apiKey
        headers["Sign"] = hash
        val call = service.privateApi(params, headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun getOpenOrders(): List<History> {
        return getOrdersHistory("returnOpenOrders")
    }

    @Throws(MarketException::class)
    override fun getHistory(context: Context): List<History> {
        val historyResult: MutableList<History> = getOrdersHistory("returnTradeHistory") as MutableList<History>
        historyResult.addAll(this.getCoinMoveHistoryByType(null))
        return historyResult
    }

    private fun getOrdersHistory(command: String): List<History> {
        if (keysIsEmpty()) return ArrayList()
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = command
        params["currencyPair"] = "all"
        params["timestamp"] = timestamp()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["Key"] = apiKey
        headers["Sign"] = hash
        val response: PoloniexResponse
        val call = service.privateApi(params, headers)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
        val result = mutableListOf<PoloniexHistoryResponse>()
        val history = response.data
        history.keySet()?.forEach { currency ->
            val currencyHistories = history[currency].asJsonArray
            if (currencyHistories.size() > 0) {
                currencyHistories.forEach { currencyHistory ->
                    val poloniexHistory = PoloniexHistoryResponse()
                    val historyObject = currencyHistory.asJsonObject
                    poloniexHistory.currencyPair = currency
                    poloniexHistory.issueTime = LocalDateTime.parse(historyObject["date"].asString)
                    poloniexHistory.historyAction = historyObject["type"].asString
                    if (command == "returnTradeHistory") {
                        poloniexHistory.historyAmount = historyObject["amount"].asDouble
                    } else if (command == "returnOpenOrders") {
                        poloniexHistory.historyAmount = historyObject["startingAmount"].asDouble
                        poloniexHistory.remainingAmount = historyObject["amount"].asDouble
                    }
                    poloniexHistory.historyPrice = historyObject["rate"].asDouble
                    result.add(poloniexHistory)
                }
            }
        }
        return HistoryResponseFactory(result).history
    }

    @Throws(MarketException::class)
    override fun getDepositHistory(): List<History> {
        return getCoinMoveHistoryByType("deposits")
    }

    @Throws(MarketException::class)
    override fun getWithdrawHistory(): List<History> {
        return getCoinMoveHistoryByType("withdrawals")
    }

    private fun getCoinMoveHistoryByType(historyType: String?): List<History> {
        if (keysIsEmpty()) return ArrayList()
        val startTime = LocalDateTime.now().minusDays(29)
        val endTime = LocalDateTime.now()
        val params: MutableMap<String, String> = LinkedHashMap()
        params["command"] = "returnDepositsWithdrawals"
        params["end"] = (endTime.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        params["start"] = (startTime.toEpochSecond(ZoneOffset.UTC) * 1000).toString()
        params["timestamp"] = timestamp()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["Key"] = apiKey
        headers["Sign"] = hash
        val response: PoloniexResponse
        val call = service.privateApi(params, headers)
        response = try {
            execute(call) as PoloniexResponse
        } catch (e: MarketException) {
            Log.e(tag, Log.getStackTraceString(e))
            throw PoloniexException(e.message!!)
        }
        val result = mutableListOf<PoloniexHistoryResponse>()
        val history = response.data
        val historyKey = if (historyType == null) {
            history.keySet()
        } else {
            setOf(historyType)
        }
        historyKey?.forEach { histType ->
            val currencyHistories = history.get(histType)?.asJsonArray
            if (currencyHistories?.size() ?: 0 > 0) {
                currencyHistories?.forEach { currencyHistory ->
                    val poloniexHistory = PoloniexHistoryResponse()
                    val historyObject = currencyHistory.asJsonObject
                    poloniexHistory.currencyPair = historyObject["currency"].asString
                    val timestamp = historyObject["timestamp"].asLong
                    poloniexHistory.issueTime = LocalDateTime.ofEpochSecond(
                            (timestamp) / 1000, (timestamp % 1000 * 1000000).toInt(), ZoneOffset.UTC)
                    poloniexHistory.historyAction = histType.dropLast(1)
                    poloniexHistory.historyAmount = historyObject["amount"].asDouble
                    result.add(poloniexHistory)
                }
            }
        }
        return HistoryResponseFactory(result).history
    }

    override fun timestamp(): String {
        return LocalDateTime.now().toEpochSecond(OffsetDateTime.now().offset).toString()
    }

    init {
        algorithm = "HmacSHA512"
        path = ""
        service = initService(initRetrofit(initGson())) as PoloniexMarketService
    }
}