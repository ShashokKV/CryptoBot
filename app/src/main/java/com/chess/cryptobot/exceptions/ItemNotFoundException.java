package com.chess.cryptobot.exceptions;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String message) {
        super(message.concat(" not found in adapter items"));
    }
}
