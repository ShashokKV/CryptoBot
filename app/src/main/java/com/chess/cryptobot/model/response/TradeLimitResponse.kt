package com.chess.cryptobot.model.response

interface TradeLimitResponse {
    fun getTradeLimitByName(pairName: String?): Double?
}