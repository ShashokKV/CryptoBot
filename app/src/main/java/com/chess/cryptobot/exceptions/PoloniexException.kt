package com.chess.cryptobot.exceptions

class PoloniexException(message: String) : MarketException("Poloniex: $message")