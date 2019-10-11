package com.chess.cryptobot.market;

import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.model.response.MarketResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

abstract class MarketRequest {
    String url;
    String path;
    String apiKey;
    String algorithm;
    private String secretKey;
    private final String DEFAULT_ENCODING = "UTF-8";

    MarketRequest(String url, String apiKey, String secretKey) {
        this.url = url;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    abstract Gson initGson();

    Retrofit initRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(this.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    abstract Object initService(Retrofit retrofit);

    boolean keysIsEmpty() {
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
        for (String hashKey : args.keySet()) {
            if (result.length() > 0) result.append('&');
            try {
                result.append(URLEncoder.encode(hashKey, DEFAULT_ENCODING))
                        .append("=").append(URLEncoder.encode(args.get(hashKey), DEFAULT_ENCODING));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    String makeHash(Map<String, String> queryParams) {
        return encode(String.format("%s%s", this.path, buildQueryString(queryParams)));
    }

    MarketResponse execute(Call<? extends MarketResponse> call) throws MarketException {
        MarketResponse response;
        try {
            Response<?> result = call.execute();
            response = (MarketResponse) result.body();
            if (response == null) {
                throw new MarketException("No response");
            } else if (!response.success()) {
                throw new MarketException(response.message());
            }
        } catch (
                IOException e) {
            throw new MarketException(e.getMessage());
        }
        return response;
    }
}
