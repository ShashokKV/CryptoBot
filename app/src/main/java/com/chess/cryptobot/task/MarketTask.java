package com.chess.cryptobot.task;

import android.os.AsyncTask;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;

import java.lang.ref.WeakReference;
import java.util.List;

abstract class MarketTask<S, T> extends AsyncTask<S, Integer, T> {
    private final WeakReference<ContextHolder> holderWeakReference;

    MarketTask(ContextHolder holder) {
        this.holderWeakReference = new WeakReference<>(holder);
    }

    @SafeVarargs
    @Override
    protected final T doInBackground(S... params) {
        S param = params[0];
        T result = null;
        ContextHolder holder = getHolder();
        publishProgress();
        preMarketProcess(param);
        MarketFactory factory = new MarketFactory();
        List<Market> markets = factory.getMarkets(holder);
        for (Market market : markets) {
            try {
                if (market == null) {
                    cancel(true);
                    return null;
                }
                result = marketProcess(market, param);
            } catch (MarketException e) {
                cancel(true);
                return exceptionProcess(param, e.getMessage());
            }
        }

        return postMarketProcess(result);
    }

    protected abstract void preMarketProcess(S param);

    protected abstract T marketProcess(Market market, S param) throws MarketException;

    protected abstract T postMarketProcess(T result);

    protected abstract T exceptionProcess(S param, String exceptionMessage);

    @Override
    protected void onPostExecute(T result) {
        ContextHolder holder = getHolder();
        if (holder == null) return;
        holder.hideSpinner();
        if (result == null) return;
        doInPostExecute(result, holder);
    }

    protected abstract void doInPostExecute(T result, ContextHolder holder);

    @Override
    protected void onCancelled(T result) {
        ContextHolder holder = getHolder();
        if (holder != null) {
            holder.hideSpinner();
        }
        if (result != null) {
            doInOnCanceled(result, holder);
        }
    }

    protected abstract void doInOnCanceled(T result, ContextHolder holder);

    @Override
    protected void onProgressUpdate(Integer... values) {
        ContextHolder holder = getHolder();
        if (holder != null) {
            holder.showSpinner();
        }
    }

    ContextHolder getHolder() {
        ContextHolder holder = holderWeakReference.get();
        if (holder == null) {
            cancel(true);
            return null;
        }
        return holder;
    }

}
