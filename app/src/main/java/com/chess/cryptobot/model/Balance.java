package com.chess.cryptobot.model;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.chess.cryptobot.market.Market;

import java.util.HashMap;
import java.util.Map;

public class Balance implements ViewItem {
    private final String coinName;
    private String coinUrl;
    private Bitmap coinIcon;
    private final Map<String, Double> amounts;
    private String message;
    private final Map<String, Boolean> statuses = new HashMap<>();

    public Balance(String coinName) {
        this.coinName = coinName;
        this.amounts = new HashMap<>();
        this.amounts.put(Market.BITTREX_MARKET, 0.0d);
        this.amounts.put(Market.LIVECOIN_MARKET, 0.0d);
        this.statuses.put(Market.BITTREX_MARKET, true);
        this.statuses.put(Market.LIVECOIN_MARKET, true);
    }

    @Override
    public boolean equals(@Nullable Object balanceObj) {
        if (balanceObj == null) return false;
        if (balanceObj == this) return true;
        if (balanceObj instanceof Balance) {
            Balance balance = (Balance) balanceObj;
            return (balance.getName().equals(this.coinName));
        } else {
            return false;
        }
    }

    public String getName() {
        return coinName;
    }

    public Bitmap getCoinIcon() {
        return coinIcon;
    }

    public void setCoinIcon(Bitmap coinIcon) {
        this.coinIcon = coinIcon;
    }

    public void setAmount(String marketName, Double amount) {
        this.amounts.put(marketName, amount);
    }

    public Double getAmount(String marketName) {
        Double amount = this.amounts.get(marketName);
        if (amount == null) return 0.0d;
        return amount;
    }

    public Map<String, Double> getAmounts() {
        return amounts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatuses(Boolean livecoinStatus, Boolean bittrexStatus) {
        this.statuses.put(Market.LIVECOIN_MARKET, livecoinStatus);
        this.statuses.put(Market.BITTREX_MARKET, bittrexStatus);
    }

    public Boolean getStatus(String marketName) {
        Boolean status = this.statuses.get(marketName);
        if (status == null) return true;
        return status;
    }

    public void setCoinUrl(String coinUrl) {
        this.coinUrl = coinUrl;
    }

    public String getCoinUrl() {
        return coinUrl;
    }
}
