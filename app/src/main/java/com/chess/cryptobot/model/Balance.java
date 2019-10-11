package com.chess.cryptobot.model;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Balance {
    private String coinName;
    private Bitmap coinIcon;
    private Map<String, Double> amounts;
    private String message;

    public Balance(String coinName) {
        this.coinName = coinName;
        this.amounts = new HashMap<>();
        this.amounts.put("bittrex", 0.0d);
        this.amounts.put("livecoin", 0.0d);
    }

    public Balance(Balance balance) {
        this.coinName = balance.getCoinName();
        Bitmap coinIcon = balance.getCoinIcon();
        if (coinIcon!=null) this.coinIcon = coinIcon.copy(coinIcon.getConfig(), coinIcon.isMutable());
        this.amounts = new HashMap<>();
        balance.getAmounts().keySet().forEach(key -> this.setAmount(key, balance.getAmount(key)));
        this.message = balance.getMessage();
    }

    @Override
    public boolean equals(@Nullable Object balanceObj) {
        if (balanceObj == null) return false;
        if (balanceObj == this) return true;
        if (balanceObj instanceof Balance) {
            Balance balance = (Balance) balanceObj;
            return (balance.getCoinName().equals(this.coinName));
        }else {
            return false;
        }
    }

    public String getCoinName() {
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

    public Double getAmount(String marketName)  {
        return this.amounts.get(marketName);
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
}
