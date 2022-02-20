package com.chess.cryptobot.api

import com.chess.cryptobot.model.response.poloniex.PoloniexResponse
import retrofit2.Call
import retrofit2.http.*

interface PoloniexMarketService {
    @FormUrlEncoded
    @POST("tradingApi")
    fun privateApi(@FieldMap(encoded = true) options: Map<String, String>, @HeaderMap headers: Map<String, String>): Call<PoloniexResponse>

    @GET("public")
    fun publicApi(@QueryMap(encoded = true) options: Map<String, String>): Call<PoloniexResponse>
}