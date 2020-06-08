package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.MarketResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class LivecoinResponse : MarketResponse {
    @SerializedName("success")
    @Expose
    private val success: Boolean? = null

    @SerializedName("errorMessage")
    @Expose
    private val errorMessage: String? = null

    @SerializedName("exception")
    @Expose
    private val exception: String? = null
    override fun success(): Boolean {
        return success ?: true
    }

    override fun message(): String? {
        return errorMessage ?: exception
    }
}