package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.AddressResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BinanceAddressResponse : BinanceResponse(), AddressResponse {
    @SerializedName("wallet")
    @Expose
    override val address: String? = null

}