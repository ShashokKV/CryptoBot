package com.chess.cryptobot.util;

import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.exceptions.SyncServiceException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.model.response.CurrenciesResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chess.cryptobot.market.Market.BITTREX_MARKET;
import static com.chess.cryptobot.market.Market.LIVECOIN_MARKET;

public class CoinInfo {
    private final Map<String, Map<String, Boolean>> statuses = new HashMap<>();
    private final Map<String, Map<String, Double>> fees = new HashMap<>();

    public CoinInfo(List<Market> markets) throws MarketException {
        for (Market market : markets) {
            List<CurrenciesResponse> currencies = market.getCurrencies();
            Map<String, Boolean> statuses = new HashMap<>();
            Map<String, Double> fees = new HashMap<>();
            currencies.forEach(currency -> {
                String currencyName = currency.getCurrencyName();
                statuses.put(currencyName, currency.isActive());
                fees.put(currencyName, currency.getFee());
            });
            this.statuses.put(market.getMarketName(), statuses);
            this.fees.put(market.getMarketName(), fees);
        }
    }

    public boolean checkCoinStatus(String coinName) {
        Map<String, Boolean> bittrexStatuses = statuses.get(BITTREX_MARKET);
        if (bittrexStatuses == null) return false;
        Map<String, Boolean> livecoinStatuses = statuses.get(LIVECOIN_MARKET);
        if (livecoinStatuses == null) return false;

        Boolean bittrexStatus = bittrexStatuses.get(coinName);
        Boolean livecoinStatus = livecoinStatuses.get(coinName);

        if (bittrexStatus == null || livecoinStatus == null) return false;
        return (bittrexStatus && livecoinStatus);
    }

    public Double getFee(String marketName, String coinName) throws SyncServiceException {
        Map<String, Double> fees = this.fees.get(marketName);
        if (fees == null) {
            throw new SyncServiceException("Can't get fees");
        }

        Double fee = fees.get(coinName);
        if (fee == null) {
            throw new SyncServiceException("Can't get fee from " + marketName);
        }

        return fee;
    }

}
