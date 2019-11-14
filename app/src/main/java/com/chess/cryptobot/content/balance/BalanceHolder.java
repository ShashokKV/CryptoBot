package com.chess.cryptobot.content.balance;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.exceptions.ItemNotFoundException;
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.market.MarketFactory;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.task.BalanceUpdateTask;
import com.chess.cryptobot.task.CoinImageTask;
import com.chess.cryptobot.view.MainFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BalanceHolder extends ContextHolder {
    private boolean hasKeys;
    private Map<String, Boolean> bittrexStatuses;
    private Map<String, Boolean> livecoinStatuses;

    public BalanceHolder(Fragment fragment) {
        super(fragment);
        bittrexStatuses = new HashMap<>();
        livecoinStatuses = new HashMap<>();
        checkIfHasKeys();
    }

    private void checkIfHasKeys() {
        hasKeys = true;
        MarketFactory factory = new MarketFactory();
        for (Market market: factory.getMarkets(this)) {
            if (market.keysIsEmpty()) {
                hasKeys = false;
                break;
            }
        }
    }

    @Override
    public Preferences initPrefs(Context context) {
        return new BalancePreferences(context);
    }

    public void initViewItems(Set<String> coinNames) {
        coinNames.forEach(coinName -> addItemToList(new Balance(coinName)));
    }

    public void add(String coinName) {
        Balance balance = new Balance(coinName);
        if (!getViewItems().contains(balance))
            add(new Balance(coinName));
    }

    @Override
    public void add(ViewItem viewItem) {
        super.add(viewItem);
        Balance balance = (Balance) viewItem;
        balance.setStatuses(livecoinStatuses.get(balance.getName()), bittrexStatuses.get(balance.getName()));
        updateImage(balance);
        if (hasKeys) updateAmount(balance);
        getMainActivity().updateBot();
    }

    @Override
    public synchronized void remove(ViewItem item) {
        super.remove(item);
        getMainActivity().updateBot();
    }

    @Override
    public void updateItem(ViewItem item) {
        Balance balance = (Balance) item;
        balance.setStatuses(livecoinStatuses.get(balance.getName()), bittrexStatuses.get(balance.getName()));
        updateImage(balance);
        if (hasKeys) updateAmount(balance);
    }

    private void updateImage(Balance balance) {
        MainFragment fragment = getMainFragment();
        if (fragment != null) {
            CoinImageTask task = new CoinImageTask(this);
            task.execute(balance);
        }
    }

    private void updateAmount(Balance balance) {
        BalanceUpdateTask task = new BalanceUpdateTask(this);
        task.execute(balance);
    }

    public Balance getBalanceByPosition(int position) throws ItemNotFoundException {
        MainFragment balanceActivity = getMainFragment();
        if (balanceActivity != null) {
            String coinName = balanceActivity.itemNameByPosition(position);
            return (Balance) this.getItemByName(coinName);
        }
        return null;
    }

    public void setMinBalance(String coinName, Double minBalance) {
        BalancePreferences preferences = (BalancePreferences) getPrefs();
        preferences.setMinBalance(coinName,minBalance);
    }

    public void setCurrencyStatus(Map<String, Boolean> bittrexStatuses, Map<String, Boolean> livecoinStatuses) {
        this.bittrexStatuses = bittrexStatuses;
        this.livecoinStatuses = livecoinStatuses;
    }
}
