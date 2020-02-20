package com.chess.cryptobot.exceptions

class BinanceException(message: String) : MarketException("Binance: $message")