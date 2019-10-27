package com.chess.cryptobot.model.response.bittrex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class BittrexGenericResponse {
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
}