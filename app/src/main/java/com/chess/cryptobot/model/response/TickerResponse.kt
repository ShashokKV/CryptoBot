package com.chess.cryptobot.model.response

interface TickerResponse {
    val tickerName: String
    val tickerBid: Double?
    val tickerAsk: Double?
    val volume: Double
}