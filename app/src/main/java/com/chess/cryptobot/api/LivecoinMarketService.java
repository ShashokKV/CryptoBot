package com.chess.cryptobot.api;

import com.chess.cryptobot.model.response.livecoin.LivecoinAddressResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinBalanceResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinCurrenciesListResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinPaymentResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinTickerResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface LivecoinMarketService {

    @GET("payment/balance")
    Call<LivecoinBalanceResponse> getBalance(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @GET("exchange/order_book")
    Call<LivecoinOrderBookResponse> getOrderBook(@QueryMap Map<String, String> options);

    @GET("exchange/ticker")
    Call<List<LivecoinTickerResponse>> getTicker();

    @GET("info/coinInfo")
    Call<LivecoinCurrenciesListResponse> getCurrencies();

    @GET("payment/get/address")
    Call<LivecoinAddressResponse> getAddress(@QueryMap Map<String, String> options, @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST("payment/out/coin")
    Call<LivecoinPaymentResponse> payment(@FieldMap Map<String, String> params, @HeaderMap Map<String, String> headers);
}
