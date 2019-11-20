package com.chess.cryptobot.market;

import com.chess.cryptobot.api.LivecoinMarketService;
import com.chess.cryptobot.exceptions.LivecoinException;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.TickerResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinAddressResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinBalanceResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinCurrenciesListResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinTickerResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LivecoinMarket extends MarketRequest {
    private LivecoinMarketService service;

    LivecoinMarket(String url, String apiKey, String secretKey) {
        super(url, apiKey, secretKey);
        this.algorithm = "HmacSHA256";
        this.path = "";
        this.service = (LivecoinMarketService) initService(initRetrofit(initGson()));
    }

    @Override
    public String getMarketName() {
        return Market.LIVECOIN_MARKET;
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

    @Override
    public List<? extends TickerResponse> getTicker() throws MarketException {
        List<LivecoinTickerResponse> responses;
        Call<List<LivecoinTickerResponse>> call = service.getTicker();
        try {
            Response<List<LivecoinTickerResponse>> result = call.execute();
            responses = result.body();
            if (responses == null) {
                throw new LivecoinException("No response");
            }
        } catch (IOException e) {
            throw new LivecoinException(e.getMessage());
        }
        return responses;
    }

    @Override
    public List<CurrenciesResponse> getCurrencies() throws MarketException {
        LivecoinCurrenciesListResponse response;
        Call<LivecoinCurrenciesListResponse> call = service.getCurrencies();
        try {
            response = (LivecoinCurrenciesListResponse) execute(call);
        }catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getInfo();
    }

    @Override
    public String getAddress(String coinName) throws MarketException {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("currency", coinName);

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        LivecoinAddressResponse response;
        try {
            Call<LivecoinAddressResponse> call = service.getAddress(params, headers);
            response = (LivecoinAddressResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getAddress();
    }

    @Override
    public String sendCoins(String coinName, Double amount, String address) throws MarketException {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("amount", amount.toString());
        params.put("currency", coinName);
        params.put("wallet", address);

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
/*
        LivecoinPaymentResponse response;
        try {
            Call<LivecoinPaymentResponse> call = service.payment(params, headers);
            response = (LivecoinPaymentResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getPaymentId();*/
        return "paymentId";
    }

    @Override
    public String buy(String pairName, Double price, Double amount) throws MarketException {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("currencyPair", pairName);
        params.put("price", price.toString());
        params.put("quantity", amount.toString());

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
/*
        LivecoinTradeResponse response;
        try {
            Call<LivecoinTradeResponse> call = service.buy(params, headers);
            response = (LivecoinTradeResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getTradeId();*/
        return "buyId";
    }

    @Override
    public String sell(String pairName, Double price, Double amount) throws MarketException {
        TreeMap<String, String> params = new TreeMap<>();
        params.put("currencyPair", pairName);
        params.put("price", price.toString());
        params.put("quantity", amount.toString());

        String hash = makeHash(params);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
/*
        LivecoinTradeResponse response;
        try {
            Call<LivecoinTradeResponse> call = service.sell(params, headers);
            response = (LivecoinTradeResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getTradeId();*/
        return "sellId";
    }


}
