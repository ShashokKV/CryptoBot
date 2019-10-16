package com.chess.cryptobot.view.adapter;

import android.view.View;

import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.view.dialog.MinBalanceDialog;

public class BalanceViewOnClickListener implements RecyclerViewOnClickListener {
    @Override
    public void onClick(View view, BalanceHolder balanceHolder, String coinName) {
        BalancePreferences balancePreferences = (BalancePreferences) balanceHolder.getPrefs();
        Double minBalance = balancePreferences.getMinBalance(coinName);
        MinBalanceDialog dialog = new MinBalanceDialog(coinName, minBalance);
        dialog.show(balanceHolder.getContext().get().getSupportFragmentManager(), "coinName");
    }
}
