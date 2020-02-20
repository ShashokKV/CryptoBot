package com.chess.cryptobot.model.response.bittrex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexPrice {
    @SerializedName("Quantity")
    @Expose
    val quantity: Double? = null
    @SerializedName("Rate")
    @Expose
    val rate: Double? = null

}