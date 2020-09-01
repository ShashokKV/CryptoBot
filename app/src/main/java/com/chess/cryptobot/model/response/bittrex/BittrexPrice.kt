package com.chess.cryptobot.model.response.bittrex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexPrice {
    @SerializedName("quantity")
    @Expose
    val quantity: Double = 0.0
    @SerializedName("rate")
    @Expose
    val rate: Double = 0.0

}