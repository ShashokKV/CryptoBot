package com.chess.cryptobot.api;

import com.chess.cryptobot.model.response.bittrex.BittrexResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;
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
    Call<BittrexResponse> getAddress(@QueryMap(encoded = true) Map<String, String> options, @HeaderMap Map<String, String> headers);

    @GET("account/withdraw")
    Call<BittrexResponse> payment(@Query(value = "currency", encoded = true) String marketName,
                                  @Query(value = "quantity", encoded = true) String quantity,
                                  @Query(value = "address", encoded = true) String rate,
                                  @Query(value = "apikey", encoded = true) String apikey,
                                  @Query(value = "nonce", encoded = true) String nonce,
                                  @HeaderMap Map<String, String> headers);

    @GET("market/buylimit")
    Call<BittrexResponse> buy(@Query(value = "market", encoded = true) String marketName,
                              @Query(value = "quantity", encoded = true) String quantity,
                              @Query(value = "rate", encoded = true) String rate,
                              @Query(value = "apikey", encoded = true) String apikey,
                              @Query(value = "nonce", encoded = true) String nonce,
                              @HeaderMap Map<String, String> headers);

    @GET("market/selllimit")
    Call<BittrexResponse> sell(@Query(value = "market", encoded = true) String marketName,
                               @Query(value = "quantity", encoded = true) String quantity,
                               @Query(value = "rate", encoded = true) String rate,
                               @Query(value = "apikey", encoded = true) String apikey,
                               @Query(value = "nonce", encoded = true) String nonce,
                               @HeaderMap Map<String, String> headers);

    @GET("account/getorderhistory")
    Call <BittrexResponse> getOrderHistory(@Query(value = "apikey", encoded = true) String apikey,
                                           @Query(value = "nonce", encoded = true)String nonce,
                                           @HeaderMap Map<String, String> headers);

    @GET("account/getwithdrawalhistory")
    Call <BittrexResponse> getWithdrawHistory(@Query(value = "apikey", encoded = true) String apikey,
                                              @Query(value = "nonce", encoded = true)String nonce,
                                              @HeaderMap Map<String, String> headers);

    @GET("account/getdeposithistory")
    Call <BittrexResponse> getDepositHistory(@Query(value = "apikey", encoded = true) String apikey,
                                              @Query(value = "nonce", encoded = true)String nonce,
                                              @HeaderMap Map<String, String> headers);


    @GET("market/getopenorders")
    Call <BittrexResponse> getOpenOrders(@Query(value = "apikey", encoded = true) String apikey,
                                              @Query(value = "nonce", encoded = true)String nonce,
                                              @HeaderMap Map<String, String> headers);
}
