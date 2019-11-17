package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.AddressResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LivecoinAddressResponse extends LivecoinResponse implements AddressResponse {

    @SerializedName("wallet")
    @Expose
    private String wallet;

    @Override
    public String getAddress() {
        return wallet;
    }
}
