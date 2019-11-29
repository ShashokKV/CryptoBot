package com.chess.cryptobot.market;

import com.chess.cryptobot.api.BittrexMarketService;
import com.chess.cryptobot.exceptions.BittrexException;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.TickerResponse;
import com.chess.cryptobot.model.response.TradeLimitResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;

public class BittrexMarket extends MarketRequest {
    private final BittrexMarketService service;

    BittrexMarket(String url, String apiKey, String secretKey) {
        super(url, apiKey, secretKey);
        this.algorithm = "HmacSHA512";
        this.service = (BittrexMarketService) initService(initRetrofit(initGson()));
    }

    @Override
    public String getMarketName() {
        return Market.BITTREX_MARKET;
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
        if (keysIsEmpty()) return null;
        this.path = this.url.concat("account/getbalance?");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("currency", coinName);
        params.put("apikey", this.apiKey);
        params.put("nonce", String.valueOf(System.currentTimeMillis()));

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("apisign", hash);

        BittrexResponse response;
        try {
            Call<BittrexResponse> call = service.getBalance(params, headers);
            response = (BittrexResponse) execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }

        if (response == null) return 0.0d;
        return response.getAmount();
    }

    @Override
    public String getAddress(String coinName) throws MarketException {
        if (keysIsEmpty()) return null;
        this.path = this.url.concat("account/getdepositaddress?");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("currency", coinName);
        params.put("apikey", this.apiKey);
        params.put("nonce", String.valueOf(System.currentTimeMillis()));

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("apisign", hash);

        BittrexResponse response;
        try {
            Call<BittrexResponse> call = service.getAddress(params, headers);
            response = (BittrexResponse) execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }

        if (response == null) return null;
        return response.getAddress();
    }

    @Override
    public OrderBookResponse getOrderBook(String pairName) throws BittrexException {
        BittrexResponse response;
        Map<String, String> params = new LinkedHashMap<>();
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
    public List<? extends TickerResponse> getTicker() throws MarketException {
        BittrexResponse response;
        Call<BittrexResponse> call = service.getTicker();
        try {
            response = (BittrexResponse) execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
        return response.getTickers();
    }

    @Override
    public List<CurrenciesResponse> getCurrencies() throws MarketException {
        BittrexResponse response;
        Call<BittrexResponse> call = service.getCurrencies();
        try {
            response = (BittrexResponse) execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
        return response.getInfo();
    }

    @Override
    public TradeLimitResponse getMinQuantity() throws MarketException {
        BittrexResponse response;
        Call<BittrexResponse> call = service.getMinTradeSize();
        try {
            response = (BittrexResponse) execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
        return response;
    }

    @Override
    public void sendCoins(String coinName, Double amount, String address) throws MarketException {
        this.path = this.url.concat("account/withdraw?");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("currency", coinName);
        params.put("quantity", String.format(Locale.US, "%.8f", amount));
        params.put("address", address);
        params.put("apikey", this.apiKey);
        params.put("nonce", String.valueOf(System.currentTimeMillis()));

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("apisign", hash);

        try {
            Call<BittrexResponse> call = service.payment(
                    params.get("currency"),
                    params.get("quantity"),
                    params.get("address"),
                    params.get("apikey"),
                    params.get("nonce"),
                    headers);
            execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
    }

    @Override
    public void buy(String pairName, Double price, Double amount) throws MarketException {
        this.path = this.url.concat("market/buylimit?");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("market", pairName);
        params.put("quantity", String.format(Locale.US, "%.5f", amount));
        params.put("rate", String.format(Locale.US, "%.8f", price));
        params.put("apikey", this.apiKey);
        params.put("nonce", String.valueOf(System.currentTimeMillis()));

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("apisign", hash);

        try {
            Call<BittrexResponse> call = service.buy(
                    params.get("market"),
                    params.get("quantity"),
                    params.get("rate"),
                    params.get("apikey"),
                    params.get("nonce"),
                    headers);
            execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
    }

    @Override
    public void sell(String pairName, Double price, Double amount) throws MarketException {
        this.path = this.url.concat("market/selllimit?");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("market", pairName);
        params.put("quantity", String.format(Locale.US, "%.5f", amount));
        params.put("rate", String.format(Locale.US, "%.8f", price));
        params.put("apikey", this.apiKey);
        params.put("nonce", String.valueOf(System.currentTimeMillis()));

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("apisign", hash);

        try {
            Call<BittrexResponse> call = service.sell(
                    params.get("market"),
                    params.get("quantity"),
                    params.get("rate"),
                    params.get("apikey"),
                    params.get("nonce"),
                    headers);
            execute(call);
        } catch (MarketException e) {
            throw new BittrexException(e.getMessage());
        }
    }
}
