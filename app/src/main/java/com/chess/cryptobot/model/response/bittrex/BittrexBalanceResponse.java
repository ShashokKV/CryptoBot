package com.chess.cryptobot.model.response.bittrex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BittrexBalanceResponse implements Result {

    @SerializedName("MarketCurrency")
    @Expose
    public String marketCurrency;
    @SerializedName("BaseCurrency")
    @Expose
    public String baseCurrency;
    @SerializedName("MarketCurrencyLong")
    @Expose
    public String marketCurrencyLong;
    @SerializedName("BaseCurrencyLong")
    @Expose
    public String baseCurrencyLong;
    @SerializedName("MinTradeSize")
    @Expose
    public Double minTradeSize;
    @SerializedName("MarketName")
    @Expose
    public String marketName;
    @SerializedName("IsActive")
    @Expose
    public Boolean isActive;
    @SerializedName("IsRestricted")
    @Expose
    public Boolean isRestricted;
    @SerializedName("Created")
    @Expose
    public String created;
    @SerializedName("Notice")
    @Expose
    public Object notice;
    @SerializedName("IsSponsored")
    @Expose
    public Object isSponsored;
    @SerializedName("LogoUrl")
    @Expose
    public String logoUrl;@SerializedName("Currency")
    @Expose
    public String currency;
    @SerializedName("Balance")
    @Expose
    public Double balance;
    @SerializedName("Available")
    @Expose
    public Double available;
    @SerializedName("Pending")
    @Expose
    public Integer pending;
    @SerializedName("CryptoAddress")
    @Expose
    public String cryptoAddress;
    @SerializedName("Requested")
    @Expose
    public Boolean requested;
    @SerializedName("Uuid")
    @Expose
    public Object uuid;

    @Override
    public Double getAmount() {
        return balance;
    }
}