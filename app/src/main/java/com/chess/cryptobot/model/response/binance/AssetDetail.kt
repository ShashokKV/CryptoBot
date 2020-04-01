package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.CurrenciesResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AssetDetail: CurrenciesResponse {
    override var currencyName: String? = null
    @Expose
    @SerializedName("depositStatus")
    val depositStatus: Boolean = false
    @Expose
    @SerializedName("withdrawStatus")
    val withdrawStatus: Boolean = false

    override val isActive: Boolean
        get() = depositStatus && withdrawStatus
    @Expose
    @SerializedName("withdrawFee")
    override val fee: Double = 0.0
}