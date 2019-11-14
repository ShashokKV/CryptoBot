package com.chess.cryptobot.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.exceptions.SyncServiceException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.response.CurrenciesResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.chess.cryptobot.market.Market.BITTREX_MARKET;
import static com.chess.cryptobot.market.Market.LIVECOIN_MARKET;

public class BalanceSyncService extends IntentService {
    private List<String> resultInfo = new ArrayList<>();
    private Map<String, Map<String, Boolean>> statuses = new HashMap<>();
    private Map<String, Map<String, Double>> fees = new HashMap<>();
    private Map<String, Market> marketsMap = new HashMap<>();

    public BalanceSyncService() {
        super("BalanceSyncService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        List<String> coinNames = intent.getStringArrayListExtra("coinNames");

        MarketFactory marketFactory = new MarketFactory();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        List<Market> markets = marketFactory.getMarkets(this, preferences);
        markets.forEach(market -> marketsMap.put(market.getMarketName(), market));

        try {
            initCoinInfo(markets);
        } catch (MarketException e) {
            e.printStackTrace();
        }

        coinNames.forEach(coinName -> {
            try {
                sync(coinName, markets);
            } catch (SyncServiceException e) {
                updateInfo(coinName, e.getMessage());
            }
        });
    }

    private void initCoinInfo(List<Market> markets) throws MarketException {
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

    private void sync(String coinName, List<Market> markets) throws SyncServiceException {
        Double minBalance = new BalancePreferences(this).getMinBalance(coinName);
        if (minBalance == 0.0f) {
            throw new SyncServiceException("Min balance not set");
        }

        if (!checkCoinStatus(coinName)) {
            throw new SyncServiceException( "Not active");
        }

        Map<String,Double> marketAmounts;
        try {
            marketAmounts = getMarketsAmmounts(markets, coinName);
        } catch (MarketException e) {
            throw new SyncServiceException(e.getMessage());
        }
        if (marketAmounts==null) return;

        Double bittrexAm = getAmount(marketAmounts, BITTREX_MARKET);
        Double livecoinAm = getAmount(marketAmounts, LIVECOIN_MARKET);

        Double livecoinFee = getFee(LIVECOIN_MARKET, coinName);
        Double bittrexFee = getFee(BITTREX_MARKET, coinName);

        Double bittrexDelta = getDelta(bittrexAm, minBalance);
        if (needSync(bittrexDelta)) {
            checkDelta(bittrexDelta, bittrexFee, livecoinAm);
            try {
                moveBalances(Objects.requireNonNull(marketsMap.get(LIVECOIN_MARKET)),
                        Objects.requireNonNull(marketsMap.get(BITTREX_MARKET)), coinName, bittrexDelta);
            } catch (MarketException e) {
                throw new SyncServiceException(e.getMessage());
            }
        }
        Double livecoinDelta = getDelta(livecoinAm, minBalance);
        if (needSync(livecoinDelta)) {
            checkDelta(livecoinDelta, livecoinFee, bittrexAm);
            try {
                moveBalances(Objects.requireNonNull(marketsMap.get(BITTREX_MARKET)),
                        Objects.requireNonNull(marketsMap.get(LIVECOIN_MARKET)), coinName, livecoinDelta);
            } catch (MarketException e) {
                throw new SyncServiceException(e.getMessage());
            }
        }
    }

    private Double getAmount(Map<String, Double> amounts, String marketName) throws SyncServiceException {
        Double amount = amounts.get(marketName);
        if (amount == null) throw new SyncServiceException("Can't get amount");
        return amount;
    }

    private Double getFee(String marketName, String coinName) throws SyncServiceException {
        Map<String, Double> fees = this.fees.get(marketName);
        if (fees == null) {
            throw new SyncServiceException("Can't get fees");
        }

        Double fee = fees.get(coinName);
        if (fee == null) {
            throw new SyncServiceException("Can't get fee from "+marketName);
        }
        return fee;
    }

    private boolean checkCoinStatus(String coinName) {
        Map<String, Boolean> bittrexStatuses = statuses.get(BITTREX_MARKET);
        if (bittrexStatuses==null) return false;
        Map<String, Boolean> livecoinStatuses = statuses.get(LIVECOIN_MARKET);
        if (livecoinStatuses==null) return false;

        Boolean bittrexStatus = bittrexStatuses.get(coinName);
        Boolean livecoinStatus = livecoinStatuses.get(coinName);

        if (bittrexStatus==null || livecoinStatus==null) return false;
        return (bittrexStatus && livecoinStatus);
    }

    private Map<String, Double> getMarketsAmmounts(List<Market> markets, String coinName) throws MarketException {
        Map<String, Double> marketAmounts = new HashMap<>();
        for (Market market : markets) {
                marketAmounts.put(market.getMarketName(), market.getAmount(coinName));
        }
        return marketAmounts;
    }

    private Double getDelta(Double amount, Double minBalance) {
        return minBalance - amount;
    }

    private boolean needSync(Double delta) {
        return delta>0;
    }

    private void checkDelta(Double delta, Double fee,Double fromAmount) throws SyncServiceException {
        if (fromAmount<(delta+fee)) throw new SyncServiceException("Not enough coins");
    }

    private void moveBalances(Market moveFrom, Market moveTo, String coinName, Double amount) throws MarketException {
        String address = moveTo.getAddress();

        moveFrom.sendCoins(coinName, amount, address);
    }

    private void updateInfo(String coinName, String message) {
        resultInfo.add(String.format("%s: %s", coinName, message));
    }
}
