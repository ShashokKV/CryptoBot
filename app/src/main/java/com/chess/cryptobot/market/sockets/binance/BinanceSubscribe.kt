package com.chess.cryptobot.market.sockets.binance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BinanceSubscribe(
    @SerializedName("method")
    @Expose
    val method: String = "SUBSCRIBE",

    @SerializedName("params")
    @Expose
    var params: List<String>,

    @SerializedName("id")
    @Expose
    val id: Int


) {
    override fun toString(): String {
        return "Subscribe(method='$method', params=$params, id=$id)"
    }
}