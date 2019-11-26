package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.TickerResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LivecoinTickerResponse extends LivecoinResponse implements TickerResponse {

    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("best_bid")
    @Expose
    private Double bestBid;
    @SerializedName("best_ask")
    @Expose
    private Double bestAsk;
    @SerializedName("volume")
    @Expose
    private Double volume;

    @Override
    public String getMarketName() {
        String[] split = symbol.split("/");
        return split[1].concat("/").concat(split[0]);
    }

    @Override
    public Double getTickerBid() {
        return bestBid;
    }

    @Override
    public Double getTickerAsk() {
        return bestAsk;
    }

    public Double getVolume() {
        return volume;
    }
}
