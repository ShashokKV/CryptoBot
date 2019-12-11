package com.chess.cryptobot.enricher;

import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;

public class PairResponseEnricher {
    private final Pair pair;
    private final static Double bittrexFee = 0.25;
    private final static Double livecoinFee = 0.18;


    public PairResponseEnricher(Pair pair) {
        this.pair = pair;
    }

    public void enrichWithResponse(OrderBookResponse response) {
        if (response instanceof BittrexResponse) {
            pair.setBittrexAsk(response.asks().get(0).getValue());
            pair.setBittrexAskQuantity(response.asks().get(0).getQuantity());
            pair.setBittrexBid(response.bids().get(0).getValue());
            pair.setBittrexBidQuantity(response.bids().get(0).getQuantity());
        } else if (response instanceof LivecoinOrderBookResponse) {
            pair.setLivecoinAsk(response.asks().get(0).getValue());
            pair.setLivecoinAskQuantity(response.asks().get(0).getQuantity());
            pair.setLivecoinBid(response.bids().get(0).getValue());
            pair.setLivecoinBidQuantity(response.bids().get(0).getQuantity());
        }
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
