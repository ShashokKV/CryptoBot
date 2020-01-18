package com.chess.cryptobot.enricher;

import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.Price;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;

import java.util.List;

public class PairResponseEnricher {
    private Pair pair;
    private final static Double bittrexFee = 0.25;
    private final static Double livecoinFee = 0.18;
    private OrderBookResponse bittrexResponse;
    private OrderBookResponse livecoinResponse;
    private List<Price> bids;
    private List<Price> asks;
    private Double ask;
    private Double bid;
    private boolean bidFromLivecoin;
    private Float minPercent;
    private Double bidQuantity;
    private Double askQuantity;


    public PairResponseEnricher(Pair pair) {
        this.pair = pair;
    }

    public void enrichWithResponse(OrderBookResponse response) {
        if (response instanceof BittrexResponse) {
            bittrexResponse = response;
            pair.setBittrexAsk(response.asks().get(0).getValue());
            pair.setBittrexAskQuantity(response.asks().get(0).getQuantity());
            pair.setBittrexBid(response.bids().get(0).getValue());
            pair.setBittrexBidQuantity(response.bids().get(0).getQuantity());
        } else if (response instanceof LivecoinOrderBookResponse) {
            livecoinResponse = response;
            pair.setLivecoinAsk(response.asks().get(0).getValue());
            pair.setLivecoinAskQuantity(response.asks().get(0).getQuantity());
            pair.setLivecoinBid(response.bids().get(0).getValue());
            pair.setLivecoinBidQuantity(response.bids().get(0).getQuantity());
        }
    }

    public PairResponseEnricher enrichWithMinPercent(Float minPercent) {
        this.minPercent = minPercent;
        Double lAsk = livecoinResponse.asks().get(0).getValue();
        Double lBid = livecoinResponse.bids().get(0).getValue();
        Double bAsk = bittrexResponse.asks().get(0).getValue();
        Double bBid = bittrexResponse.bids().get(0).getValue();

        if ((lBid - bAsk) > (bBid - lAsk)) {
            bid = lBid;
            ask = bAsk;
            bids = livecoinResponse.bids();
            asks = bittrexResponse.asks();
            bidFromLivecoin = true;
        } else {
            bid = bBid;
            ask = lAsk;
            bids = bittrexResponse.bids();
            asks = livecoinResponse.asks();
            bidFromLivecoin = false;
        }

        Pair tmpPair = pair;
        tmpPair.setPercent(0.0f);

        bidQuantity = bids.get(0).getQuantity();
        askQuantity = asks.get(0).getQuantity();

        boolean increaseAsks = (bidQuantity > askQuantity);
        int maxPriceSize = bids.size() > asks.size() ? asks.size() : bids.size();

        for (int i = 1; i < maxPriceSize; i++) {
            if (!enrichedFromStack(tmpPair, i, increaseAsks)) {
                return this;
            }
        }
        return this;
    }

    private boolean enrichedFromStack(Pair tmpPair, int i, boolean increaseAsks) {
        Float percent = countPercent(bid, ask, bidFromLivecoin);
        if (percent > minPercent) {
            updateTempPair(tmpPair);
            tmpPair.setPercent(percent);

            if (increaseAsks) {
                askQuantity = askQuantity + asks.get(i).getQuantity();
                ask = asks.get(i).getValue();
            } else {
                bidQuantity = bidQuantity + bids.get(i).getQuantity();
                bid = bids.get(i).getValue();
            }
            return true;
        } else {
            pair = tmpPair;
            return false;
        }
    }

    private void updateTempPair(Pair tmpPair) {
        if (bidFromLivecoin) {
            tmpPair.setLivecoinBidQuantity(bidQuantity);
            tmpPair.setBittrexAskQuantity(askQuantity);
            tmpPair.setLivecoinBid(bid);
            tmpPair.setBittrexAsk(ask);
        } else {
            tmpPair.setBittrexBid(bidQuantity);
            tmpPair.setLivecoinAskQuantity(askQuantity);
            tmpPair.setBittrexBid(bid);
            tmpPair.setLivecoinAsk(ask);
        }
    }


    private Float countPercent(Double bid, Double ask, boolean bidFromLivecoin) {
        if (bid == null || ask == null) return 0.0f;

        Double bidFee = bidFromLivecoin ? livecoinFee : bittrexFee;
        Double askFee = bidFromLivecoin ? bittrexFee : livecoinFee;
        Float percentBid = Double.valueOf(bid - ((bid / 100) * bidFee)).floatValue();
        Float percentAsk = Double.valueOf(ask + ((ask / 100) * askFee)).floatValue();
        return ((percentBid - percentAsk) / percentBid) * 100;
    }

    public PairResponseEnricher countPercent() {
        if (pair.getLivecoinAsk() == null || pair.getLivecoinBid() == null
                || pair.getBittrexAsk() == null || pair.getBittrexBid() == null
                || pair.getLivecoinAsk() == 0 || pair.getBittrexAsk() == 0) {
            pair.setPercent(0.0f);
            return this;
        }

        Float bb = Double.valueOf(pair.getBittrexBid() - ((pair.getBittrexBid() / 100) * bittrexFee)).floatValue();
        Float ba = Double.valueOf(pair.getBittrexAsk() + ((pair.getBittrexAsk() / 100) * bittrexFee)).floatValue();
        Float lb = Double.valueOf(pair.getLivecoinBid() - ((pair.getLivecoinBid() / 100) * livecoinFee)).floatValue();
        Float la = Double.valueOf(pair.getLivecoinAsk() + ((pair.getLivecoinAsk() / 100) * livecoinFee)).floatValue();

        Float bittrexPercent = ((bb - la) / la) * 100;
        Float livecoinPercent = ((lb - ba) / ba) * 100;
        if (bittrexPercent > livecoinPercent) {
            pair.setPercent(bittrexPercent);
        } else {
            pair.setPercent(livecoinPercent);
        }
        return this;
    }

    public Pair getPair() {
        return pair;
    }
}
