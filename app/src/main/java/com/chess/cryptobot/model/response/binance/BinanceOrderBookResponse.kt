package com.chess.cryptobot.model.response.binance

import com.chess.cryptobot.model.Price
import com.chess.cryptobot.model.response.OrderBookResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class BinanceOrderBookResponse : BinanceResponse(), OrderBookResponse {
    @SerializedName("asks")
    @Expose
    private val asks: List<List<String>>? = null
    @SerializedName("bids")
    @Expose
    private val bids: List<List<String>>? = null

    override fun bids(): List<Price?>? {
        return parseValues(bids)
    }

    override fun asks(): List<Price?>? {
        return parseValues(asks)
    }

    private fun parseValues(values: List<List<String>>?): List<Price?> {
        val prices: MutableList<Price?> = ArrayList()
        values!!.forEach{ value: List<String> -> prices.add(Price(value[0].toDouble(), value[1].toDouble())) }
        return prices
    }
}