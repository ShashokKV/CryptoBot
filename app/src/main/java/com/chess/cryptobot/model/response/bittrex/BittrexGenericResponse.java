package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.model.response.TickerResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class BittrexGenericResponse implements TickerResponse {
    @SerializedName("Available")
    @Expose
    private Double available;
    @SerializedName("buy")
    @Expose
    private List<BittrexPrice> buy;
    @SerializedName("sell")
    @Expose
    private List<BittrexPrice> sell;
    @SerializedName("MarketCurrency")
    @Expose
    private String marketCurrency;
    @SerializedName("BaseCurrency")
    @Expose
    private String baseCurrency;
    @SerializedName("MarketName")
    @Expose
    private String tickerName;
    @SerializedName("Bid")
    @Expose
    private Double tickerBid;
    @SerializedName("Ask")
    @Expose
    private Double tickerAsk;

    Double getAvailable() {
        return available;
    }

    List<BittrexPrice> getBuy() {
        return buy;
    }

    List<BittrexPrice> getSell() {
        return sell;
    }

    String getMarketName() {return String.format("%s/%s", baseCurrency, marketCurrency);}

    public String getTickerName() {return tickerName.replace("-", "/");}

    public Double getTickerBid() {
        return tickerBid;
    }

    public Double getTickerAsk() {
        return tickerAsk;
    }
}