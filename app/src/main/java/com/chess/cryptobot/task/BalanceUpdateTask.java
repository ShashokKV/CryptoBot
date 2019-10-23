package com.chess.cryptobot.task;

import android.os.AsyncTask;

import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Balance;

import java.lang.ref.WeakReference;
import java.util.List;

public class BalanceUpdateTask extends AsyncTask<Balance, Integer, Balance> {
    private WeakReference<BalanceHolder> balanceHolderWeakReference;

    public BalanceUpdateTask(BalanceHolder balanceHolder) {
        this.balanceHolderWeakReference = new WeakReference<>(balanceHolder);
    }

    @Override
    protected Balance doInBackground(Balance[] balances) {
        Balance balance = balances[0];
        int hashCode = balance.getAmounts().hashCode();
        BalanceHolder balanceHolder = balanceHolderWeakReference.get();
        if (balanceHolder==null) {
            cancel(true);
            return null;
        }
        publishProgress();
        MarketFactory factory = new MarketFactory();
        List<Market> markets = factory.getMarkets(balanceHolder);
        for (Market market: markets) {
            try {
                if (market==null) {
                    cancel(true);
                    return null;
                }
                balance.setAmount(market.getMarketName(), market.getAmount(balance.getName()));
            } catch (MarketException e) {
                balance.setMessage(e.getMessage());
                cancel(true);
                return balance;
            }
        }

        try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
        if (hashCode==balance.getAmounts().hashCode()) return null;
        return balance;
    }

    @Override
    protected void onPostExecute(Balance balance) {
        BalanceHolder balanceHolder = balanceHolderWeakReference.get();
        if (balanceHolder==null) return;
        balanceHolder.hideSpinner();
        if (balance==null) return;
        balanceHolder.setItem(balance);
    }

    @Override
    protected void onCancelled(Balance balance) {
        BalanceHolder balanceHolder = balanceHolderWeakReference.get();
        if (balanceHolder!=null && balance!=null) {
            balanceHolder.hideSpinner();
            balanceHolder.makeToast(balance.getMessage());
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        BalanceHolder balanceHolder = balanceHolderWeakReference.get();
        if (balanceHolder!=null) {
            balanceHolder.showSpinner();
        }
    }
}
