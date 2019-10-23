package com.chess.cryptobot.content.pairs;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.model.TradingPair;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.task.TradingPairsUpdateTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TradingPairsHolder extends ContextHolder {
    private List<String> invalidTradingPairs;

    public TradingPairsHolder(Fragment fragment) {
        super(fragment);
        invalidTradingPairs = new ArrayList<>();
    }

    @Override
    public Preferences initPrefs(Context context) {
        return new TradingPairsPreferences(context);
    }

    public void initViewItems(Set<String> pairNames) {
        pairNames.forEach(pairName -> addItemToList(TradingPair.fromPairName(pairName)));
    }

    public void updateFromBalance(Set<String> coinNames) {
        List<TradingPair> balanceTradingPairs = new ArrayList<>();
        for (String baseName : coinNames) {
            for (String marketName : coinNames) {
                if (!baseName.equals(marketName)) {
                    balanceTradingPairs.add(new TradingPair(baseName, marketName));
                }
            }
        }
        removeIfNotExists(balanceTradingPairs);
        addIfNotExists(balanceTradingPairs);
    }

    private void addIfNotExists(List<TradingPair> balanceTradingPairs) {
        balanceTradingPairs.forEach(tradingPair -> {
            if (!getViewItems().contains(tradingPair) && !invalidTradingPairs.contains(tradingPair.getName())) {
                add(tradingPair);
            }
        });
    }

    private void removeIfNotExists(List<TradingPair> balanceTradingPairs) {
        for (int i=0; i<getViewItems().size(); i++) {
            TradingPair tradingPair = (TradingPair) getViewItems().get(i);
            if (!balanceTradingPairs.contains(tradingPair)) {
                remove(tradingPair);
            }
        }
    }

    public void updateItem(ViewItem item) {
        TradingPair tradingPair = (TradingPair) item;
        TradingPairsUpdateTask task = new TradingPairsUpdateTask(this);
        task.execute(tradingPair);
    }


    public void addToInvalidPairs(TradingPair tradingPair) {
        String pairName = tradingPair.getName();
        if (!invalidTradingPairs.contains(pairName)) invalidTradingPairs.add(pairName);
    }
}
