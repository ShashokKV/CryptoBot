package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.BalanceResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LivecoinBalanceResponse implements BalanceResponse {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("currency")
    @Expose
    public String currency;
    @SerializedName("value")
    @Expose
    public Double value;

    @Override
    public Double getAmount() {
        return value;
    }
}
