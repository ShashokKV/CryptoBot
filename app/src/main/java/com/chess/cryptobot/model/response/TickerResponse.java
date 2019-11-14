package com.chess.cryptobot.model.response;

public interface TickerResponse {
    
    String getTickerName();
    
    Double getTickerBid();
    
    Double getTickerAsk();

    Double getVolume();
}
