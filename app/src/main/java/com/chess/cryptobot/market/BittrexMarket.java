package com.chess.cryptobot.market;

import com.chess.cryptobot.api.BittrexMarketService;
import com.chess.cryptobot.api.MarketService;
import com.chess.cryptobot.model.response.BalanceResponse;

import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class BittrexMarket extends MarketAuthenticator implements Market {

    BittrexMarket(String url, String apiKey, String secretKey) {
        super(url, apiKey, secretKey);
        this.algorithm = "HmacSHA512";
    }

    @Override
    MarketService getService() {
        return retrofit.create(BittrexMarketService.class);
    }

    @Override
    public Double getAmount(String coinName) {
        Map<String, String> params = new TreeMap<>();
        params.put("currency", coinName);
        params.put("apikey", this.apiKey);

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("apisign", hash);

        BalanceResponse response = service.getBalance(params, headers);
        return response.getAmount();
    }

    @Override
    String makeHash(Map<String, String> queryParams) {
        return encode(String.format("%s?%s&nonce=%s",
                this.url, buildQueryString(queryParams), System.currentTimeMillis()));
    }

    String asString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes).toUpperCase();
    }
}
