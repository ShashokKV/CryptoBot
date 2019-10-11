package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.model.response.BalanceResponse;
import com.chess.cryptobot.model.response.MarketResponse;

public class BittrexResponse implements MarketResponse, BalanceResponse {

    private Boolean success;
    public String message;
    private BittrexGenericResponse[] results;

    BittrexResponse(BittrexGenericResponse[] results) {
        this.results = results;
    }

    BittrexResponse(BittrexGenericResponse result) {
        this.results = new BittrexGenericResponse[1];
        results[0] = result;
    }

    void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Double getAmount() {
        return results[0].getAvailable();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public String message() {
        return message;
    }
}