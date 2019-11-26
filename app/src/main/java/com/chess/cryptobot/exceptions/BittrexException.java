package com.chess.cryptobot.exceptions;

public class BittrexException extends MarketException {

    public BittrexException(String message) {
        super("Bittrex: " + message);
    }
}
