package com.chess.cryptobot.model;

public class TradingPair {
    private String baseName;
    private String marketName;
    private Double bittrexAsk;
    private Double bittrexBid;
    private Double livecoinAsk;
    private Double livecoinBid;
    private float percent;

    public TradingPair(String baseName, String marketName) {
        this.baseName = baseName;
        this.marketName = marketName;
        this.bittrexAsk = 0.0d;
        this.bittrexBid = 0.0d;
        this.livecoinAsk = 0.0d;
        this.livecoinBid = 0.0d;
        this.percent = 0.0f;
    }

    public TradingPair(TradingPair tradingPair) {
        this.baseName = tradingPair.baseName;
        this.marketName = tradingPair.marketName;
        this.livecoinBid = tradingPair.livecoinBid;
        this.livecoinAsk = tradingPair.livecoinAsk;
        this.bittrexBid = tradingPair.bittrexBid;
        this.bittrexAsk = tradingPair.bittrexAsk;
        this.percent = tradingPair.percent;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getMarketName() {
        return marketName;
    }

    public Double getBittrexAsk() {
        return bittrexAsk;
    }

    public Double getBittrexBid() {
        return bittrexBid;
    }

    public Double getLivecoinAsk() {
        return livecoinAsk;
    }

    public Double getLivecoinBid() {
        return livecoinBid;
    }

    public float getPercent() {
        return percent;
    }

    public String getBittrexPairName() {
        return baseName.concat("-").concat(marketName);
    }

    public String getLIvecoinPairName() {
        return marketName.concat("/").concat(baseName);
    }
}
