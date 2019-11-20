package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.chess.cryptobot.model.response.TickerResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

class BittrexGenericResponse implements TickerResponse, CurrenciesResponse {
    @SerializedName("Available")
    @Expose
    private Double available;
    @SerializedName("buy")
    @Expose
    private List<BittrexPrice> buy;
    @SerializedName("sell")
    @Expose
    private List<BittrexPrice> sell;
    @SerializedName("MarketName")
    @Expose
    private String tickerName;
    @SerializedName("Bid")
    @Expose
    private Double tickerBid;
    @SerializedName("Ask")
    @Expose
    private Double tickerAsk;
    @SerializedName("Volume")
    @Expose
    private Double volume;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("TxFee")
    @Expose
    private Double txFee;
    @SerializedName("IsActive")
    @Expose
    private Boolean isActive;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("uuid")
    @Expose
    private String uuid;

    Double getAvailable() {
        return available;
    }

    List<BittrexPrice> getBuy() {
        return buy;
    }

    List<BittrexPrice> getSell() {
        return sell;
    }

    public String getTickerName() {return tickerName.replace("-", "/");}

    public Double getTickerBid() {
        return tickerBid;
    }

    public Double getTickerAsk() {
        return tickerAsk;
    }

    @Override
    public Double getVolume() {
        return volume;
    }

    @Override
    public String getCurrencyName() {
        return currency;
    }

    @Override
    public Boolean isActive() {
        if (isActive==null) return false;
        return isActive;
    }

    @Override
    public Double getFee() {
        if (txFee==null) return 0.0d;
        return txFee;
    }

    String getAddress() {
        return address;
    }

    String getUuid() {
        return uuid;
    }
}