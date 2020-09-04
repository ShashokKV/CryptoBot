package com.chess.cryptobot.model.response.bittrex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexWithdrawResponse : BittrexMarketResponse() {

    @SerializedName("status")
    @Expose
    val status: String? = null
}