package com.chess.cryptobot.model.response;

import com.chess.cryptobot.model.Price;

import java.util.List;

public interface OrderBookResponse {

    List<Price> bids();
    List<Price> asks();
}
