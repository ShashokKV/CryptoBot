package com.chess.cryptobot.model.response.poloniex

import com.chess.cryptobot.model.response.TradeLimitResponse

class PoloniexTradeLimitResponse : TradeLimitResponse {
    override fun getTradeLimitByName(pairName: String?): Double {
       return 0.0
    }
}