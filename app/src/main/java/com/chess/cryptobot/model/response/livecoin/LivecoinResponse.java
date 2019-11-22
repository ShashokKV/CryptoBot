package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.MarketResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LivecoinResponse implements MarketResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("exception")
    @Expose
    private String exception;

    @Override
    public boolean success() {
        if (success==null) return true;
        return success;
    }

    @Override
    public String message() {
        if (errorMessage==null) return exception;
        return errorMessage;
    }
}
