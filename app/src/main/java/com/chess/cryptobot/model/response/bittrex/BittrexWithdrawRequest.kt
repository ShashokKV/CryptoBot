package com.chess.cryptobot.model.response.bittrex

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexWithdrawRequest {

    @SerializedName("currencySymbol")
    @Expose
    var currencySymbol: String? = null

    @SerializedName("quantity")
    @Expose
    var quantity: Double = 0.0

    @SerializedName("cryptoAddress")
    @Expose
    var cryptoAddress: String? = null
}