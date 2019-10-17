package com.chess.cryptobot.task;

import android.os.AsyncTask;

import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.exceptions.MarketException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.view.BalanceActivity;

import java.lang.ref.WeakReference;

public class BalanceUpdateTask extends AsyncTask<Balance, Integer, Balance> {
    private WeakReference<BalanceHolder> balanceHolderWeakReference;

    public BalanceUpdateTask(BalanceHolder balanceHolder) {
        this.balanceHolderWeakReference = new WeakReference<>(balanceHolder);
    }

    @Override
    protected Balance doInBackground(Balance[] balances) {
        Balance balance = balances[0];
        int hashCode = balance.getAmounts().hashCode();
        String[] markets = {"bittrex", "livecoin"};
        MarketFactory factory = new MarketFactory();
        BalanceHolder balanceHolder = this.balanceHolderWeakReference.get();
        for (String marketName : markets) {
            Market market = factory.getMarket(marketName, balanceHolder.getPrefs(), balanceHolder.getContext().get());
            try {
                balance.setAmount(marketName, market.getAmount(balance.getCoinName()));
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
        balanceHolderWeakReference.get().updateBalance(balance);
    }

    @Override
    protected void onCancelled(Balance balance) {
        BalanceActivity activity =  balanceHolderWeakReference.get().getBalanceActivityOrNull();
        if (activity!=null) activity.makeToast(balance.getMessage());
    }
}
