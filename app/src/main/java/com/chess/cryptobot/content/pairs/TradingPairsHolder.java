package com.chess.cryptobot.content.pairs;

import android.content.Context;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.model.TradingPair;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.task.TradingPairsUpdateTask;

import java.util.Set;

public class TradingPairsHolder extends ContextHolder {

    public TradingPairsHolder(Context context) {
        super(context);
    }

    @Override
    public Preferences initPrefs(Context context) {
        return new TradingPairsPreferences(context);
    }

    public void initViewItems(Set<String> coinNames) {
        TradingPair tradingPair;
        for (String baseName : coinNames) {
            for (String marketName : coinNames) {
                if (!baseName.equals(marketName)) {
                    tradingPair = new TradingPair(baseName, marketName);
                    add(tradingPair);
                }
            }
        }
    }

    public void updateItem(ViewItem item) {
        TradingPair tradingPair = (TradingPair) item;
        TradingPairsUpdateTask task = new TradingPairsUpdateTask(this);
        task.execute(tradingPair);
    }
}
