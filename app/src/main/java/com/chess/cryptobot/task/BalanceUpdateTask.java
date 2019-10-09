package com.chess.cryptobot.task;

import android.os.AsyncTask;

import com.chess.cryptobot.model.Balance;

public class BalanceUpdateTask extends AsyncTask<Balance, Integer, Balance> {
    @Override
    protected Balance doInBackground(Balance[] balances) {
        return null;
    }

    @Override
    protected void onPostExecute(Balance balance) {
        super.onPostExecute(balance);
    }
}
