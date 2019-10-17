package com.chess.cryptobot.content;

import android.content.Context;

import java.lang.ref.WeakReference;

public abstract class ContextHolder {
    private WeakReference<Context> context;

    public ContextHolder(Context context) {
        this.context = new WeakReference<>(context);
    }

    public WeakReference<Context> getContext() {
        return this.context;
    }

    public abstract Preferences getPrefs();
}