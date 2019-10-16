package com.chess.cryptobot.view.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.content.balance.BalanceHolder;

public class SwipeBalanceCallback extends ItemTouchHelper.SimpleCallback {
    private BalanceHolder balanceHolder;

    public SwipeBalanceCallback(BalanceHolder balanceHolder) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.balanceHolder = balanceHolder;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        balanceHolder.remove(viewHolder.getAdapterPosition());
    }
}