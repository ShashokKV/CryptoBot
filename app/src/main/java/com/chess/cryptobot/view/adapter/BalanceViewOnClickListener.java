package com.chess.cryptobot.view.adapter;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.view.dialog.MinBalanceDialog;

public class BalanceViewOnClickListener implements RecyclerViewOnClickListener {
    private BalanceHolder balanceHolder;

    BalanceViewOnClickListener(BalanceHolder balanceHolder) {
        this.balanceHolder = balanceHolder;
    }

    @Override
    public void onClick(View view, String coinName) {
        BalancePreferences balancePreferences = (BalancePreferences) balanceHolder.getPrefs();
        Double minBalance = balancePreferences.getMinBalance(coinName);
        MinBalanceDialog dialog = new MinBalanceDialog(balanceHolder, coinName, minBalance);
        AppCompatActivity activity = (AppCompatActivity) balanceHolder.getContext();
        dialog.show(activity.getSupportFragmentManager(), "coinName");
    }
}
