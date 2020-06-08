package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.TradeLimitResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LivecoinTradeLimitResponse : LivecoinResponse(), TradeLimitResponse {
    @SerializedName("restrictions")
    @Expose
    private val restrictions: List<Restriction>? = null
    override fun getTradeLimitByName(pairName: String?): Double? {
        for (restriction in restrictions!!) {
            if (restriction.currencyPair == pairName) {
                return restriction.minLimitQuantity
            }
        }
        return null
    }

    internal inner class Restriction {
        @SerializedName("currencyPair")
        @Expose
        val currencyPair: String? = null

        @SerializedName("minLimitQuantity")
        @Expose
        val minLimitQuantity: Double? = null

    }
}