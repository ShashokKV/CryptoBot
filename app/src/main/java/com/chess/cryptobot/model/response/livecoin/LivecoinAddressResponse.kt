package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.AddressResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LivecoinAddressResponse : LivecoinResponse(), AddressResponse {
    @SerializedName("wallet")
    @Expose
    override val address: String? = null
}