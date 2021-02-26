package com.chess.cryptobot.model.response.poloniex

import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.OrderBookResponse

class PoloniexOrderBookResponse(poloniexResponse: PoloniexResponse) : OrderBookResponse {

    private var asks: MutableList<Price> = mutableListOf()

    private var bids: MutableList<Price> = mutableListOf()

    override fun bids(): List<Price?> {
        return bids
    }

    override fun asks(): List<Price?> {
        return asks
    }

    init {
        poloniexResponse.data?.get("asks")?.asJsonArray?.forEach { ask ->
            asks.add(Price(ask.asJsonArray[0].asDouble, ask.asJsonArray[1].asDouble))
        }
        poloniexResponse.data?.get("bids")?.asJsonArray?.forEach { ask ->
            bids.add(Price(ask.asJsonArray[0].asDouble, ask.asJsonArray[1].asDouble))
        }
    }
}