package com.chess.cryptobot.task;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.pairs.PairsHolder;
import com.chess.cryptobot.enricher.PairResponseEnricher;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;

public class PairsUpdateTask extends MarketTask<Pair, Pair> {
    private PairResponseEnricher enricher;

    public PairsUpdateTask(PairsHolder pairsHolder) {
        super(pairsHolder);
    }

    @Override
    public void preMarketProcess(Pair pair) {
        this.enricher = new PairResponseEnricher(pair);
    }

    @Override
    public Pair marketProcess(Market market, Pair pair) throws MarketException {
        OrderBookResponse response = market.getOrderBook(pair.getPairNameForMarket(market.getMarketName()));
        enricher.enrichWithResponse(response);
        return null;
    }

    @Override
    public Pair postMarketProcess(Pair pair) {
        return enricher.countPercent().getPair();
    }

    @Override
    public Pair exceptionProcess(Pair pair, String exceptionMessage) {
        pair.setMessage(exceptionMessage);
        return pair;
    }

    @Override
    public void doInPostExecute(Pair pair, ContextHolder holder) {
        PairsHolder pairsHolder = (PairsHolder) holder;
        if (pair.getPercent() < 0) {
            pairsHolder.remove(pair);
            pairsHolder.addToNegativePercentPairs(pair);
        } else {
            pairsHolder.setItem(pair);
        }
    }

    @Override
    public void doInOnCanceled(Pair pair, ContextHolder holder) {
        String message = pair.getMessage();
        if (message == null) return;
        PairsHolder pairsHolder = (PairsHolder) holder;
        if (message.contains("Unknown currency pair") || message.contains("INVALID_MARKET")) {
            pairsHolder.addToInvalidPairs(pair);
        } else {
            pairsHolder.makeToast(message);
        }
        pairsHolder.remove(pair);
    }
}
