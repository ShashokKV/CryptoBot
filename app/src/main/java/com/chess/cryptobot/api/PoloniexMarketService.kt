package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.livecoin.LivecoinAddressResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinHistoryResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinOrdersResponse
import com.chess.cryptobot.model.response.livecoin.PoloniexResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinTradeLimitResponse
import retrofit2.Call
import retrofit2.http.*

interface PoloniexMarketService {
    @POST("tradingApi")
    fun getBalance(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>?): Call<PoloniexResponse?>?

    @GET("public")
    fun getOrderBook(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>

    @GET("public")
    fun ticker(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>

    @GET("public")
    fun currencies(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>

    @get:GET("exchange/restrictions")
    val minTradeSize: Call<LivecoinTradeLimitResponse>

    @GET("payment/get/address")
    fun getAddress(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>?): Call<LivecoinAddressResponse?>?

    @FormUrlEncoded
    @POST("payment/out/coin")
    fun payment(@Field(value = "amount", encoded = true) amount: String?,
                @Field(value = "currency", encoded = true) currency: String?,
                @Field(value = "wallet", encoded = true) wallet: String?,
                @HeaderMap headers: Map<String?, String?>?): Call<PoloniexResponse?>?

    @FormUrlEncoded
    @POST("exchange/buylimit")
    fun buy(@Field(value = "currencyPair", encoded = true) currencyPair: String?,
            @Field(value = "price", encoded = true) price: String?,
            @Field(value = "quantity", encoded = true) quantity: String?,
            @HeaderMap headers: Map<String, String>?): Call<PoloniexResponse?>?

    @FormUrlEncoded
    @POST("exchange/selllimit")
    fun sell(@Field(value = "currencyPair", encoded = true) currencyPair: String?,
             @Field(value = "price", encoded = true) price: String?,
             @Field(value = "quantity", encoded = true) quantity: String?,
             @HeaderMap headers: Map<String, String>?): Call<PoloniexResponse?>?

    @GET("payment/history/transactions")
    fun getHistory(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>): Call<List<LivecoinHistoryResponse>>

    @GET("exchange/client_orders")
    fun getOpenOrders(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>?): Call<LivecoinOrdersResponse?>?
}