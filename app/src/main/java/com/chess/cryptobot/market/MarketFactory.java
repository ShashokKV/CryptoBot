package com.chess.cryptobot.market;

import android.content.Context;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;

import java.util.ArrayList;
import java.util.List;

public class MarketFactory {

    public List<Market> getMarkets(ContextHolder contextHolder) {
        String[] marketNames = {"bittrex", "livecoin"};
        List<Market> markets = new ArrayList<>();

        for (String marketName : marketNames) {
            markets.add(getMarket(marketName, contextHolder.getPrefs(), contextHolder.getContext()));
        }
        return markets;
    }

    private Market getMarket(String marketName, Preferences preferences, Context context) {
        if (context==null) return null;
        if (marketName.equals("bittrex")) {
            return new BittrexMarket(context.getString(R.string.bittrex_url),
                    preferences.getValue(context.getString(R.string.bittrex_api_key)),
                    preferences.getValue(context.getString(R.string.bittrex_secret_key)));
        }else if(marketName.equals("livecoin")) {
            return new LivecoinMarket(context.getString(R.string.livecoin_url),
                    preferences.getValue(context.getString(R.string.livecoin_api_key)),
                    preferences.getValue(context.getString(R.string.livecoin_secret_key)));
        }else {
            throw new IllegalArgumentException("Unknoqn market: "+marketName);
        }
    }
}
