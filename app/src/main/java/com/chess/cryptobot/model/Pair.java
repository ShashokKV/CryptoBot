package com.chess.cryptobot.model;

import androidx.annotation.Nullable;

public class Pair implements ViewItem {
    private String baseName;
    private String marketName;
    private Double bittrexAsk;
    private Double bittrexBid;
    private Double livecoinAsk;
    private Double livecoinBid;
    private float percent;
    private String message;

    public Pair(String baseName, String marketName) {
        this.baseName = baseName;
        this.marketName = marketName;
        this.bittrexAsk = 0.0d;
        this.bittrexBid = 0.0d;
        this.livecoinAsk = 0.0d;
        this.livecoinBid = 0.0d;
        this.percent = 0.0f;
    }

    public static Pair fromPairName(String pairName) {
        String[] coinNames = pairName.split("/");
        return new Pair(coinNames[0], coinNames[1]);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null) return false;
        if (obj==this) return true;
        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            return pair.marketName.equals(this.marketName) &&
                    pair.baseName.equals(this.baseName);
        }
        return false;
    }

    @Override
    public int compareTo(ViewItem item) {
        Pair pair = (Pair) item;
        return (int) (this.percent- pair.percent)*100;
    }

    public String getName() {
        return baseName.concat("/").concat(marketName);
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

    public void setBittrexAsk(Double bittrexAsk) {
        this.bittrexAsk = bittrexAsk;
    }

    public void setBittrexBid(Double bittrexBid) {
        this.bittrexBid = bittrexBid;
    }

    public void setLivecoinAsk(Double livecoinAsk) {
        this.livecoinAsk = livecoinAsk;
    }

    public void setLivecoinBid(Double livecoinBid) {
        this.livecoinBid = livecoinBid;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public String getPairNameForMarket(String marketName) {
        if (marketName.equals("bittrex")) {
            return getBittrexPairName();
        }else {
            return getLIvecoinPairName();
        }
    }

    private String getBittrexPairName() {
        return baseName.concat("-").concat(marketName);
    }

    private String getLIvecoinPairName() {
        return marketName.concat("/").concat(baseName);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
