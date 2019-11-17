package com.chess.cryptobot.model.response.bittrex;

import com.chess.cryptobot.model.Price;
import com.chess.cryptobot.model.response.AddressResponse;
import com.chess.cryptobot.model.response.BalanceResponse;
import com.chess.cryptobot.model.response.CurrenciesListResponse;
import com.chess.cryptobot.model.response.CurrenciesResponse;
import com.chess.cryptobot.model.response.MarketResponse;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.PaymentResponse;
import com.chess.cryptobot.model.response.TickerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BittrexResponse implements MarketResponse,
        BalanceResponse,
        OrderBookResponse,
        CurrenciesListResponse,
        AddressResponse,
        PaymentResponse
{

    private Boolean success;
    public String message;
    private BittrexGenericResponse[] results;

    BittrexResponse(BittrexGenericResponse[] results) {
        this.results = results;
    }

    BittrexResponse(BittrexGenericResponse result) {
        this.results = new BittrexGenericResponse[1];
        results[0] = result;
    }

    void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public boolean success() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public Double getAmount() {
        return results[0].getAvailable();
    }

    @Override
    public List<Price> bids() {
        return parsePrices(results[0].getBuy());
    }

    @Override
    public List<Price> asks() {
        return parsePrices(results[0].getSell());
    }

    public List<TickerResponse> getTickers() {
        return Arrays.asList(results);
    }

    private List<Price> parsePrices(List<BittrexPrice> prices) {
        ArrayList<Price> parsedPrices = new ArrayList<>();
        prices.forEach(price -> parsedPrices.add(new Price(price.getRate(), price.getQuantity())));
        return parsedPrices;
    }

    @Override
    public List<CurrenciesResponse> getInfo() {
        return Arrays.asList(results);
    }

    @Override
    public String getAddress() {
        return results[0].getCryptoAddress();
    }

    @Override
    public String getPaymentId() {
        return results[0].getUuid();
    }
}