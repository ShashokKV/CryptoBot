package com.chess.cryptobot.model;

import android.content.Context;

import com.chess.cryptobot.activity.BalanceActivity;
import com.chess.cryptobot.adapter.BalanceAdapter;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.task.CoinImageTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BalanceHolder {
    private ArrayList<Balance> balances;
    private BalanceAdapter balanceAdapter;
    private Preferences prefs;
    private WeakReference<Context> context;

    public BalanceHolder(BalanceActivity context) {
        this.context = new WeakReference<>(context);
        prefs = new Preferences(context);
        balances = new ArrayList<>();

        Set<String> coinNames = prefs.getCoinNames();
        coinNames.forEach(coinName -> balances.add(new Balance(coinName)));

        balanceAdapter = new BalanceAdapter(this);
        updateImage(balances);
        updateAmount();
    }

    public void add (String coinName) {
        add(new Balance(coinName));
    }

    private void add(Balance balance) {
        balances.add(balance);
        prefs.addCoinToBalance(balance.getCoinName());
        updateImage(balance);
        updateAmount();
    }

    public void remove(int position) {
        String coinName = balanceAdapter.coinNameByPosition(position);
        prefs.removeCoinFromBalance(coinName);
        balanceAdapter.deleteItem(position);
        balances.remove(coinName);
    }

    private void updateImage(ArrayList<Balance> balances) {
        balances.forEach(this::updateImage);
    }

    private void updateImage(Balance balance) {
        CoinImageTask task = new CoinImageTask(this);
        task.execute(balance);
    }

    public synchronized void updateView(Balance balance) {
        balanceAdapter.updateAdapter(balance);
    }

    private void updateAmount() {
        //TODO
    }

    public WeakReference<Context> getContext() {
        return this.context;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public BalanceAdapter getBalanceAdapter() {
        return balanceAdapter;
    }

}
