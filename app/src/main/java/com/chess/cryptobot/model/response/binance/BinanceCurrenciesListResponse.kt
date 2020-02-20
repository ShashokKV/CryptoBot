package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.response.CurrenciesListResponse
import com.chess.cryptobot.model.response.CurrenciesResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class BinanceCurrenciesListResponse : BinanceResponse(), CurrenciesListResponse {

    @SerializedName("info")
    @Expose
    var info: Array<Info>? = null

    override fun getCurrencies(): List<CurrenciesResponse> {
        return if (info == null) ArrayList() else listOf(*info!!)
    }

    class Info : CurrenciesResponse {
        @SerializedName("symbol")
        @Expose
        override val currencyName: String? = null
        @SerializedName("walletStatus")
        @Expose
        private val walletStatus: String? = null
        @SerializedName("withdrawFee")
        @Expose
        private val withdrawFee: Double? = null

        override val isActive: Boolean
            get() = if (walletStatus == null) false else walletStatus == "normal"

        override val fee: Double?
            get() = withdrawFee ?: withdrawFee
    }
}
