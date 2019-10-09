package com.chess.cryptobot.market;

import android.content.Context;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.Preferences;

public class MarketFactory {

    public Market getMarket(String marketName, Preferences preferences, Context context) {
        if (marketName.equals("bittrex")) {
            return new BittrexMarket(context.getString(R.string.bittrex_url),
                    preferences.getStringByKey(context.getString(R.string.bittrex_api_key)),
                    preferences.getStringByKey(context.getString(R.string.bittrex_secret_key)));
        }else if(marketName.equals("livecoin")) {
            return new LivecoinMarket(context.getString(R.string.livecoin_url),
                    preferences.getStringByKey(context.getString(R.string.livecoin_api_key)),
                    preferences.getStringByKey(context.getString(R.string.livecoin_secret_key)));
        }else {
            throw new IllegalArgumentException("Unknoqn market: "+marketName);
        }
    }
}
