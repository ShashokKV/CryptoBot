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
        MarketFactory factory = new MarketFactory();
        List<Market> markets = factory.getMarkets(balanceHolderWeakReference.get());
        for (Market market: markets) {
            try {
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
        if (balance==null) return;
        balanceHolderWeakReference.get().setItem(balance);
    }

    @Override
    protected void onCancelled(Balance balance) {
        balanceHolderWeakReference.get().makeToast(balance.getMessage());
    }
}
