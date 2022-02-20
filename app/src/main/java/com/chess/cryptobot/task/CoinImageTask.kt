package com.chess.cryptobot.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.model.Balance
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class CoinImageTask(private val balanceHolder: BalanceHolder) {
    private val executor = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(SupervisorJob() + executor.asCoroutineDispatcher())
    private val tag = CoinImageTask::class.qualifiedName

    fun doInBackground(vararg params: Balance) {
        val balance = params[0]

        try {
            scope.launch(IO) {
                val bitmap = getImage(balance)
                balance.coinIcon = bitmap
                withContext(Dispatchers.Main) {
                    onPostExecute(balance)
                }
            }
        } catch (e: IOException) {
            Log.e(tag, Log.getStackTraceString(e))
        }

    }

    private fun onPostExecute(balance: Balance?) {
        if (balance == null) return
        balanceHolder.setItem(balance)
    }

    @Throws(IOException::class)
    private fun getImage(balance: Balance): Bitmap? {
        var bitmap: Bitmap?
        val coinName = balance.name
        try {
            bitmap = loadImage(coinName)
        } catch (e: IOException) {
            if (balance.coinUrl == null) return null
            bitmap = downloadImage(balance.coinUrl)
            saveImage(bitmap, fileName(coinName))
        }
        return bitmap
    }

    @Throws(IOException::class)
    private fun loadImage(coinName: String): Bitmap? {
        context.openFileInput(fileName(coinName))
            .use { fileInputStream -> return BitmapFactory.decodeStream(fileInputStream) }
    }

    private fun fileName(coinName: String): String {
        return String.format("%s.png", coinName.lowercase(Locale.getDefault()))
    }

    private fun saveImage(bitmap: Bitmap?, fileName: String) {
        if (bitmap == null) return
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE)
                .use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        } catch (e: IOException) {
            Log.e(tag, Log.getStackTraceString(e))
        }
    }

    @Throws(IOException::class)
    private fun downloadImage(coinUrl: String?): Bitmap {
        val imageUrl = URL(coinUrl)
        imageUrl.openStream().use { inputStream -> return BitmapFactory.decodeStream(inputStream) }
    }

    private val context: Context
        get() {
            return balanceHolder.context
        }
}