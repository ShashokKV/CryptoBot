package com.chess.cryptobot.task;

import android.content.Context;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.pairs.PairsHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AvailablePairsTask extends MarketTask<Integer, List<String>> {
    private List<String> allMarketNames;

    public AvailablePairsTask(PairsHolder pairsHolder) {
        super(pairsHolder);
    }

    @Override
    public void preMarketProcess(Integer param) {
        allMarketNames = new LinkedList<>();
    }

    @Override
    public List<String> marketProcess(Market market, Integer param) throws MarketException {
        List<String> marketNames = market.getAllMarkets();
        if (allMarketNames.isEmpty()) {
            allMarketNames = marketNames;
        }else {
            allMarketNames.retainAll(marketNames);
        }
        return allMarketNames;
    }

    @Override
    public List<String> postMarketProcess(List<String> allMarketNames) {return allMarketNames;}

    @Override
    public List<String> exceptionProcess(Integer param, String exceptionMessage) {return null;}

    @Override
    public void doInPostExecute(List<String> allMarketNames, ContextHolder holder) {
        PairsHolder pairsHolder = (PairsHolder) holder;
        pairsHolder.setAvailablePairs(excludeMarkets(allMarketNames, holder));
        pairsHolder.removeInvalidPairs();
        pairsHolder.updateAllItems();
    }

    @Override
    public void doInOnCanceled(List<String> result, ContextHolder holder) {}

    private List<String> excludeMarkets(List<String> allMarketNames, ContextHolder holder) {
        Context context = holder.getContext();
        List<String> invalidPairs = Arrays.asList(context.getString(R.string.ignored_pairs).split("#"));
        allMarketNames.removeAll(invalidPairs);
        List<String> usdPairs = new ArrayList<>();
        allMarketNames.forEach(marketName -> {
            if (marketName.startsWith("USD/")) usdPairs.add(marketName);
        });
        allMarketNames.removeAll(usdPairs);
        return allMarketNames;
    }
}
