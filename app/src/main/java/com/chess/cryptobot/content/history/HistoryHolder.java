package com.chess.cryptobot.content.history;

import android.content.Context;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.task.HistoryTask;

import java.util.Set;

public class HistoryHolder extends ContextHolder {
    private final State state;

    public HistoryHolder(Fragment fragment, State state) {
        super(fragment);
        this.state = state;
        updateAllItems();
    }

    @Override
    protected Preferences initPrefs(Context context) {
        return new HistoryPreferences(context);
    }

    @Override
    protected void initViewItems(Set<String> itemNamesSet) {

    }

    @Override
    public void updateAllItems() {
        initFields();
        HistoryTask task = new HistoryTask(this, state);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
    }

    @Override
    protected void updateItem(ViewItem item) {

    }

    public enum State {
        HISTORY,
        ORDERS
    }
}
