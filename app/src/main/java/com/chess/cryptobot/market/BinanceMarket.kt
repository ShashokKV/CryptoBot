package com.chess.cryptobot.market

import android.content.Context
import com.chess.cryptobot.api.BinanceMarketService
import com.chess.cryptobot.content.pairs.PairsPreferences
import com.chess.cryptobot.exceptions.BinanceException
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.response.*
import com.chess.cryptobot.model.response.binance.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BinanceMarket internal constructor(url: String, apiKey: String?, secretKey: String?,
                                         private val proxySelector: BinanceProxySelector) : MarketRequest(url, apiKey, secretKey) {

    private val service: BinanceMarketService
    var balances: MutableMap<String, Double> = HashMap()

    init {
        algorithm = "HmacSHA256"
        path = ""
        service = initService(initRetrofit(initGson())) as BinanceMarketService
    }

    override fun initHttpClient(): OkHttpClient {


        return OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .proxySelector(proxySelector)
                .proxyAuthenticator(proxySelector.proxyAuthenticator)
                .build()
    }

    override fun getMarketName(): String {
        return Market.BINANCE_MARKET
    }

    override fun initGson(): Gson {
        return GsonBuilder()
                .registerTypeAdapter(BinanceResponse::class.java, AssetDetailDeserializer())
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
        params["timestamp"] = System.currentTimeMillis().toString()
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
        response.responsesList?.forEach { balanceResponse ->
            balances[(balanceResponse as BinanceBalanceResponse).coinName!!] = balanceResponse.amount
        }
        return balances[coinName] ?: 0.0
    }

    @Throws(BinanceException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: BinanceOrderBookResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["symbol"] = pairName
        params["limit"] = "10"
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
        return response.responsesList?.filterIsInstance<BinanceTickerResponse>()?: ArrayList()
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["timestamp"] = System.currentTimeMillis().toString()
        val headers: MutableMap<String, String> = HashMap()
        val hash = makeHash(params)
        headers["X-MBX-APIKEY"] = apiKey
        params["signature"] = hash
        val response: BinanceCurrenciesListResponse
        val call = service.getAssetDetails(params, headers)
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
        val call = service.getExchangeInfo()
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
        params["asset"] = coinName
        params["timestamp"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        headers["signature"] = hash
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
        params["asset"] = coinName
        params["address"] = address
        params["timestamp"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        headers["signature"] = hash
        val call = service.payment(
                params["amount"]!!,
                params["asset"]!!,
                params["address"]!!,
                params["timestamp"]!!,
                headers)
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
        params["timestamp"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        headers["signature"] = hash
        val call = service.newOrder(
                params["symbol"]!!,
                params["side"]!!,
                params["type"]!!,
                params["timeInForce"]!!,
                params["quantity"]!!,
                params["price"]!!,
                params["timestamp"]!!,
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
        params["timestamp"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        headers["signature"] = hash
        val response: BinanceResponse
        val call = service.getOpenOrders(params, headers)
        response = try {
            execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return HistoryResponseFactory(response.responsesList?.filterIsInstance<BinanceOrdersResponse>()).history
    }

    @Throws(MarketException::class)
    override fun getHistory(context: Context?): List<History> {
        if (keysIsEmpty()) return listOf(History())
        val startTime = (LocalDateTime.now().minusDays(29)
                .toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())) * 1000).toString()

        val historyList: MutableList<History> = ArrayList()
        getAllPairs(context).forEach{pair -> historyList.addAll(getOrdersHistory(pair, startTime))}

        historyList.addAll(getWithdrawHistory(startTime))
        historyList.addAll(getDepositHistory(startTime))

        return historyList
    }

    private fun getAllPairs(context: Context?): List<String> {
        val prefs = PairsPreferences(context)
        val pairsList = ArrayList<Pair>()
        prefs.items.forEach { item -> pairsList.add(Pair.fromPairName(item)) }

        return pairsList.map { pair: Pair -> pair.getPairNameForMarket(Market.BINANCE_MARKET) }
    }

    private fun getOrdersHistory(pairName: String, startTime: String) : List<History>{
        val params: MutableMap<String, String> = LinkedHashMap()
        params["symbol"] = pairName
        params["startTime"] = startTime
        params["timestamp"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        headers["signature"] = hash
        val response: BinanceResponse
        val call = service.getAllOrders(params, headers)
        try {
            response = execute(call) as BinanceResponse
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        }
        return HistoryResponseFactory(response.responsesList?.filterIsInstance<BinanceAllOrdersResponse>()?.filter { order -> order.status.equals("FILLED") }).history
    }

    @Throws(BinanceException::class)
    private fun getDepositHistory(startTime: String): List<History> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["timestamp"] = System.currentTimeMillis().toString()
        params["startTime"] = startTime
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        headers["signature"] = hash
        val response: BinanceDepositHistoryResponse
        val call = service.getDepositHistory(params, headers)
        response = try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        } as BinanceDepositHistoryResponse
        return HistoryResponseFactory(response.depositList).history
    }

    @Throws(BinanceException::class)
    private fun getWithdrawHistory(startTime: String): List<History> {
        val params: MutableMap<String, String> = LinkedHashMap()
        params["timestamp"] = System.currentTimeMillis().toString()
        params["startTime"] = startTime
        val hash = makeHash(params)
        val headers: MutableMap<String, String> = HashMap()
        headers["X-MBX-APIKEY"] = apiKey
        headers["signature"] = hash
        val response: BinanceWithdrawHistoryResponse
        val call = service.getWithdrawHistory(params, headers)
        response = try {
            execute(call)
        } catch (e: MarketException) {
            throw BinanceException(e.message!!)
        } as BinanceWithdrawHistoryResponse
        return HistoryResponseFactory(response.withdrawList).history
    }
}