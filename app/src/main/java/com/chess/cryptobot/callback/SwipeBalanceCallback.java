package com.chess.cryptobot.callback;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.chess.cryptobot.adapter.BalanceAdapter;
import com.chess.cryptobot.content.Preferences;

public class SwipeBalanceCallback extends ItemTouchHelper.SimpleCallback {
    private BalanceAdapter balanceAdapter;
    private Preferences preferences;

    public SwipeBalanceCallback(BalanceAdapter adapter, Preferences preferences) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        balanceAdapter = adapter;
        this.preferences = preferences;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        preferences.removeCoinFromBalance(balanceAdapter.coinNameByPosition(position));
        balanceAdapter.deleteItem(position);
    }
}
