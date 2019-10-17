package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.model.Price;
import com.chess.cryptobot.model.response.BalanceResponse;
import com.chess.cryptobot.model.response.MarketResponse;
import com.chess.cryptobot.model.response.OrderBookResponse;

import java.util.ArrayList;
import java.util.List;

public class BittrexResponse implements MarketResponse, BalanceResponse, OrderBookResponse {

    private Boolean success;
    public String message;
    private BittrexGenericResponse[] results;

    BittrexResponse(BittrexGenericResponse[] results) {
        this.results = results;
    }

    BittrexResponse(BittrexGenericResponse result) {
        this.results = new BittrexGenericResponse[1];
        results[0] = result;
    }

    void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public Double getAmount() {
        return results[0].getAvailable();
    }

    @Override
    public String message() {
        return message;
    }


    @Override
    public List<Price> bids() {
        return parsePrices(results[0].getBuy());
    }

    @Override
    public List<Price> asks() {
        return parsePrices(results[0].getSell());
    }

    private List<Price> parsePrices(List<BittrexPrice> prices) {
        ArrayList<Price> parsedPrices = new ArrayList<>();
        prices.forEach(price -> parsedPrices.add(new Price(price.getRate(), price.getQuantity())));
        return parsedPrices;
    }
}