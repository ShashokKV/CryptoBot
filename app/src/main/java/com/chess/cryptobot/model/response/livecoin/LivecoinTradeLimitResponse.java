package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.TradeLimitResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LivecoinTradeLimitResponse extends LivecoinResponse implements TradeLimitResponse {

    @SerializedName("restrictions")
    @Expose
    public List<Restriction> restrictions = null;

    @Override
    public Double getTradeLimitByName(String pairName) {
        for (Restriction restriction : restrictions) {
            if (restriction.getCurrencyPair().equals(pairName)) {
                return restriction.getMinLimitQuantity();
            }
        }
        return null;
    }

    public class Restriction {

        @SerializedName("currencyPair")
        @Expose
        private String currencyPair;

        @SerializedName("minLimitQuantity")
        @Expose
        private Double minLimitQuantity;

        String getCurrencyPair() {
            return currencyPair;
        }

        Double getMinLimitQuantity() {
            return minLimitQuantity;
        }
    }
}
