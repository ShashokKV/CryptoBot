package com.chess.cryptobot.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chess.cryptobot.R;
import com.chess.cryptobot.adapter.BalanceAdapter;
import com.chess.cryptobot.model.Balance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

public class CoinImageTask extends AsyncTask<Balance, Integer, Balance[]> {
    private BalanceAdapter balanceAdapter;
    private WeakReference<Context> context;
    private final static String TAG = "CoinImageTask";

    public CoinImageTask(BalanceAdapter balanceAdapter, Context context) {
        this.balanceAdapter = balanceAdapter;
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Balance[] doInBackground(Balance... balances) {
        boolean updated = false;
        for (Balance balance : balances) {
            try{
                Bitmap bitmap = getImage(balance.getCoinName());
                balance.setCoinIcon(bitmap);
                updated = true;
            }catch (IOException ignored){}
        }

        if (!updated) {
            cancel(true);
            return null;
        }
        return balances;
    }

    @Override
    protected void onPostExecute(@Nullable Balance[] balance) {
        if (balance==null) return;
        balanceAdapter.updateAdapter(balance);
    }

    private Bitmap getImage(String coinName) throws IOException {
        Bitmap bitmap;
        try{
            bitmap = loadImage(coinName);
        }catch (IOException e) {
            bitmap = downloadImage(coinName);
            saveImage(bitmap, fileName(coinName));
        }

        return bitmap;
    }

    private Bitmap loadImage(String coinName) throws IOException {
        try(FileInputStream fileInputStream = context.get().openFileInput(fileName(coinName))) {
            return BitmapFactory.decodeStream(fileInputStream);
        }
    }

    private String fileName(String coinName) {
        return String.format("%s.png", coinName.toLowerCase());
    }

    private void saveImage(Bitmap bitmap, String fileName) {
        try(FileOutputStream out = context.get().openFileOutput(fileName, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    private Bitmap downloadImage(String coinName) throws IOException {
        try(InputStream inputStream = imageUrl(coinName).openStream()) {
            return BitmapFactory.decodeStream(inputStream);
        }
    }

    private URL imageUrl(String coinName) throws MalformedURLException {
        return new URL(String.format(context.get().getResources().getString(R.string.crypto_icons_url), coinName.toLowerCase()));
    }
}
