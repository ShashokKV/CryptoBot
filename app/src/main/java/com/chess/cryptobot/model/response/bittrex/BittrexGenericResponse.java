package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.chess.cryptobot.model.response.HistoryResponse;
import com.chess.cryptobot.model.response.TickerResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

class BittrexGenericResponse implements TickerResponse, CurrenciesResponse, HistoryResponse {
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
    private String marketName;
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
    @SerializedName("MinTradeSize")
    @Expose
    private Double minTradeSize;
    @SerializedName("Exchange")
    @Expose
    private String exchange;
    @SerializedName("TimeStamp")
    @Expose
    private String timeStamp;
    @SerializedName("OrderType")
    @Expose
    private String orderType;
    @SerializedName("Quantity")
    @Expose
    private Double quantity;
    @SerializedName("QuantityRemaining")
    @Expose
    private Double quantityRemaining;
    @SerializedName("PricePerUnit")
    @Expose
    private Double pricePerUnit;
    @SerializedName("Amount")
    @Expose
    private Double amount;
    @SerializedName("Opened")
    @Expose
    private String opened;
    @SerializedName("LastUpdated")
    @Expose
    private String lastUpdated;


    Double getAvailable() {
        if (available == null) return 0.0d;
        return available;
    }

    List<BittrexPrice> getBuy() {
        return buy;
    }

    List<BittrexPrice> getSell() {
        return sell;
    }

    public String getMarketName() {
        return marketName.replace("-", "/");
    }

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
        if (isActive == null) return false;
        return isActive;
    }

    @Override
    public Double getFee() {
        if (txFee == null) return 0.0d;
        return txFee;
    }

    String getAddress() {
        return address;
    }

    public String getPairName() {
        return marketName;
    }

    Double getMinTradeSize() {
        return minTradeSize;
    }

    @Override
    public LocalDateTime getHistoryTime() {
        String timeString = this.timeStamp == null ? this.opened : this.timeStamp;
        timeString = timeString == null ? lastUpdated : timeString;
        return LocalDateTime.parse(timeString);
    }

    @Override
    public String getHistoryName() {
        return currency == null ? exchange.replace("-", "/") : currency;
    }

    @Override
    public String getHistoryMarket() {
        return "bittrex";
    }

    @Override
    public Double getHistoryAmount() {
        return quantity == null ? amount : quantity;
    }

    @Override
    public Double getHistoryPrice() {
        return pricePerUnit;
    }

    @Override
    public String getHistoryAction() {
        return orderType == null ?
                (lastUpdated == null ? "withdraw" : "deposit")
                : orderType.toLowerCase().replace("limit_", "");
    }

    @Override
    public Integer getProgress() {
        if (quantity == null || quantityRemaining == null) return 0;
        if (quantity == 0d) return 0;
        return Double.valueOf(((quantity-quantityRemaining) / quantity) * 100).intValue();
    }
}