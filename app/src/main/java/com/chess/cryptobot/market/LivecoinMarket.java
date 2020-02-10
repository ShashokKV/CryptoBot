package com.chess.cryptobot.market;

import com.chess.cryptobot.api.LivecoinMarketService;
import com.chess.cryptobot.exceptions.LivecoinException;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.History;
import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.chess.cryptobot.model.response.HistoryResponseFactory;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.TickerResponse;
import com.chess.cryptobot.model.response.TradeLimitResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinAddressResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinBalanceResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinCurrenciesListResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinHistoryResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrdersResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinTickerResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinTradeLimitResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LivecoinMarket extends MarketRequest {
    private final LivecoinMarketService service;

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
        Call<LivecoinBalanceResponse> call = service.getBalance(params, headers);
        try {
            response = (LivecoinBalanceResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getAmount();
    }

    @Override
    public OrderBookResponse getOrderBook(String pairName) throws LivecoinException {
        LivecoinOrderBookResponse response;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("currencyPair", pairName);
        params.put("groupByPrice", "true");
        params.put("depth", "10");

        Call<LivecoinOrderBookResponse> call = service.getOrderBook(params);
        try {
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
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getInfo();
    }

    @Override
    public TradeLimitResponse getMinQuantity() throws MarketException {
        LivecoinTradeLimitResponse response;
        Call<LivecoinTradeLimitResponse> call = service.getMinTradeSize();
        try {
            response = (LivecoinTradeLimitResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response;
    }

    @Override
    public String getAddress(String coinName) throws MarketException {
        if (keysIsEmpty()) return null;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("currency", coinName);

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        LivecoinAddressResponse response;
        Call<LivecoinAddressResponse> call = service.getAddress(params, headers);
        try {
            response = (LivecoinAddressResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return response.getAddress();
    }

    @Override
    public void sendCoins(String coinName, Double amount, String address) throws MarketException {
        if (keysIsEmpty()) return;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("amount", String.format(Locale.US, "%.8f", amount));
        params.put("currency", coinName);
        params.put("wallet", address);

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        Call<LivecoinResponse> call = service.payment(
                params.get("amount"),
                params.get("currency"),
                params.get("wallet"),
                headers);
        try {
            execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
    }

    @Override
    public void buy(String pairName, Double price, Double amount) throws MarketException {
        if (keysIsEmpty()) return;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("currencyPair", pairName);
        params.put("price", String.format(Locale.US, "%.8f", price));
        params.put("quantity", String.format(Locale.US, "%.8f", amount));

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        Call<LivecoinResponse> call = service.buy(
                params.get("currencyPair"),
                params.get("price"),
                params.get("quantity"),
                headers);
        try {
            execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
    }

    @Override
    public void sell(String pairName, Double price, Double amount) throws MarketException {
        if (keysIsEmpty()) return;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("currencyPair", pairName);
        params.put("price", String.format(Locale.US, "%.8f", price));
        params.put("quantity", String.format(Locale.US, "%.8f", amount));

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        Call<LivecoinResponse> call = service.sell(
                params.get("currencyPair"),
                params.get("price"),
                params.get("quantity"),
                headers);
        try {
            execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
    }

    @Override
    public List<History> getOpenOrders() throws MarketException {
        if (keysIsEmpty()) return null;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("openClosed", "OPEN");

        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        LivecoinOrdersResponse response;
        Call<LivecoinOrdersResponse> call = service.getOpenOrders(params, headers);
        try {
            response = (LivecoinOrdersResponse) execute(call);
        } catch (MarketException e) {
            throw new LivecoinException(e.getMessage());
        }
        return new HistoryResponseFactory(response.getData()).getHistory();
    }

    @Override
    public List<History> getHistory() throws MarketException {
        if (keysIsEmpty()) return null;
        LocalDateTime startTime = LocalDateTime.now().minusDays(29);
        LocalDateTime endTime = LocalDateTime.now();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("end", String.valueOf(endTime.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()))*1000));
        params.put("start", String.valueOf(startTime.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()))*1000));
        String hash = makeHash(params);

        Map<String, String> headers = new HashMap<>();
        headers.put("API-key", this.apiKey);
        headers.put("Sign", hash);

        List<LivecoinHistoryResponse> responses;
        Call<List<LivecoinHistoryResponse>> call = service.getHistory(params, headers);
        try {
            Response<List<LivecoinHistoryResponse>> result = call.execute();
            responses = result.body();
            if (responses == null) {
                throw new LivecoinException("No response");
            }
        } catch (MarketException | IOException e) {
            throw new LivecoinException(e.getMessage());
        }
        return new HistoryResponseFactory(responses).getHistory();
    }
}
