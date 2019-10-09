package com.chess.cryptobot.market;

import com.chess.cryptobot.api.MarketService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

abstract class MarketAuthenticator {
    String url;
    String apiKey;
    String algorithm;
    private String secretKey;
    private final String DEFAULT_ENCODING = "UTF-8";
    Retrofit retrofit;
    MarketService service;

    MarketAuthenticator(String url, String apiKey, String secretKey) {
        this.url = url;
        this.apiKey = apiKey;
        this.secretKey = secretKey;

        retrofit = new Retrofit.Builder()
                .baseUrl(this.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = getService();
    }

    abstract MarketService getService();

    abstract String makeHash(Map<String, String> queryParams);

    String encode(String value) {
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

    abstract String asString(byte[] bytes);

    String buildQueryString(Map<String, String> args) {
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
}
