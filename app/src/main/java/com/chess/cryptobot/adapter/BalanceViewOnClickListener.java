package com.chess.cryptobot.adapter;

import android.view.View;

import com.chess.cryptobot.activity.dialog.MinBalanceDialog;
import com.chess.cryptobot.content.ContextHolder;

public class BalanceViewOnClickListener implements RecyclerViewOnClickListener {
    @Override
    public void onClick(View view, ContextHolder contextHolder, String coinName) {
        Double minBalance = contextHolder.getPrefs().getMinBalance(coinName);
        MinBalanceDialog dialog = new MinBalanceDialog(coinName, minBalance);
        dialog.show(contextHolder.getContext().get().getSupportFragmentManager(), "coinName");
    }
}
