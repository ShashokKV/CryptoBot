package com.chess.cryptobot.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String = "no error Message"
        get() = message?:msg?:code?:field
    @SerializedName("message")
    @Expose
    private val message: String? = null
    @SerializedName("msg")
    @Expose
    private val msg: String? = null
    @SerializedName("code")
    @Expose
    private val code: String? = null

}