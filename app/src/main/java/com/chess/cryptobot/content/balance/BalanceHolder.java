package com.chess.cryptobot.content.balance;

import android.content.Context;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.exceptions.ItemNotFoundException;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.task.BalanceUpdateTask;
import com.chess.cryptobot.task.CoinImageTask;
import com.chess.cryptobot.view.AdapterActivity;

import java.util.Set;

public class BalanceHolder extends ContextHolder {

    public BalanceHolder(Context context) {
        super(context);
    }

    @Override
    public Preferences initPrefs(Context context) {
        return new BalancePreferences(context);
    }

    public void initViewItems(Set<String> coinNames) {
        coinNames.forEach(coinName -> add(new Balance(coinName)));
    }

    public void add(String coinName) {
        add(new Balance(coinName));
    }

    @Override
    public void add(ViewItem viewItem) {
        super.add(viewItem);
        BalancePreferences preferences = (BalancePreferences) getPrefs();
        Balance balance = (Balance) viewItem;
        preferences.addCoinToBalance(balance.getName());
        updateImage(balance);
        updateAmount(balance);
    }

    @Override
    public void remove(ViewItem item) {
        super.remove(item);
        removeCoinFromPrefs(item);
    }

    private void removeCoinFromPrefs(ViewItem item) {
        BalancePreferences preferences = (BalancePreferences) getPrefs();
        preferences.removeCoin(item.getName());
    }

    @Override
    public void updateItem(ViewItem item) {
        Balance balance = (Balance) item;
        updateImage(balance);
        updateAmount(balance);
    }

    private void updateImage(Balance balance) {
        AdapterActivity activity = getAdapterActivityOrNull();
        if (activity != null) {
            CoinImageTask task = new CoinImageTask(this);
            task.execute(balance);
        }
    }

    private void updateAmount(Balance balance) {
        BalanceUpdateTask task = new BalanceUpdateTask(this);
        task.execute(balance);
    }

    public Balance getBalanceByPosition(int position) throws ItemNotFoundException {
        AdapterActivity balanceActivity = getAdapterActivityOrNull();
        if (balanceActivity != null) {
            String coinName = balanceActivity.itemNameByPosition(position);
            return (Balance) this.getItemByName(coinName);
        }
        return null;
    }

    public void makeToast(String message) {
        AdapterActivity adapterActivity = this.getAdapterActivityOrNull();
        if (adapterActivity!=null) adapterActivity.makeToast(message);
    }

    public void setMinBalance(String coinName, Double minBalance) {
        BalancePreferences preferences = (BalancePreferences) getPrefs();
        preferences.setMinBalance(coinName,minBalance);
    }
}
