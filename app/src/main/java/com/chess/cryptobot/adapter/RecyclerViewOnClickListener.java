package com.chess.cryptobot.adapter;

import android.view.View;

import com.chess.cryptobot.content.ContextHolder;

public interface RecyclerViewOnClickListener {
    void onClick(View view, ContextHolder contextHolder, String coinName);
}
