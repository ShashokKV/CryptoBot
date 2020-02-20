package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.BalanceResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BinanceBalanceResponse : BinanceResponse(), BalanceResponse {
    @SerializedName("value")
    @Expose
    override val amount: Double = 0.0

}