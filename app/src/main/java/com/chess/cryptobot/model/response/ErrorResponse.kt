package com.chess.cryptobot.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String? = null
        get() = field ?: message ?: msg
    @SerializedName("message")
    @Expose
    private val message: String? = null
    @SerializedName("msg")
    @Expose
    private val msg: String? = null

}