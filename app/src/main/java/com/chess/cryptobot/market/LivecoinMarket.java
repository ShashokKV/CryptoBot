package com.chess.cryptobot.market;

import com.chess.cryptobot.api.LivecoinMarketService;
import com.chess.cryptobot.exceptions.LivecoinException;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinBalanceResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Retrofit;

public class LivecoinMarket extends MarketRequest implements Market {
    private LivecoinMarketService service;

    LivecoinMarket(String url, String apiKey, String secretKey) {
        super(url, apiKey, secretKey);
        this.algorithm = "HmacSHA256";
        this.path = "";
        this.service = (LivecoinMarketService) initService(initRetrofit(initGson()));
    }

    @Override
    public String getMarketName() {
        return "livecoin";
    }

    @Override
    Gson initGson() {
        return new GsonBuilder().create();
    }

    @Override
    Object initService(Retrofit retrofit) {
        return retrofit.create(LivecoinMarketService.class);
    }

    @Override
    public Double getAmount(String coinName) throws LivecoinException {
        if (keysIsEmpty()) return 0.0d;
        TreeMap<String, String> params = new TreeMap<>();
        params.put("currency", coinName);

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        LivecoinBalanceResponse response;
        try {
            Call<LivecoinBalanceResponse> call = service.getBalance(params, headers);
            response = (LivecoinBalanceResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getAmount();
    }

    @Override
    public OrderBookResponse getOrderBook(String pairName) throws LivecoinException {
        LivecoinOrderBookResponse response;
        TreeMap<String, String> params = new TreeMap<>();
        params.put("currencyPair", pairName);
        params.put("groupByPrice", "true");
        params.put("depth", "10");

        try {
            Call<LivecoinOrderBookResponse> call = service.getOrderBook(params);
            response = (LivecoinOrderBookResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response;
    }
}
