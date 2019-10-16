package com.chess.cryptobot.content.pairs;

import android.content.Context;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.model.TradingPair;

import java.util.ArrayList;

public class TradingPairsHolder extends ContextHolder {
private TradingPairsPreferences prefs;
private ArrayList<TradingPair> tradingPairs;

    public TradingPairsHolder(Context context) {
        super(context);
        prefs = new TradingPairsPreferences(context);
        initPairs();
    }

    @Override
    public Preferences getPrefs() {
        return this.prefs;
    }

    private void initPairs() {
        tradingPairs = new ArrayList<>();
        String[] coinNames = prefs.getCoinNames().toArray(new String[0]);
        for (String name : coinNames) {
            for (String coinName : coinNames) {
                if (!name.equals(coinName)) {
                    tradingPairs.add(new TradingPair(name, coinName));
                }
            }
        }
    }

    public ArrayList<TradingPair> getTradingPairs() {
        return this.tradingPairs;
    }
}
