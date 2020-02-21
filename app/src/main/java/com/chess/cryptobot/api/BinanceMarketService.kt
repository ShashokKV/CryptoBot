package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.binance.*
import retrofit2.Call
import retrofit2.http.*

interface BinanceMarketService {
    @GET("payment/balance")
    fun getBalance(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceBalanceResponse>

    @GET("exchange/order_book")
    fun getOrderBook(@QueryMap(encoded = true) options: Map<String, String>): Call<BinanceOrderBookResponse>

    @GET("exchange/ticker")
    fun getTicker(): Call<List<BinanceTickerResponse>>

    @GET("info/coinInfo")
    fun getCurrencies(): Call<BinanceCurrenciesListResponse>

    @GET("exchange/restrictions")
    fun getMinTradeSize(): Call<BinanceTradeLimitResponse>

    @GET("payment/get/address")
    fun getAddress(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceAddressResponse>

    @FormUrlEncoded
    @POST("payment/out/coin")
    fun payment(@Field(value = "amount", encoded = true) amount: String,
                @Field(value = "currency", encoded = true) currency: String,
                @Field(value = "wallet", encoded = true) wallet: String,
                @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @FormUrlEncoded
    @POST("exchange/buylimit")
    fun buy(@Field(value = "currencyPair", encoded = true) currencyPair: String,
            @Field(value = "price", encoded = true) price: String,
            @Field(value = "quantity", encoded = true) quantity: String,
            @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @FormUrlEncoded
    @POST("exchange/selllimit")
    fun sell(@Field(value = "currencyPair", encoded = true) currencyPair: String,
             @Field(value = "price", encoded = true) price: String,
             @Field(value = "quantity", encoded = true) quantity: String,
             @HeaderMap headers: Map<String, String>): Call<BinanceResponse>

    @GET("payment/history/transactions")
    fun getHistory(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<List<BinanceHistoryResponse>>

    @GET("exchange/client_orders")
    fun getOpenOrders(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<BinanceOrdersResponse>
}