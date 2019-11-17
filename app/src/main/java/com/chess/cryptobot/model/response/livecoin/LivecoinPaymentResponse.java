package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.PaymentResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LivecoinPaymentResponse extends LivecoinResponse implements PaymentResponse {
    @SerializedName("id")
    @Expose
    private Long id;


    @Override
    public String getPaymentId() {
        return id.toString();
    }
}
