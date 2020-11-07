package com.chess.cryptobot.model.websocket.binance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubscribeResponse(
        @SerializedName("result")
        @Expose
        val result: String,

        @SerializedName("id")
        @Expose
        val id: Int
)