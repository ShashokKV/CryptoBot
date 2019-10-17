package com.chess.cryptobot.market;

import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.OrderBookResponse;

public interface Market {

    Double getAmount(String coinName) throws MarketException;

    OrderBookResponse getOrderBook(String pairName) throws MarketException;
}
