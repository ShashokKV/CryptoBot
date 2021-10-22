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

    @GET("sapi/v1/asset/assetDetail")
    fun getAssetDetails(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/exchangeInfo")
    fun getExchangeInfo(): Call<BinanceResponse>

    @GET("sapi/v1/capital/deposit/address")
    fun getAddress(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @FormUrlEncoded
    @POST("sapi/v1/capital/withdraw/apply")
    fun payment(@FieldMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @FormUrlEncoded
    @POST("api/v3/order")
    fun newOrder(@FieldMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("sapi/v1/capital/deposit/hisrec")
    fun getDepositHistory(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("sapi/v1/capital/withdraw/history")
    fun getWithdrawHistory(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/allOrders")
    fun getAllOrders(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("api/v3/openOrders")
    fun getOpenOrders(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceResponse>
}