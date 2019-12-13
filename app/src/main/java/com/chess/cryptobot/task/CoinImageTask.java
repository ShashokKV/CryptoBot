package com.chess.cryptobot.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.model.Balance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

public class CoinImageTask extends AsyncTask<Balance, Integer, Balance> {
    private final WeakReference<BalanceHolder> balanceHolderWeakReference;
    private final static String TAG = "CoinImageTask";

    public CoinImageTask(BalanceHolder balanceHolder) {
        this.balanceHolderWeakReference = new WeakReference<>(balanceHolder);
    }

    @Override
    protected Balance doInBackground(Balance... balances) {
        boolean updated = false;
        Balance balance = balances[0];
        try {
            Bitmap bitmap = getImage(balance);
            balance.setCoinIcon(bitmap);
            updated = true;
        } catch (IOException ignored) {
        }

        if (!updated) {
            cancel(true);
            return null;
        }
        return balance;
    }

    @Override
    protected void onPostExecute(@Nullable Balance balance) {
        if (balance == null) return;
        BalanceHolder balanceHolder = balanceHolderWeakReference.get();
        if (balanceHolder != null) balanceHolder.setItem(balance);
    }

    private Bitmap getImage(Balance balance) throws IOException {
        Bitmap bitmap;
        String coinName = balance.getName();
        try {
            bitmap = loadImage(coinName);
        } catch (IOException e) {
            bitmap = downloadImage(balance.getCoinUrl());
            saveImage(bitmap, fileName(coinName));
        }

        return bitmap;
    }

    private Bitmap loadImage(String coinName) throws IOException {
        Context context = getContext();
        if (context == null) return null;
        try (FileInputStream fileInputStream = context.openFileInput(fileName(coinName))) {
            return BitmapFactory.decodeStream(fileInputStream);
        }
    }

    private String fileName(String coinName) {
        return String.format("%s.png", coinName.toLowerCase());
    }

    private void saveImage(Bitmap bitmap, String fileName) {
        Context context = getContext();
        if (context == null || bitmap == null) return;
        try (FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    private Bitmap downloadImage(String coinUrl) throws IOException {
        URL imageUrl = new URL(coinUrl);
        try (InputStream inputStream = imageUrl.openStream()) {
            return BitmapFactory.decodeStream(inputStream);
        }
    }

    private Context getContext() {
        BalanceHolder balanceHolder = balanceHolderWeakReference.get();
        if (balanceHolder != null) {
            return balanceHolder.getContext();
        } else {
            cancel(true);
            return null;
        }
    }
}
