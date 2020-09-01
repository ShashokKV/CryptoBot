package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.CurrenciesResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexCurrency : CurrenciesResponse, BittrexMarketResponse() {
    @SerializedName("txFee")
    @Expose
    private val txFee: Double? = null

    @SerializedName("symbol")
    @Expose
    override val currencyName: String? = null

    @SerializedName("status")
    @Expose
    private val status: String? = null

    @SerializedName("logoUrl")
    @Expose
    val logoUrl: String? = null

    override val isActive: Boolean?
        get() = status?.equals("ONLINE") ?: false

    override val fee: Double
        get() = txFee ?: 0.0

}