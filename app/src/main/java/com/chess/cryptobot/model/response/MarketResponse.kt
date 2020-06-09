package com.chess.cryptobot.model.response

interface MarketResponse {
    fun success(): Boolean
    fun message(): String
}