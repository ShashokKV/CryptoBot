package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.poloniex.LivecoinHistoryResponse
import com.chess.cryptobot.model.response.poloniex.LivecoinOrdersResponse
import com.chess.cryptobot.model.response.poloniex.PoloniexResponse
import retrofit2.Call
import retrofit2.http.*

interface PoloniexMarketService {
    @POST("tradingApi")
    fun getBalance(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<PoloniexResponse>

    @GET("public")
    fun getOrderBook(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>

    @GET("public")
    fun ticker(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>

    @GET("public")
    fun currencies(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>

    @GET("tradingApi")
    fun getAddress(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<PoloniexResponse>

    @POST("tradingApi")
    fun payment(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<PoloniexResponse>

    @POST("tradingApi")
    fun buy(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<PoloniexResponse>

    @POST("tradingApi")
    fun sell(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<PoloniexResponse>

    @GET("payment/history/transactions")
    fun getHistory(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>): Call<List<LivecoinHistoryResponse>>

    @GET("exchange/client_orders")
    fun getOpenOrders(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>?): Call<LivecoinOrdersResponse?>?
}