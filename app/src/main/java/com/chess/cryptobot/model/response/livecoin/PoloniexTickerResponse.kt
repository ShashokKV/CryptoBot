package com.chess.cryptobot.model.response.livecoin

import com.chess.cryptobot.model.response.TickerResponse

class PoloniexTickerResponse(private val symbol: String,
                             override val tickerBid: Double,
                             override val tickerAsk: Double) : TickerResponse {

    override val tickerName: String
        get() {
            return symbol.replace("_", "/")
        }


}