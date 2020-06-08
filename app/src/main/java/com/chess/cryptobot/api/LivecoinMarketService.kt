package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.livecoin.LivecoinAddressResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinBalanceResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinCurrenciesListResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinHistoryResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinOrdersResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinTickerResponse
import com.chess.cryptobot.model.response.livecoin.LivecoinTradeLimitResponse
import retrofit2.Call
import retrofit2.http.*

interface LivecoinMarketService {
    @GET("payment/balance")
    fun getBalance(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>?): Call<LivecoinBalanceResponse?>?

    @GET("exchange/order_book")
    fun getOrderBook(@QueryMap(encoded = true) options: Map<String, String>): Call<LivecoinOrderBookResponse>

    @get:GET("exchange/ticker")
    val ticker: Call<List<LivecoinTickerResponse>>

    @get:GET("info/coinInfo")
    val currencies: Call<LivecoinCurrenciesListResponse>

    @get:GET("exchange/restrictions")
    val minTradeSize: Call<LivecoinTradeLimitResponse>

    @GET("payment/get/address")
    fun getAddress(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>?): Call<LivecoinAddressResponse?>?

    @FormUrlEncoded
    @POST("payment/out/coin")
    fun payment(@Field(value = "amount", encoded = true) amount: String?,
                @Field(value = "currency", encoded = true) currency: String?,
                @Field(value = "wallet", encoded = true) wallet: String?,
                @HeaderMap headers: Map<String?, String?>?): Call<LivecoinResponse?>?

    @FormUrlEncoded
    @POST("exchange/buylimit")
    fun buy(@Field(value = "currencyPair", encoded = true) currencyPair: String?,
            @Field(value = "price", encoded = true) price: String?,
            @Field(value = "quantity", encoded = true) quantity: String?,
            @HeaderMap headers: Map<String, String>?): Call<LivecoinResponse?>?

    @FormUrlEncoded
    @POST("exchange/selllimit")
    fun sell(@Field(value = "currencyPair", encoded = true) currencyPair: String?,
             @Field(value = "price", encoded = true) price: String?,
             @Field(value = "quantity", encoded = true) quantity: String?,
             @HeaderMap headers: Map<String, String>?): Call<LivecoinResponse?>?

    @GET("payment/history/transactions")
    fun getHistory(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>): Call<List<LivecoinHistoryResponse>>

    @GET("exchange/client_orders")
    fun getOpenOrders(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String, String>?): Call<LivecoinOrdersResponse?>?
}