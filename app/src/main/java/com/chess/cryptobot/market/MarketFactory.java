package com.chess.cryptobot.market;

import android.content.Context;
import android.content.SharedPreferences;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;

import java.util.ArrayList;
import java.util.List;

public class MarketFactory {

    public List<Market> getMarkets(ContextHolder contextHolder) {
        return getMarkets(contextHolder.getContext(), contextHolder.getPrefs().getSharedPreferences());
    }

    public List<Market> getMarkets(Context context, SharedPreferences preferences) {
        String[] marketNames = {"bittrex", "livecoin"};
        List<Market> markets = new ArrayList<>();

        for (String marketName : marketNames) {
            markets.add(getMarket(marketName, preferences, context));
        }
        return markets;
    }

    private Market getMarket(String marketName, SharedPreferences preferences, Context context) {
        if (context == null) return null;
        if (marketName.equals("bittrex")) {
            return new BittrexMarket(context.getString(R.string.bittrex_url),
                    preferences.getString(context.getString(R.string.bittrex_api_key), null),
                    preferences.getString(context.getString(R.string.bittrex_secret_key), null));
        } else if (marketName.equals("livecoin")) {
            return new LivecoinMarket(context.getString(R.string.livecoin_url),
                    preferences.getString(context.getString(R.string.livecoin_api_key), null),
                    preferences.getString(context.getString(R.string.livecoin_secret_key), null));
        } else {
            throw new IllegalArgumentException("Unknoqn market: " + marketName);
        }
    }
}
