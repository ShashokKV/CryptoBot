package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.CurrenciesListResponse;
import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LivecoinCurrenciesListResponse extends LivecoinResponse implements CurrenciesListResponse {
    @SerializedName("info")
    @Expose
    public Info[] info;

    public List<CurrenciesResponse> getInfo() {
        if (this.info == null) return new ArrayList<>();
        return Arrays.asList(this.info);
    }
}


class Info implements CurrenciesResponse {
    @SerializedName("symbol")
    @Expose
    private String symbol;
    @SerializedName("walletStatus")
    @Expose
    private String walletStatus;
    @SerializedName("withdrawFee")
    @Expose
    private Double withdrawFee;

    @Override
    public String getCurrencyName() {
        return symbol;
    }

    @Override
    public Boolean isActive() {
        if (walletStatus == null) return false;
        return walletStatus.equals("normal");
    }

    @Override
    public Double getFee() {
        if (withdrawFee == null) return withdrawFee;
        return withdrawFee;
    }
}
