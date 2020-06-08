package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.bittrex.BittrexResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface BittrexMarketService {
    @GET("account/getbalance")
    fun getBalance(@QueryMap options: Map<String, String>?, @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("public/getorderbook")
    fun getOrderBook(@QueryMap options: Map<String, String>): Call<BittrexResponse>

    @GET("public/getmarketsummaries")
    fun getTicker(): Call<BittrexResponse>

    @GET("public/getcurrencies")
    fun getCurrencies(): Call<BittrexResponse>

    @GET("public/getmarkets")
    fun getMarkets(): Call<BittrexResponse>

    @GET("account/getdepositaddress")
    fun getAddress(@QueryMap(encoded = true) options: Map<String, String>?, @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("account/withdraw")
    fun payment(@Query(value = "currency", encoded = true) marketName: String?,
                @Query(value = "quantity", encoded = true) quantity: String?,
                @Query(value = "address", encoded = true) rate: String?,
                @Query(value = "apikey", encoded = true) apikey: String?,
                @Query(value = "nonce", encoded = true) nonce: String?,
                @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("market/buylimit")
    fun buy(@Query(value = "market", encoded = true) marketName: String?,
            @Query(value = "quantity", encoded = true) quantity: String?,
            @Query(value = "rate", encoded = true) rate: String?,
            @Query(value = "apikey", encoded = true) apikey: String?,
            @Query(value = "nonce", encoded = true) nonce: String?,
            @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("market/selllimit")
    fun sell(@Query(value = "market", encoded = true) marketName: String?,
             @Query(value = "quantity", encoded = true) quantity: String?,
             @Query(value = "rate", encoded = true) rate: String?,
             @Query(value = "apikey", encoded = true) apikey: String?,
             @Query(value = "nonce", encoded = true) nonce: String?,
             @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("account/getorderhistory")
    fun getOrderHistory(@Query(value = "apikey", encoded = true) apikey: String?,
                        @Query(value = "nonce", encoded = true) nonce: String?,
                        @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("account/getwithdrawalhistory")
    fun getWithdrawHistory(@Query(value = "apikey", encoded = true) apikey: String?,
                           @Query(value = "nonce", encoded = true) nonce: String?,
                           @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("account/getdeposithistory")
    fun getDepositHistory(@Query(value = "apikey", encoded = true) apikey: String?,
                          @Query(value = "nonce", encoded = true) nonce: String?,
                          @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>

    @GET("market/getopenorders")
    fun getOpenOrders(@Query(value = "apikey", encoded = true) apikey: String?,
                      @Query(value = "nonce", encoded = true) nonce: String?,
                      @HeaderMap headers: Map<String?, String?>?): Call<BittrexResponse>
}