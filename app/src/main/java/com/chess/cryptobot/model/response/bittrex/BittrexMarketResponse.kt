package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.MarketResponse

open class BittrexMarketResponse: MarketResponse {
    override fun success(): Boolean {
        return true
    }

    override fun message(): String {
        return ""
    }
}