package com.chess.cryptobot.exceptions

class BittrexException(message: String) : MarketException("Bittrex: $message")