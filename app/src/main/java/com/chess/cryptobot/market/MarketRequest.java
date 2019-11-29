package com.chess.cryptobot.market;

import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.ErrorResponse;
import com.chess.cryptobot.model.response.MarketResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

abstract class MarketRequest implements Market {
    final String url;
    String path;
    final String apiKey;
    String algorithm;
    private final String secretKey;
    private final String DEFAULT_ENCODING = "UTF-8";

    MarketRequest(String url, String apiKey, String secretKey) {
        this.url = url;
        this.apiKey = apiKey == null ? "" : apiKey;
        this.secretKey = secretKey == null ? "" : secretKey;
    }

    abstract Gson initGson();

    Retrofit initRetrofit(Gson gson) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(this.url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    abstract Object initService(Retrofit retrofit);

    public boolean keysIsEmpty() {
        return this.apiKey.isEmpty() || this.secretKey.isEmpty();
    }

    private String encode(String value) {
        return asString(encodeToBytes(value));
    }

    private byte[] encodeToBytes(String value) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    this.secretKey.getBytes(DEFAULT_ENCODING), algorithm);

            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac.doFinal(value.getBytes(DEFAULT_ENCODING));

        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String asString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }

    private String buildQueryString(Map<String, String> args) {
        StringBuilder result = new StringBuilder();
        for (String param : args.keySet()) {
            if (result.length() > 0) result.append('&');
            try {
                result.append(URLEncoder.encode(param, DEFAULT_ENCODING))
                        .append("=").append(URLEncoder.encode(args.get(param), DEFAULT_ENCODING));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    String makeHash(Map<String, String> queryParams) {
        return encode(String.format(Locale.US, "%s%s", this.path, buildQueryString(queryParams)));
    }

    MarketResponse execute(Call<? extends MarketResponse> call) throws MarketException {
        MarketResponse response;
        try {
            Response<?> result = call.execute();
            response = (MarketResponse) result.body();
            if (response == null) {
                ResponseBody errorBody = result.errorBody();
                if (errorBody == null) {
                    throw new MarketException("No response");
                } else {
                    Gson gson = new GsonBuilder().create();
                    ErrorResponse errorResponse = gson.fromJson(errorBody.string(), ErrorResponse.class);
                    throw new MarketException(errorResponse.getErrorMessage());
                }
            } else if (!response.success()) {
                throw new MarketException(response.message());
            }
        } catch (IOException e) {
            throw new MarketException(e.getMessage());
        }
        return response;
    }
}
