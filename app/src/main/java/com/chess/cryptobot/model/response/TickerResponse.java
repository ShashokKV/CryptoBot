package com.chess.cryptobot.model.response;

public interface TickerResponse {

    String getMarketName();

    Double getTickerBid();

    Double getTickerAsk();

    Double getVolume();
}
