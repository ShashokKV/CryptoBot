package com.chess.cryptobot.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.model.Balance
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL
import java.util.*

class CoinImageTask(balanceHolder: BalanceHolder) : AsyncTask<Balance, Int?, Balance?>() {

    private val balanceHolderWeakReference: WeakReference<BalanceHolder> = WeakReference(balanceHolder)

    override fun doInBackground(vararg params: Balance): Balance? {
        var updated = false
        val balance = params[0]
        try {
            val bitmap = getImage(balance)
            balance.coinIcon = bitmap
            updated = true
        } catch (ignored: IOException) {
        }
        if (!updated) {
            cancel(true)
            return null
        }
        return balance
    }

    override fun onPostExecute(balance: Balance?) {
        if (balance == null) return
        val balanceHolder = balanceHolderWeakReference.get()
        balanceHolder?.setItem(balance)
    }

    @Throws(IOException::class)
    private fun getImage(balance: Balance): Bitmap? {
        var bitmap: Bitmap?
        val coinName = balance.name
        try {
            bitmap = loadImage(coinName)
        } catch (e: IOException) {
            bitmap = downloadImage(balance.coinUrl)
            saveImage(bitmap, fileName(coinName))
        }
        return bitmap
    }

    @Throws(IOException::class)
    private fun loadImage(coinName: String): Bitmap? {
        val context = context ?: return null
        context.openFileInput(fileName(coinName)).use { fileInputStream -> return BitmapFactory.decodeStream(fileInputStream) }
    }

    private fun fileName(coinName: String): String {
        return String.format("%s.png", coinName.toLowerCase(Locale.getDefault()))
    }

    private fun saveImage(bitmap: Bitmap?, fileName: String) {
        val context = context
        if (context == null || bitmap == null) return
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        } catch (e: IOException) {
            Log.d(TAG, e.localizedMessage)
        }
    }

    @Throws(IOException::class)
    private fun downloadImage(coinUrl: String?): Bitmap {
        val imageUrl = URL(coinUrl)
        imageUrl.openStream().use { inputStream -> return BitmapFactory.decodeStream(inputStream) }
    }

    private val context: Context?
        get() {
            val balanceHolder = balanceHolderWeakReference.get()
            return if (balanceHolder != null) {
                balanceHolder.context
            } else {
                cancel(true)
                null
            }
        }

    companion object {
        private const val TAG = "CoinImageTask"
    }

}