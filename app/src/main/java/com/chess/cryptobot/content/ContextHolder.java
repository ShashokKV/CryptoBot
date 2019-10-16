package com.chess.cryptobot.content;

import android.content.Context;

import com.chess.cryptobot.view.BalanceActivity;

import java.lang.ref.WeakReference;

public abstract class ContextHolder {
    private WeakReference<BalanceActivity> context;

    public ContextHolder(Context context) {
        this.context = new WeakReference<>((BalanceActivity)context);
    }

    public WeakReference<BalanceActivity> getContext() {
        return this.context;
    }

    public abstract Preferences getPrefs();
}
