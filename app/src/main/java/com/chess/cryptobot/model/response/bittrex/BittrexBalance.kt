package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.BalanceResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexBalance : BalanceResponse, BittrexMarketResponse() {
    @SerializedName("available")
    @Expose
    override val amount: Double = 0.0
}