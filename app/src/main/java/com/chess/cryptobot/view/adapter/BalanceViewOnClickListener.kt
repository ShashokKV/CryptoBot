package com.chess.cryptobot.view.adapter

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.view.dialog.MinBalanceDialog

class BalanceViewOnClickListener internal constructor(private val balanceHolder: BalanceHolder) : RecyclerViewOnClickListener {
    override fun onClick(view: View?, coinName: String?) {
        val balancePreferences = balanceHolder.prefs as BalancePreferences
        val minBalance = balancePreferences.getMinBalance(coinName!!)
        val dialog = MinBalanceDialog(balanceHolder, coinName, minBalance)
        val activity = balanceHolder.context as AppCompatActivity?
        dialog.show(activity!!.supportFragmentManager, "coinName")
    }

}