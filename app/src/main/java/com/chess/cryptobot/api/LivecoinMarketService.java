package com.chess.cryptobot.api;

import com.chess.cryptobot.model.response.livecoin.LivecoinAddressResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinBalanceResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinCurrenciesListResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinTickerResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinTradeLimitResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface LivecoinMarketService {

    @GET("payment/balance")
    Call<LivecoinBalanceResponse> getBalance(@QueryMap(encoded = true) Map<String, String> options, @HeaderMap Map<String, String> headers);

    @GET("exchange/order_book")
    Call<LivecoinOrderBookResponse> getOrderBook(@QueryMap(encoded = true) Map<String, String> options);

    @GET("exchange/ticker")
    Call<List<LivecoinTickerResponse>> getTicker();

    @GET("info/coinInfo")
    Call<LivecoinCurrenciesListResponse> getCurrencies();

    @GET("exchange/restrictions")
    Call<LivecoinTradeLimitResponse> getMinTradeSize();

    @GET("payment/get/address")
    Call<LivecoinAddressResponse> getAddress(@QueryMap(encoded = true) Map<String, String> options, @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST("payment/out/coin")
    Call<LivecoinResponse> payment(@Field(value = "amount", encoded = true) String amount,
                                   @Field(value = "currency", encoded = true) String currency,
                                   @Field(value = "wallet", encoded = true) String wallet,
                                   @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST("exchange/buylimit")
    Call<LivecoinResponse> buy(@Field(value = "currencyPair", encoded = true) String currencyPair,
                                    @Field(value = "price", encoded = true) String price,
                                    @Field(value = "quantity", encoded = true) String quantity,
                                    @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST("exchange/selllimit")
    Call<LivecoinResponse> sell(@Field(value = "currencyPair", encoded = true) String currencyPair,
                                     @Field(value = "price", encoded = true) String price,
                                     @Field(value = "quantity", encoded = true) String quantity,
                                     @HeaderMap Map<String, String> headers);
}
