package com.chess.cryptobot.model.response.binance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BinanceBalance {
    @SerializedName("coin")
    @Expose
    val coinName: String? = null

    @SerializedName("free")
    @Expose
    val amount: Double = 0.0
}