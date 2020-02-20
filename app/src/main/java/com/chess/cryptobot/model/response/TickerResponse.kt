package com.chess.cryptobot.model.response

interface TickerResponse {
    val marketName: String
    val tickerBid: Double?
    val tickerAsk: Double?
    val volume: Double
}