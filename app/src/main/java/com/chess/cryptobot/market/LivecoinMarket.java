package com.chess.cryptobot.market;

import com.chess.cryptobot.api.LivecoinMarketService;
import com.chess.cryptobot.api.MarketService;
import com.chess.cryptobot.model.response.BalanceResponse;

import java.util.Map;
import java.util.TreeMap;

public class LivecoinMarket extends MarketAuthenticator implements Market {

    LivecoinMarket(String url, String apiKey, String secretKey) {
        super(url, apiKey, secretKey);
        this.algorithm = "HmacSHA256";
    }

    @Override
    MarketService getService() {
        return retrofit.create(LivecoinMarketService.class);
    }

    @Override
    public Double getAmount(String coinName) {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("currency", coinName);

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        BalanceResponse response = service.getBalance(params, headers);
        return response.getAmount();
    }

    @Override
    String makeHash(Map<String, String> queryParams) {
        return encode(buildQueryString(queryParams));
    }

    @Override
    String asString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }
}
