package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.bittrex.*
import retrofit2.Call
import retrofit2.http.*

interface BittrexMarketService {
    @GET("balances/{coinName}")
    fun getBalance(@Path("coinName") coinName: String, @HeaderMap headers: Map<String, String>): Call<BittrexBalance>

    @GET("markets/{marketSymbol}/orderbook")
    fun getOrderBook(@Path("marketSymbol") pairName: String): Call<BittrexOrderBook>

    @GET("markets/tickers")
    fun getTicker(): Call<List<BittrexTicker>>

    @GET("currencies")
    fun getCurrencies(): Call<List<BittrexCurrency>>

    @GET("markets")
    fun getMarkets(): Call<List<BittrexLimits>>

    @GET("addresses/{currencySymbol}")
    fun getAddress(@Path("currencySymbol") coinName: String, @HeaderMap headers: Map<String, String>): Call<BittrexAddress>

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("withdrawals")
    fun payment(@Body body: BittrexWithdrawRequest, @HeaderMap headers: Map<String, String>): Call<BittrexWithdrawResponse>

    @Headers("Content-Type: application/json; charset=UTF-8")
    @POST("orders")
    fun order(@Body body: BittrexOrder, @HeaderMap headers: Map<String, String>): Call<BittrexMarketResponse>

    @GET("orders/open")
    fun getOpenOrders(@HeaderMap headers: Map<String, String>): Call<List<BittrexHistory>>

    @GET("orders/closed")
    fun getOrderHistory(@HeaderMap headers: Map<String, String>): Call<List<BittrexHistory>>

    @GET("withdrawals/closed")
    fun getWithdrawHistory(@HeaderMap headers: Map<String, String>): Call<List<BittrexHistory>>

    @GET("deposits/closed")
    fun getDepositHistory(@HeaderMap headers: Map<String, String>): Call<List<BittrexHistory>>


}