package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.Price;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LivecoinOrderBookResponse extends LivecoinResponse implements OrderBookResponse {
    @SerializedName("asks")
    @Expose
    public List<List<String>> asks;
    @SerializedName("bids")
    @Expose
    public List<List<String>> bids;

    @Override
    public List<Price> bids() {
        return parseValues(bids);
    }


    @Override
    public List<Price> asks() {
        return parseValues(asks);
    }

    private List<Price> parseValues(List<List<String>> values) {
        List<Price> prices = new ArrayList<>();
        values.forEach(value -> prices.add(new Price(Double.valueOf(value.get(0)), Double.valueOf(value.get(1)))));
        return prices;
    }
}
