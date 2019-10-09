package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.model.response.BalanceResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BittrexResponse implements BalanceResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("result")
    @Expose
    public List<Result> result;

    @Override
    public Double getAmount() {
        return result.get(0).getAmount();
    }
}