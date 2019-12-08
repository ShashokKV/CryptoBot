package com.chess.cryptobot.market;

import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.History;
import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.TickerResponse;
import com.chess.cryptobot.model.response.TradeLimitResponse;

import java.util.List;

public interface Market {
    String BITTREX_MARKET = "bittrex";
    String LIVECOIN_MARKET = "livecoin";

    String getMarketName();

    Double getAmount(String coinName) throws MarketException;

    OrderBookResponse getOrderBook(String pairName) throws MarketException;

    List<? extends TickerResponse> getTicker() throws MarketException;

    List<CurrenciesResponse> getCurrencies() throws MarketException;

    TradeLimitResponse getMinQuantity() throws MarketException;

    String getAddress(String coinName) throws MarketException;

    void sendCoins(String coinName, Double amount, String address) throws MarketException;

    void buy(String pairName, Double price, Double amount) throws MarketException;

    void sell(String pairName, Double price, Double amount) throws MarketException;

    boolean keysIsEmpty();

    List<History> getOpenOrders() throws MarketException;

    List<History> getHistory() throws MarketException;
}
