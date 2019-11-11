package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.BalanceResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LivecoinBalanceResponse extends LivecoinResponse implements BalanceResponse {
    @SerializedName("value")
    @Expose
    private Double value;

    @Override
    public Double getAmount() {
        return value;
    }
}
