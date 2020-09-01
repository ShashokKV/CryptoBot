package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.response.TradeLimitResponse

class BittrexTradeLimit: TradeLimitResponse {

    var limits: List<BittrexLimits> = ArrayList()

    override fun getTradeLimitByName(pairName: String?): Double? {
        for (limit in limits) {
            if (limit.symbol == pairName) {
                return limit.minTradeSize
            }
        }
        return null
    }
}