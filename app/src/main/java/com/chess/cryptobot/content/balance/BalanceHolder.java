package com.chess.cryptobot.content.balance;

import android.content.Context;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.task.BalanceUpdateTask;
import com.chess.cryptobot.task.CoinImageTask;
import com.chess.cryptobot.view.BalanceActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BalanceHolder extends ContextHolder {
    private ArrayList<Balance> balances;
    private BalancePreferences prefs;

    public BalanceHolder(Context context) {
        super(context);

        this.prefs = new BalancePreferences(context);
        balances = new ArrayList<>();
        Set<String> coinNames = prefs.getCoinNames();
        coinNames.forEach(coinName -> balances.add(new Balance(coinName)));
    }

    @Override
    public Preferences getPrefs() {
        return this.prefs;
    }

    public void add(String coinName) {
        add(new Balance(coinName));
    }

    private void add(Balance balance) {
        if (balances.contains(balance)) return;
        balances.add(balance);
        prefs.addCoinToBalance(balance.getCoinName());
        BalanceActivity balanceActivity = getBalanceActivityOrNull();
        if (balanceActivity!=null) {
            balanceActivity.addBalance(balance);
            updateImage(balance);
            updateAmount(balance);
        }
    }

    public void remove(int position) {
        if (position >= balances.size()) return;
        BalanceActivity balanceActivity = getBalanceActivityOrNull();
        if (balanceActivity!=null) {
            String coinName = balanceActivity.coinNameByPosition(position);
            prefs.removeCoinFromBalance(coinName);
            balanceActivity.deleteBalanceByPosition(position);
            balances.remove(position);
        }
    }

    public void updateAll() {
        updateImage(balances);
        updateAmount(balances);
    }

    private void updateImage(ArrayList<Balance> balances) {
        balances.forEach(this::updateImage);
    }

    private void updateImage(Balance balance) {
        BalanceActivity  balanceActivity = getBalanceActivityOrNull();
        if (balanceActivity != null) {
            CoinImageTask task = new CoinImageTask(this);
            task.execute(balance);
        }
    }

    private void updateAmount(ArrayList<Balance> balances) {
        balances.forEach(this::updateAmount);
    }

    private void updateAmount(Balance balance) {
        BalanceUpdateTask task = new BalanceUpdateTask(this);
        task.execute(balance);
    }

    public void updateBalance(Balance updatedBalance) {
        for(Balance balance: balances) {
            if (balance.equals(updatedBalance)) {
                balances.set(balances.indexOf(balance), updatedBalance);
                break;
            }
        }

        BalanceActivity activity =  getBalanceActivityOrNull();
        if (activity!=null) activity.updateBalance(updatedBalance);
    }

    public void setMinBalance(String coinName, Double minBalance) {
        prefs.setMinBalance(coinName,minBalance);
    }

    public BalanceActivity getBalanceActivityOrNull() {
        Context context = this.getContext().get();
        if (context instanceof BalanceActivity) {
            return (BalanceActivity) context;
        }
        return null;
    }

    public List<Balance> getBalances() {
        return balances;
    }
}
