package com.chess.cryptobot.content.balance;

import android.content.Context;
import android.widget.Toast;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.task.BalanceUpdateTask;
import com.chess.cryptobot.task.CoinImageTask;
import com.chess.cryptobot.view.adapter.BalanceAdapter;

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

    private void updateAmount(Balance balance) {
        BalanceUpdateTask task = new BalanceUpdateTask(this);
        task.execute(balance);
    }

    public synchronized void updateBalanceInView(Balance balance) {
        balanceAdapter().updateItem(balance);
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

    public void updateAllInView() {
        updateImage(balances);
        updateAmount(balances);
    }

    public void makeToast(String message) {
        if (message!=null && !message.isEmpty()) {
            Toast.makeText(getContext().get(), message, Toast.LENGTH_LONG).show();
        }
    }

    private BalanceAdapter balanceAdapter() {
        return getContext().get().getBalanceAdapter();
    }

    public List<Balance> getBalances() {
        return balances;
    }
}
