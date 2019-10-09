package com.chess.cryptobot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.chess.cryptobot.model.Balance;


public class BalanceUpdateService extends Service {
    private final IBinder balanceBinder = new BalanceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return balanceBinder;
    }

    public class BalanceBinder extends Binder {

        public BalanceUpdateService getService() {
            return BalanceUpdateService.this;
        }

    }
}
