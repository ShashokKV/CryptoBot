package com.chess.cryptobot.model.response.bittrex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class BittrexPrice {
    @SerializedName("Quantity")
    @Expose
    private Double quantity;
    @SerializedName("Rate")
    @Expose
    private Double rate;

    Double getQuantity() {
        return quantity;
    }

    Double getRate() {
        return rate;
    }
}
