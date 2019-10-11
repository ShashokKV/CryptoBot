package com.chess.cryptobot.content;

import android.content.Context;
import android.widget.Toast;

import com.chess.cryptobot.activity.BalanceActivity;
import com.chess.cryptobot.adapter.BalanceAdapter;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.task.BalanceUpdateTask;
import com.chess.cryptobot.task.CoinImageTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContextHolder {
    private ArrayList<Balance> balances;
    private Preferences prefs;
    private WeakReference<BalanceActivity> context;

    public ContextHolder(Context context) {
        this.context = new WeakReference<>((BalanceActivity)context);
        prefs = new Preferences(context);
        balances = new ArrayList<>();

        Set<String> coinNames = prefs.getCoinNames();
        coinNames.forEach(coinName -> balances.add(new Balance(coinName)));
        updateImage(balances);
        updateAmount(balances);
    }

    public void add (String coinName) {
        add(new Balance(coinName));
    }

    private void add(Balance balance) {
        if (balances.contains(balance)) return;
        balances.add(balance);
        prefs.addCoinToBalance(balance.getCoinName());
        balanceAdapter().addItem(balance);
        updateImage(balance);
        updateAmount(balance);
    }

    public void remove(int position) {
        if (position>=balances.size()) return;
        String coinName = balanceAdapter().coinNameByPosition(position);
        prefs.removeCoinFromBalance(coinName);
        balanceAdapter().deleteItem(position);
        balances.remove(position);
    }

    private void updateImage(ArrayList<Balance> balances) {
        balances.forEach(this::updateImage);
    }

    private void updateImage(Balance balance) {
        CoinImageTask task = new CoinImageTask(this);
        task.execute(balance);
    }

    private void updateAmount(ArrayList<Balance> balances) {
        balances.forEach(this::updateAmount);
    }

    private void updateAmount(Balance balance) {
        BalanceUpdateTask task = new BalanceUpdateTask(this);
        task.execute(balance);
    }

    public synchronized void updateView(Balance balance) {
        balanceAdapter().updateItem(balance);
    }

    public void makeToast(String message) {
        if (message!=null && !message.isEmpty()) {
            Toast.makeText(context.get(), message, Toast.LENGTH_LONG).show();
        }
    }

    private BalanceAdapter balanceAdapter() {
        return context.get().getBalanceAdapter();
    }

    public WeakReference<BalanceActivity> getContext() {
        return this.context;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public Preferences getPrefs() {return prefs;}
}
