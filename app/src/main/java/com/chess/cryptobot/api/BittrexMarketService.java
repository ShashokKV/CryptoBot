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

    @GET("public/getmarketsummaries")
    Call<BittrexResponse> getTicker();

    @GET("public/getcurrencies")
    Call<BittrexResponse> getCurrencies();

    @GET("public/getmarkets")
    Call<BittrexResponse> getMinTradeSize();

    @GET("account/getdepositaddress")
    Call<BittrexResponse> getAddress(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @GET("account/withdraw")
    Call<BittrexResponse> payment(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @GET("market/buylimit")
    Call<BittrexResponse> buy(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @GET("market/selllimit")
    Call<BittrexResponse> sell(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);
}
