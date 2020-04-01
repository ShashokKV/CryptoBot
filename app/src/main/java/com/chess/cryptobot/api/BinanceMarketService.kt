package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.binance.*
import retrofit2.Call
import retrofit2.http.*

interface BinanceMarketService {
    @GET("sapi/v1/capital/config/getall")
    fun getBalance(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/depth")
    fun getOrderBook(@QueryMap(encoded = true) options: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/ticker/24hr")
    fun getTicker(): Call<BinanceResponse>

    @GET("wapi/v3/assetDetail.html")
    fun getAssetDetails(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/exchangeInfo")
    fun getExchangeInfo(): Call<BinanceResponse>

    @GET("wapi/v3/depositAddress.html")
    fun getAddress(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @FormUrlEncoded
    @POST("wapi/v3/withdraw.html")
    fun payment(@Field(value = "amount", encoded = true) amount: String,
                @Field(value = "asset", encoded = true) asset: String,
                @Field(value = "address", encoded = true) address: String,
                @Field(value = "timestamp", encoded = true) timestamp: String,
                @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @FormUrlEncoded
    @POST("api/v3/order")
    fun newOrder(@Field(value = "symbol", encoded = true) symbol: String,
                 @Field(value = "side", encoded = true) side: String,
                 @Field(value = "type", encoded = true) type: String,
                 @Field(value = "timeInForce", encoded = true) timeInForce: String,
                 @Field(value = "quantity", encoded = true) quantity: String,
                 @Field(value = "price", encoded = true) price: String,
                 @Field(value = "timestamp", encoded = true) timestamp: String,
                 @Field(value = "signature", encoded = true) signature: String,
                 @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("wapi/v3/depositHistory.html")
    fun getDepositHistory(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("wapi/v3/withdrawHistory.html")
    fun getWithdrawHistory(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/allOrders")
    fun getAllOrders(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/openOrders")
    fun getOpenOrders(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>
}