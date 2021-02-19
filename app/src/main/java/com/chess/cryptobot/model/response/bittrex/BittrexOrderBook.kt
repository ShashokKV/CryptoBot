package com.chess.cryptobot.model.response.bittrex

import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.OrderBookResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BittrexOrderBook: OrderBookResponse, BittrexMarketResponse() {

    @SerializedName("bid")
    @Expose
    val bid: List<BittrexPrice>? = null
    @SerializedName("ask")
    @Expose
    val ask: List<BittrexPrice>? = null

    override fun bids(): List<Price?> {
        return parsePrices(bid)
    }

    override fun asks(): List<Price?> {
        return parsePrices(ask)
    }

    private fun parsePrices(prices: List<BittrexPrice>?): List<Price?> {
        val parsedPrices = ArrayList<Price?>()
        prices!!.forEach{ price: BittrexPrice -> parsedPrices.add(Price(price.rate, price.quantity)) }
        return parsedPrices
    }
}