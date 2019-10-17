package com.chess.cryptobot.api;

import com.chess.cryptobot.model.response.bittrex.BittrexResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.QueryMap;

public interface BittrexMarketService {

    @GET("account/getbalance")
    Call<BittrexResponse> getBalance(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @GET("public/getorderbook")
    Call<BittrexResponse> getOrderBook(@QueryMap Map<String, String> options);
}
