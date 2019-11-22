package com.chess.cryptobot.task;

import android.content.Context;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.pairs.PairsHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.model.response.TickerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AvailablePairsTask extends MarketTask<Integer, List<String>> {
    private List<String> availablePairNames;
    private Map<String, Double> bittrexVolumes;
    private Map<String, Double> livecoinVolumes;

    public AvailablePairsTask(PairsHolder pairsHolder) {
        super(pairsHolder);
    }

    @Override
    public void preMarketProcess(Integer param) {
        bittrexVolumes = new HashMap<>();
        livecoinVolumes = new HashMap<>();
    }

    @Override
    public List<String> marketProcess(Market market, Integer param) throws MarketException {
        List<String> pairNames;
        List<? extends TickerResponse> tickers = market.getTicker();
        pairNames = getPairNames(tickers);
        updateVolumesForMarket(market.getMarketName(), tickers);

        if (availablePairNames == null) {
            availablePairNames = new LinkedList<>(pairNames);
        } else {
            availablePairNames.retainAll(pairNames);
            livecoinVolumes.keySet().retainAll(availablePairNames);
            bittrexVolumes.keySet().retainAll(availablePairNames);
        }

        return availablePairNames;
    }

    private List<String> getPairNames(List<? extends TickerResponse> tickers) {
        List<String> pairNames = new ArrayList<>();
        tickers.forEach(ticker -> pairNames.add(ticker.getMarketName()));
        return pairNames;
    }

    private void updateVolumesForMarket(String marketName, List<? extends TickerResponse> tickers) {
        if (marketName.equals(Market.BITTREX_MARKET)) {
            updateVolumes(bittrexVolumes, tickers);
        } else {
            updateVolumes(livecoinVolumes, tickers);
        }
    }

    private void updateVolumes(Map<String, Double> volumeMap, List<? extends TickerResponse> tickers) {
        tickers.forEach(ticker -> volumeMap.put(ticker.getMarketName(), ticker.getVolume()));
    }

    @Override
    public List<String> postMarketProcess(List<String> allMarketNames) {
        return allMarketNames;
    }

    @Override
    public List<String> exceptionProcess(Integer param, String exceptionMessage) {
        return null;
    }

    @Override
    public void doInPostExecute(List<String> allPairNames, ContextHolder holder) {
        PairsHolder pairsHolder = (PairsHolder) holder;
        pairsHolder.setAvailablePairs(excludePairs(allPairNames, holder));
        pairsHolder.setVolumes(bittrexVolumes, livecoinVolumes);
        pairsHolder.removeInvalidPairs();
        pairsHolder.updateAllItems();
    }

    @Override
    public void doInOnCanceled(List<String> result, ContextHolder holder) {
    }

    private List<String> excludePairs(List<String> allPairNames, ContextHolder holder) {
        Context context = holder.getContext();
        List<String> invalidPairs = Arrays.asList(context.getString(R.string.ignored_pairs).split("#"));
        allPairNames.removeAll(invalidPairs);
        List<String> usdPairs = new ArrayList<>();
        allPairNames.forEach(marketName -> {
            if (marketName.startsWith("USD/")) usdPairs.add(marketName);
        });
        allPairNames.removeAll(usdPairs);
        return allPairNames;
    }
}
