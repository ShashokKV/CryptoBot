package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.TradeResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LivecoinTradeResponse extends LivecoinResponse implements TradeResponse {
    @SerializedName("orderId")
    @Expose
    private String orderId;

    @Override
    public String getTradeId() {
        return orderId;
    }
}
