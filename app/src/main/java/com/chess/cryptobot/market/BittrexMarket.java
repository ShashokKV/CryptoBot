package com.chess.cryptobot.market;

import com.chess.cryptobot.api.BittrexMarketService;
import com.chess.cryptobot.exceptions.BittrexException;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Retrofit;

public class BittrexMarket extends MarketRequest implements Market {
    private BittrexMarketService service;

    BittrexMarket(String url, String apiKey, String secretKey) {
        super(url, apiKey, secretKey);
        this.algorithm = "HmacSHA512";
        this.service = (BittrexMarketService) initService(initRetrofit(initGson()));
    }

    @Override
    public String getMarketName() {
        return "bittrex";
    }

    @Override
    Gson initGson() {
        return new GsonBuilder()
                .registerTypeAdapter(BittrexResponse.class, new BittrexTypeAdapter())
                .create();
    }

    @Override
    Object initService(Retrofit retrofit) {
        return retrofit.create(BittrexMarketService.class);
    }

    @Override
    public Double getAmount(String coinName) throws BittrexException {
        if (keysIsEmpty()) return 0.0d;
        this.path = this.url.concat("account/getbalance?");
        Map<String, String> params = new TreeMap<>();
        params.put("currency", coinName);
        params.put("apikey", this.apiKey);
        params.put("nonce", String.valueOf(System.currentTimeMillis()));

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("apisign", hash);

        BittrexResponse response;
        try {
            Call<BittrexResponse> call = service.getBalance(params, headers);
            response = (BittrexResponse) execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
        return response.getAmount();
    }

    @Override
    public OrderBookResponse getOrderBook(String pairName) throws BittrexException {
        BittrexResponse response;
        Map<String, String> params = new TreeMap<>();
        params.put("market", pairName);
        params.put("type", "both");
        try {
            Call<BittrexResponse> call = service.getOrderBook(params);
            response = (BittrexResponse) execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllMarkets() throws BittrexException {
        BittrexResponse response;
        Call<BittrexResponse> call = service.getAllMarkets();
        try {
            response = (BittrexResponse) execute(call);
        }catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
        return response.getMarketNames();
    }


}
