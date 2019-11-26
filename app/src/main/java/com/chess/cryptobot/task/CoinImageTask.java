package com.chess.cryptobot.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.model.Balance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
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
            Bitmap bitmap = getImage(balance.getName());
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

    private Bitmap getImage(String coinName) throws IOException {
        Bitmap bitmap;
        try {
            bitmap = loadImage(coinName);
        } catch (IOException e) {
            bitmap = downloadImage(coinName);
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

    private Bitmap downloadImage(String coinName) throws IOException {
        URL imageUrl = imageUrl(coinName);
        if (imageUrl == null) return null;
        try (InputStream inputStream = imageUrl.openStream()) {
            return BitmapFactory.decodeStream(inputStream);
        }
    }

    private URL imageUrl(String coinName) throws MalformedURLException {
        Context context = getContext();
        if (context == null) return null;
        return new URL(String.format(context.getResources().getString(R.string.crypto_icons_url), coinName.toLowerCase()));
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
