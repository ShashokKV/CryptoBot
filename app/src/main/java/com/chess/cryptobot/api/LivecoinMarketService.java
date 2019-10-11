package com.chess.cryptobot.api;

import com.chess.cryptobot.model.response.livecoin.LivecoinBalanceResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.QueryMap;

public interface LivecoinMarketService {

    @GET("payment/balance")
    Call<LivecoinBalanceResponse> getBalance(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);
}
