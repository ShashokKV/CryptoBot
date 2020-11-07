package com.chess.cryptobot.model.websocket.binance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Ticker(
        @SerializedName("e")
        @Expose
        val eventType: String,

        @SerializedName("E")
        @Expose
        val eventTime: Long,

        @SerializedName("s")
        @Expose
        val symbol: String,

        @SerializedName("t")
        @Expose
        val tradeId: Long,

        @SerializedName("p")
        @Expose
        val price: String,

        @SerializedName("q")
        @Expose
        val quantity: String,

        @SerializedName("b")
        @Expose
        val buyerId: Int,

        @SerializedName("a")
        @Expose
        val sellerId: Int,

        @SerializedName("T")
        @Expose
        val tradeTime: Long,

        @SerializedName("m")
        @Expose
        val isMaker: Boolean,

        @SerializedName("M")
        @Expose
        val ignore: Boolean
) {
    override fun toString(): String {
        return "Ticker(eventType='$eventType', eventTime=$eventTime, symbol='$symbol', tradeId=$tradeId, price='$price', quantity='$quantity', buyerId=$buyerId, sellerId=$sellerId, tradeTime=$tradeTime, isMaker=$isMaker, ignore=$ignore)"
    }
}