package com.chess.cryptobot.model.response.bittrex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexWithdraw : BittrexMarketResponse() {

    @SerializedName("status")
    @Expose
    val status: String? = null
}