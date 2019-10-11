package com.chess.cryptobot.model.response.bittrex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class BittrexGenericResponse {
    @SerializedName("Available")
    @Expose
    private Double available;

    Double getAvailable() {
        return available;
    }
}