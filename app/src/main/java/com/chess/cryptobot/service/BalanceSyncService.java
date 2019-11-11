package com.chess.cryptobot.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.BittrexMarket;
import com.chess.cryptobot.market.LivecoinMarket;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceSyncService extends IntentService {

    public BalanceSyncService() {
        super("BalanceSyncService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent==null) return;
        List<String> coinNames = intent.getStringArrayListExtra("coinNames");

        MarketFactory marketFactory = new MarketFactory();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        List<Market> markets = marketFactory.getMarkets(this, preferences);

        coinNames.forEach(coinName -> sync(coinName, markets));
    }

    private void sync(String coinName, List<Market> markets) {
        Double minBalance = new BalancePreferences(this).getMinBalance(coinName);
        if (minBalance==0.0f) return;

        Map<String, Double> marketAmounts = new HashMap<>();
        for(Market market: markets) {
            try {
                marketAmounts.put(market.getMarketName(), market.getAmount(coinName));
            } catch (MarketException e) {
                e.printStackTrace();
                return;
            }
        }

        Double bittrexAm = marketAmounts.get(Market.BITTREX_MARKET);
        if (bittrexAm==null) bittrexAm = 0.0d;
        Double livecoinAm = marketAmounts.get(Market.LIVECOIN_MARKET);
        if (livecoinAm==null) livecoinAm = 0.0d;
        Double bittrexDelta, livecoinDelta;

        LivecoinMarket livecoinMarket = null;
        BittrexMarket bittrexMarket = null;
        for (Market market : markets) {
            if (market instanceof LivecoinMarket) {
                livecoinMarket = (LivecoinMarket) market;
            } else if (market instanceof  BittrexMarket) {
                bittrexMarket = (BittrexMarket) market;
            }
        }

        if (minBalance > bittrexAm) {
            bittrexDelta = livecoinAm - bittrexAm;
            if (bittrexDelta>minBalance) {
                moveBalances(livecoinMarket, bittrexMarket, coinName, bittrexDelta);
            }
        }
    }

    private void moveBalances(Market moveFrom, Market moveTo, String coinName, Double amount) {

    }
}
