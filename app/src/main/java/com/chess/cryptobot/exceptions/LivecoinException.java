package com.chess.cryptobot.exceptions;

public class LivecoinException extends MarketException {

    public LivecoinException(String message) {
        super("Livecoin: " + message);
    }
}
