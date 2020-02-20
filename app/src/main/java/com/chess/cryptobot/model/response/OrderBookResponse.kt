package com.chess.cryptobot.model.response

import com.chess.cryptobot.model.Price

interface OrderBookResponse {
    fun bids(): List<Price?>?
    fun asks(): List<Price?>?
}