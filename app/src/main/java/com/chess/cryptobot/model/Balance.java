package com.chess.cryptobot.model;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class Balance {
    private Double bittrexAmmount;
    private Double livecoinAmmount;
    private String coinName;
    private Bitmap coinIcon;

    Balance(String coinName) {
        this.coinName = coinName;
        this.bittrexAmmount = 0.0d;
        this.livecoinAmmount = 0.0d;
    }

    @Override
    public boolean equals(@Nullable Object balanceObj) {
        if (balanceObj == null) return false;
        if (balanceObj instanceof String) {
            return balanceObj.equals(this.coinName);
        }else if (balanceObj instanceof Balance) {
            Balance balance = (Balance) balanceObj;
            return (balance.getCoinName().equals(coinName));
        }else {
            return false;
        }
    }

    public Double getBittrexAmmount() {
        return bittrexAmmount;
    }

    public void setBittrexAmmount(double bittrexAmmount) {
        this.bittrexAmmount = bittrexAmmount;
    }

    public Double getLivecoinAmmount() {
        return livecoinAmmount;
    }

    public void setLivecoinAmmount(double livecoinAmmount) {
        this.livecoinAmmount = livecoinAmmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public Bitmap getCoinIcon() {
        return coinIcon;
    }

    public void setCoinIcon(Bitmap coinIcon) {
        this.coinIcon = coinIcon;
    }
}
