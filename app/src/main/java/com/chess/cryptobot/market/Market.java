package com.chess.cryptobot.market;

import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.TickerResponse;

import java.util.List;

public interface Market {
    String BITTREX_MARKET = "bittrex";
    String LIVECOIN_MARKET = "livecoin";

    String getMarketName();

    Double getAmount(String coinName) throws MarketException;

    OrderBookResponse getOrderBook(String pairName) throws MarketException;

    List<String> getAllMarkets() throws MarketException;

    List<? extends TickerResponse> getTicker() throws MarketException;

    boolean keysIsEmpty();
}
