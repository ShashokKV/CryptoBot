package com.chess.cryptobot.model.response.bittrex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexOrder {

    @SerializedName("marketSymbol")
    @Expose
    var marketSymbol: String? = null

    @SerializedName("direction")
    @Expose
    var direction: String? = null

    @SerializedName("type")
    @Expose
    private val type: String = "LIMIT"

    @SerializedName("quantity")
    @Expose
    var quantity: Double = 0.0

    @SerializedName("limit")
    @Expose
    var limit: Double = 0.0

    @SerializedName("useAwards")
    @Expose
    private val useAwards: Boolean = true

    @SerializedName("timeInForce")
    @Expose
    private val timeInForce: String = "GOOD_TIL_CANCELLED"
}