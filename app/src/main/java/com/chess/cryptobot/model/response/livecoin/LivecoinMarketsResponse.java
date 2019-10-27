package com.chess.cryptobot.model.response.livecoin;

import com.chess.cryptobot.model.response.AllMarketsResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LivecoinMarketsResponse extends LivecoinResponse implements AllMarketsResponse {
    
    @SerializedName("restrictions")
    @Expose
    public List<Restriction> restrictions;

    @Override
    public List<String> getMarketNames() {
        List<String> marketNames = new ArrayList<>();
        for (Restriction restriction : restrictions) {
            String[] pairs = restriction.currencyPair.split("/");
            marketNames.add(String.format("%s/%s", pairs[1], pairs[0]));
        }
        return marketNames;
    }

    class Restriction {
        @SerializedName("currencyPair")
        @Expose
        public String currencyPair;
    }
}
