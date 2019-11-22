package com.chess.cryptobot.enricher;

import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexResponse;
import com.chess.cryptobot.model.response.livecoin.LivecoinOrderBookResponse;

public class PairResponseEnricher {
    private final Pair pair;

    public PairResponseEnricher(Pair pair) {
        this.pair = pair;
    }

   public void enrichWithResponse(OrderBookResponse response) {
        if (response instanceof BittrexResponse) {
            pair.setBittrexAsk(response.asks().get(0).getValue());
            pair.setBittrexAskQuantity(response.asks().get(0).getQuantity());
            pair.setBittrexBid(response.bids().get(0).getValue());
            pair.setBittrexBidQuantity(response.bids().get(0).getQuantity());
        } else if (response instanceof LivecoinOrderBookResponse){
            pair.setLivecoinAsk(response.asks().get(0).getValue());
            pair.setLivecoinAskQuantity(response.asks().get(0).getQuantity());
            pair.setLivecoinBid(response.bids().get(0).getValue());
            pair.setLivecoinBidQuantity(response.bids().get(0).getQuantity());
        }
   }

   public PairResponseEnricher countPercent(Float fee) {
        if (pair.getLivecoinAsk()==0 || pair.getBittrexAsk()==0) {
            pair.setPercent(0.0f);
            return this;
        }
        Float bittrexPercent = (Double.valueOf((pair.getBittrexBid() - pair.getLivecoinAsk())
                / pair.getLivecoinAsk()*100)).floatValue()-fee;
        Float livecoinPercent = (Double.valueOf((pair.getLivecoinBid() - pair.getBittrexAsk())
                / pair.getBittrexAsk()*100)).floatValue()-fee;
        if (bittrexPercent>livecoinPercent) {
            pair.setPercent(bittrexPercent);
        }else {
            pair.setPercent(livecoinPercent);
        }
        return  this;
   }

    public Pair getPair() {
        return pair;
    }
}
