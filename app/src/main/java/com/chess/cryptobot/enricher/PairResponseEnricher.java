package com.chess.cryptobot.enricher;

import com.chess.cryptobot.model.Pair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexResponse;

public class PairResponseEnricher {
    private Pair pair;

    public PairResponseEnricher(Pair pair) {
        this.pair = pair;
    }

   public void enrichWithResponse(OrderBookResponse response) {
        if (response instanceof BittrexResponse) {
            pair.setBittrexAsk(response.asks().get(0).getValue());
            pair.setBittrexBid(response.bids().get(0).getValue());
        } else {
            pair.setLivecoinAsk(response.asks().get(0).getValue());
            pair.setLivecoinBid(response.bids().get(0).getValue());
        }
   }

   public PairResponseEnricher countPercent(Float fee) {
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
