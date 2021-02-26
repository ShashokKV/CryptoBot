package com.chess.cryptobot.model.response.poloniex

import com.chess.cryptobot.model.response.MarketResponse
import com.google.gson.JsonObject

open class PoloniexResponse : MarketResponse {
    var error: String? = null

    override fun success(): Boolean {
        return error==null
    }

    override fun message(): String {
        return error ?: "no message"
    }

    var data: JsonObject? = null
}