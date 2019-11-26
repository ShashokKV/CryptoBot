package com.chess.cryptobot.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;

    @SerializedName("message")
    @Expose
    private String message;

    public String getErrorMessage() {
        if (errorMessage == null) return message;
        return errorMessage;
    }
}
