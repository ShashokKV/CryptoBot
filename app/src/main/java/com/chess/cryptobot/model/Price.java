package com.chess.cryptobot.model;

public class Price {
    private final Double value;
    private final Double quantity;

    public Price(Double value, Double quantity) {
        this.value = value;
        this.quantity = quantity;
    }

    public Double getValue() {
        return value;
    }

    public Double getQuantity() {
        return quantity;
    }
}
