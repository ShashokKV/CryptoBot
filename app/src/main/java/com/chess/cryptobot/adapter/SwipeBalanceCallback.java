package com.chess.cryptobot.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.content.ContextHolder;

public class SwipeBalanceCallback extends ItemTouchHelper.SimpleCallback {
    private ContextHolder contextHolder;

    public SwipeBalanceCallback(ContextHolder contextHolder) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.contextHolder = contextHolder;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        contextHolder.remove(viewHolder.getAdapterPosition());
    }
}