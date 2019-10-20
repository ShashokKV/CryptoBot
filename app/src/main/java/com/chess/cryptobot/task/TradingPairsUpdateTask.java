package com.chess.cryptobot.task;

import android.os.AsyncTask;

import com.chess.cryptobot.content.pairs.TradingPairsHolder;
import com.chess.cryptobot.enricher.PairResponseEnricher;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.TradingPair;
import com.chess.cryptobot.model.response.OrderBookResponse;

import java.lang.ref.WeakReference;
import java.util.List;

public class TradingPairsUpdateTask extends AsyncTask<TradingPair, Integer, TradingPair> {
    private WeakReference<TradingPairsHolder> tradingPairsHolderWeakReference;

    public TradingPairsUpdateTask(TradingPairsHolder tradingPairsHolder) {
        this.tradingPairsHolderWeakReference = new WeakReference<>(tradingPairsHolder);
    }

    @Override
    protected TradingPair doInBackground(TradingPair... tradingPairs) {
        TradingPair tradingPair = tradingPairs[0];
        PairResponseEnricher enricher = new PairResponseEnricher(tradingPair);
        MarketFactory factory = new MarketFactory();
        List<Market> markets = factory.getMarkets(tradingPairsHolderWeakReference.get());
        for (Market market: markets) {
            try {
                OrderBookResponse response = market.getOrderBook(tradingPair.getPairNameForMarket(market.getMarketName()));
                enricher.enrichWithResponse(response);
            } catch (MarketException e) {
                cancel(true);
                tradingPair.setMessage(e.getMessage());
                return tradingPair;
            }
        }
        tradingPair = enricher.countPercent().getTradingPair();
        return tradingPair;
    }

    @Override
    protected void onPostExecute(TradingPair tradingPair) {
        if (tradingPair==null) return;
        tradingPairsHolderWeakReference.get().setItem(tradingPair);
    }

    @Override
    protected void onCancelled(TradingPair tradingPair) {
        tradingPairsHolderWeakReference.get().remove(tradingPair);
    }
}
