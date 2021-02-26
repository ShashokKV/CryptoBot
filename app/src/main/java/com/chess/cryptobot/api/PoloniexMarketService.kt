package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.poloniex.PoloniexResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface PoloniexMarketService {
    @POST("tradingApi")
    fun privateApi(@QueryMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<PoloniexResponse>

    @GET("public")
    fun publicApi(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>
}