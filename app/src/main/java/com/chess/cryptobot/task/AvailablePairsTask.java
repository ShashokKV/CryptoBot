package com.chess.cryptobot.task;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.pairs.PairsHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;

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
        pairsHolder.setAvailablePairs(allMarketNames);
        pairsHolder.removeInvalidPairs();
        pairsHolder.updateAllItems();
    }

    @Override
    public void doInOnCanceled(List<String> result, ContextHolder holder) {}
}
