package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.BalanceResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LivecoinBalanceResponse : LivecoinResponse(), BalanceResponse {
    @SerializedName("value")
    @Expose
    override val amount: Double? = null
}