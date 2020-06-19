package com.chess.cryptobot.market

import android.content.Context
import com.chess.cryptobot.api.BittrexMarketService
import com.chess.cryptobot.exceptions.BittrexException
import com.chess.cryptobot.exceptions.MarketException
import com.chess.cryptobot.model.History
import com.chess.cryptobot.model.response.CurrenciesResponse
import com.chess.cryptobot.model.response.OrderBookResponse
import com.chess.cryptobot.model.response.TickerResponse
import com.chess.cryptobot.model.response.TradeLimitResponse
import com.chess.cryptobot.model.response.bittrex.BittrexResponse
import com.chess.cryptobot.model.response.bittrex.BittrexTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

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
                .registerTypeAdapter(BittrexResponse::class.java, BittrexTypeAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create()
    }

    override fun initService(retrofit: Retrofit): Any {
        return retrofit.create(BittrexMarketService::class.java)
    }

    @Throws(BittrexException::class)
    override fun getAmount(coinName: String): Double {
        if (keysIsEmpty()) return 0.0
        path = url + "account/getbalance?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currency"] = coinName
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val response: BittrexResponse
        response = try {
            val call = service.getBalance(params, headers)
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.amount
    }

    @Throws(MarketException::class)
    override fun getAddress(coinName: String): String? {
        if (keysIsEmpty()) return null
        path = url + "account/getdepositaddress?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currency"] = coinName
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val response: BittrexResponse
        response = try {
            val call = service.getAddress(params, headers)
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.address
    }

    @Throws(BittrexException::class)
    override fun getOrderBook(pairName: String): OrderBookResponse {
        val response: BittrexResponse
        val params: MutableMap<String, String> = LinkedHashMap()
        params["market"] = pairName
        params["type"] = "both"
        response = try {
            val call = service.getOrderBook(params)
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response
    }

    @Throws(MarketException::class)
    override fun getTicker(): List<TickerResponse> {
        val response: BittrexResponse
        val call = service.getTicker()
        response = try {
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.tickers
    }

    @Throws(MarketException::class)
    override fun getCurrencies(): List<CurrenciesResponse> {
        val response: BittrexResponse
        val call = service.getCurrencies()
        response = try {
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.getCurrencies()
    }

    @Throws(MarketException::class)
    override fun getMinQuantity(): TradeLimitResponse {
        return markets
    }

    @get:Throws(BittrexException::class)
    val markets: BittrexResponse
        get() {
            val response: BittrexResponse
            val call = service.getMarkets()
            response = try {
                execute(call) as BittrexResponse
            } catch (e: MarketException) {
                throw BittrexException(e.message!!)
            }
            return response
        }

    @Throws(MarketException::class)
    override fun sendCoins(coinName: String, amount: Double, address: String) {
        if (keysIsEmpty()) return
        path = url + "account/withdraw?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["currency"] = coinName
        params["quantity"] = String.format(Locale.US, "%.8f", amount)
        params["address"] = address
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val call = service.payment(
                params["currency"],
                params["quantity"],
                params["address"],
                params["apikey"],
                params["nonce"],
                headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun buy(pairName: String, price: Double, amount: Double) {
        if (keysIsEmpty()) return
        path = url + "market/buylimit?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["market"] = pairName
        params["quantity"] = String.format(Locale.US, "%.5f", amount)
        params["rate"] = String.format(Locale.US, "%.8f", price)
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val call = service.buy(
                params["market"],
                params["quantity"],
                params["rate"],
                params["apikey"],
                params["nonce"],
                headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun sell(pairName: String, price: Double, amount: Double) {
        if (keysIsEmpty()) return
        path = url + "market/selllimit?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["market"] = pairName
        params["quantity"] = String.format(Locale.US, "%.5f", amount)
        params["rate"] = String.format(Locale.US, "%.8f", price)
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val call = service.sell(
                params["market"],
                params["quantity"],
                params["rate"],
                params["apikey"],
                params["nonce"],
                headers)
        try {
            execute(call)
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
    }

    @Throws(MarketException::class)
    override fun getOpenOrders(): List<History> {
        if (keysIsEmpty()) return listOf(History())
        path = url + "market/getopenorders?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val response: BittrexResponse
        val call = service.getOpenOrders(
                params["apikey"],
                params["nonce"],
                headers)
        response = try {
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.history
    }

    @Throws(MarketException::class)
    override fun getHistory(context: Context?): List<History> {
        if (keysIsEmpty()) return listOf(History())
        path = url + "account/getorderhistory?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val historyList: MutableList<History>
        val response: BittrexResponse
        val call = service.getOrderHistory(
                params["apikey"],
                params["nonce"],
                headers)
        try {
            response = execute(call) as BittrexResponse
            historyList = ArrayList(response.history)
            historyList.addAll(getWithdrawHistory())
            historyList.addAll(getDepositHistory())
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return historyList
    }

    override fun getDepositHistory(): List<History> {
        if (keysIsEmpty()) return ArrayList()
        path = url + "account/getdeposithistory?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val response: BittrexResponse
        val call = service.getDepositHistory(
                params["apikey"],
                params["nonce"],
                headers)
        response = try {
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.history
    }

    override fun getWithdrawHistory(): List<History> {
        if (keysIsEmpty()) return ArrayList()
        path = url + "account/getwithdrawalhistory?"
        val params: MutableMap<String, String> = LinkedHashMap()
        params["apikey"] = apiKey
        params["nonce"] = System.currentTimeMillis().toString()
        val hash = makeHash(params)
        val headers: MutableMap<String?, String?> = HashMap()
        headers["apisign"] = hash
        val response: BittrexResponse
        val call = service.getWithdrawHistory(
                params["apikey"],
                params["nonce"],
                headers)
        response = try {
            execute(call) as BittrexResponse
        } catch (e: MarketException) {
            throw BittrexException(e.message!!)
        }
        return response.history
    }
}