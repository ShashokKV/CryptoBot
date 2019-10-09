package com.chess.cryptobot.api;

import com.chess.cryptobot.model.response.BalanceResponse;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.QueryMap;

public interface LivecoinMarketService extends MarketService {

    @GET("payment/balance")
    BalanceResponse getBalance(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);
}
