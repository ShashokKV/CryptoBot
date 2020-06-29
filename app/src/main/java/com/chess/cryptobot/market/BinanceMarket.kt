package com.chess.cryptobot.market

import android.content.Context
import com.chess.cryptobot.api.BinanceMarketService
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.content.pairs.AllPairsPreferences
import com.chess.cryptobot.exceptions.BinanceException
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.response.*
import com.chess.cryptobot.model.response.binance.BinanceDeserializer
import com.chess.cryptobot.model.response.binance.BinanceResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


class BinanceMarket internal constructor(url: String, apiKey: String?, secretKey: String?,
                                         private val proxySelector: BinanceProxySelector?) : MarketRequest(url, apiKey, secretKey) {

    private val service: BinanceMarketService
    var balances: MutableMap<String, Double> = HashMap()

    init {
        algorithm = "HmacSHA256"
        path = ""
        service = initService(initRetrofit(initGson())) as BinanceMarketService
    }

    override fun initHttpClient(): OkHttpClient {

        var clientBuilder = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)

        if (proxySelector != null) {
            clientBuilder = clientBuilder
                    .proxySelector(proxySelector)
                    .proxyAuthenticator(proxySelector.proxyAuthenticator)
        }

        return clientBuilder.build()
    }

    override fun getMarketName(): String {
        return Market.BINANCE_MARKET
    }

    override fun initGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(BinanceResponse::class.java, BinanceDeserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create()
    }

    override fun initService(retrofit: Retrofit): Any {
        return retrofit.create(BinanceMarketService::class.java)
    }

    @Throws(BinanceException::class)
    override fun getAmount(coinName: String): Double {
        if (keysIsEmpty()) return 0.0

        val balance: Double? = balances[coinName]
        if (balance != null) return balance

        val params: MutableMap<String, String> = LinkedHashMap()
        params["recvWindow"] = "15000"
        addTimestamp(params)
        val headers: MutableMap<String, String> = HashMap()
        val hash = makeHash(params)
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash

        val response: BinanceResponse
        val call = service.getBalance(params, headers)
        response = try {
            execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }

        balances = HashMap()
        response.balances.forEach { balanceResponse ->
            balances[balanceResponse.coinName!!] = balanceResponse.amount
        }
        return balances[coinName] ?: 0.0
    }

    @Throws(BinanceException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: BinanceResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["symbol"] = pairName
        params["limit"] = "10"
        val call = service.getOrderBook(params)
        response = try {
            execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return response
    }

    @Throws(MarketException::class)
    override fun getTicker(): List<TickerResponse> {
        val response: BinanceResponse?
        val call = service.getTicker()
        try {
            val result = call.execute()
            response = result.body()
            if (response == null) {
                throw BinanceException("No response")
            }
        } catch (e: IOException) {
            throw BinanceException(e.message!!)
        }
        val tickerResponse = response.tickers
        return tickerResponse.filter { it.tickerAsk ?: 0.0 > 0.0 && it.tickerBid ?: 0.0 > 0.0 }
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        if (keysIsEmpty()) return ArrayList()
        val params: MutableMap<String, String> = LinkedHashMap()
        params["recvWindow"] = "15000"
        addTimestamp(params)
        val headers: MutableMap<String, String> = HashMap()
        val hash = makeHash(params)
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val response: BinanceResponse
        val call = service.getAssetDetails(params, headers)
        response = try {
            execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return response.getCurrencies()
    }

    @Throws(MarketException::class)
    override fun getMinQuantity(): TradeLimitResponse {
        val response: BinanceResponse
        val call = service.getExchangeInfo()
        response = try {
            execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return response
    }

    @Throws(MarketException::class)
    override fun getAddress(coinName: String): String? {
        if (keysIsEmpty()) return null
        val params: MutableMap<String, String> = LinkedHashMap()
        params["asset"] = coinName
        params["recvWindow"] = "10000"
        addTimestamp(params)
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val response: BinanceResponse
        val call = service.getAddress(params, headers)
        response = try {
            execute(call) as BinanceResponse
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
        params["asset"] = coinName
        params["address"] = address
        params["recvWindow"] = "15000"
        addTimestamp(params)
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val call = service.payment(params, headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun buy(pairName: String, price: Double, amount: Double) {
        newOrder(pairName, price, amount, "BUY")
    }

    @Throws(MarketException::class)
    override fun sell(pairName: String, price: Double, amount: Double) {
        newOrder(pairName, price, amount, "SELL")
    }

    private fun newOrder(pairName: String, price: Double, amount: Double, side: String) {
        if (keysIsEmpty()) return
        val params: MutableMap<String, String> = LinkedHashMap()
        params["symbol"] = pairName
        params["side"] = side
        params["type"] = "LIMIT"
        params["timeInForce"] = "GTC"
        params["quantity"] = String.format(Locale.US, "%.8f", amount)
        params["price"] = String.format(Locale.US, "%.8f", price)
        params["newOrderRespType"] = "ACK"
        params["recvWindow"] = "15000"
        addTimestamp(params)
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val call = service.newOrder(params, headers)
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
        params["recvWindow"] = "15000"
        addTimestamp(params)
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val response: BinanceResponse
        val call = service.getOpenOrders(params, headers)
        response = try {
            execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return HistoryResponseFactory(response.orders).history
    }

    @Throws(MarketException::class)
    override fun getHistory(context: Context?): List<History> {
        if (keysIsEmpty()) return listOf(History())
        val startTime = getStartTime()
        val historyList: MutableList<History> = ArrayList()
        getAllPairs(context).forEach { pair -> historyList.addAll(getOrdersHistory(pair, startTime)) }

        historyList.addAll(getWithdrawHistory())
        historyList.addAll(getDepositHistory())

        return historyList
    }

    private fun getAllPairs(context: Context?): List<String> {
        val allPairs = AllPairsPreferences(context).items ?: return ArrayList()
        val balancePrefs = BalancePreferences(context)
        val pairsList = ArrayList<String>()
        balancePrefs.items?.forEach { baseName ->
            balancePrefs.items?.forEach { marketName ->
                if (baseName!=marketName) {
                    var pairName = "${baseName}/${marketName}"
                    if (allPairs.contains(pairName)) {
                        pairName = "$marketName$baseName"
                        pairsList.add(pairName)
                    }
                }
            }
        }
        return pairsList
    }

    private fun getOrdersHistory(pairName: String, startTime: String): List<History> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["symbol"] = pairName
        params["startTime"] = startTime
        params["recvWindow"] = "15000"
        addTimestamp(params)
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val response: BinanceResponse
        val call = service.getAllOrders(params, headers)
        try {
            response = execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return HistoryResponseFactory(response.orders.filter { order -> order.status.equals("FILLED") }).history
    }

    @Throws(BinanceException::class)
    override fun getDepositHistory(): List<History> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["recvWindow"] = "15000"
        addTimestamp(params)
        params["startTime"] = getStartTime()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val response: BinanceResponse
        val call = service.getDepositHistory(params, headers)
        response = try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        } as BinanceResponse
        return HistoryResponseFactory(response.depositList).history
    }

    @Throws(BinanceException::class)
    override fun getWithdrawHistory(): List<History> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["recvWindow"] = "15000"
        addTimestamp(params)
        params["startTime"] = getStartTime()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val response: BinanceResponse
        val call = service.getWithdrawHistory(params, headers)
        response = try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        } as BinanceResponse
        return HistoryResponseFactory(response.withdrawList).history
    }

    private fun getStartTime(): String {
        return (LocalDateTime.now().minusDays(29)
                .toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) * 1000).toString()
    }

    private fun addTimestamp(params: MutableMap<String, String>) {
        params["timestamp"] = Instant.now().atZone(ZoneId.of("Z")).toInstant().toEpochMilli().toString()
    }
}