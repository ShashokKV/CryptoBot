package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.MarketResponse
import com.google.gson.JsonArray
import com.google.gson.JsonObject

open class PoloniexResponse : MarketResponse {
    var error: String? = null

    override fun success(): Boolean {
        return error==null
    }

    override fun message(): String {
        return error ?: "no message"
    }

    var objectData: JsonObject? = null

    var arrayData: JsonArray? = null
}