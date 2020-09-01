package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.AddressResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexAddress: AddressResponse, BittrexMarketResponse() {

    @SerializedName("cryptoAddress")
    @Expose
    override val address: String? = null
}