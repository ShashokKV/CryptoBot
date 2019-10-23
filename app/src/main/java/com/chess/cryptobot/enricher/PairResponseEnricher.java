package com.chess.cryptobot.enricher;

import com.chess.cryptobot.model.TradingPair;
import com.chess.cryptobot.model.response.OrderBookResponse;
import com.chess.cryptobot.model.response.bittrex.BittrexResponse;

public class PairResponseEnricher {
    private TradingPair tradingPair;

    public PairResponseEnricher(TradingPair tradingPair) {
        this.tradingPair = tradingPair;
    }

   public void enrichWithResponse(OrderBookResponse response) {
        if (response instanceof BittrexResponse) {
            tradingPair.setBittrexAsk(response.asks().get(0).getValue());
            tradingPair.setBittrexBid(response.bids().get(0).getValue());
        } else {
            tradingPair.setLivecoinAsk(response.asks().get(0).getValue());
            tradingPair.setLivecoinBid(response.bids().get(0).getValue());
        }
   }

   public PairResponseEnricher countPercent() {
        Float bittrexPercent = (Double.valueOf((tradingPair.getLivecoinAsk() - tradingPair.getBittrexBid())
                /tradingPair.getBittrexBid()*100)).floatValue();
        Float livecoinPercent = (Double.valueOf((tradingPair.getBittrexAsk() - tradingPair.getLivecoinBid())
                /tradingPair.getLivecoinBid()*100)).floatValue();
        if (bittrexPercent>livecoinPercent) {
            tradingPair.setPercent(bittrexPercent);
        }else {
            tradingPair.setPercent(livecoinPercent);
        }
        return  this;
   }

    public TradingPair getTradingPair() {
        return tradingPair;
    }
}
