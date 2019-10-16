package com.chess.cryptobot.view.adapter;

import android.view.View;

import com.chess.cryptobot.content.balance.BalanceHolder;

public interface RecyclerViewOnClickListener {
    void onClick(View view, BalanceHolder balanceHolder, String coinName);
}
