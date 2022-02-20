package com.chess.cryptobot.view.adapter

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.exceptions.ItemNotFoundException
import com.chess.cryptobot.model.Balance
import com.chess.cryptobot.view.MainActivity

class SwipeBalanceCallback(
    private val balanceHolder: BalanceHolder,
    private val activity: MainActivity
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val tag = SwipeBalanceCallback::class.qualifiedName

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val balance: Balance = try {
            balanceHolder.getBalanceByPosition(viewHolder.adapterPosition)
        } catch (e: ItemNotFoundException) {
            Log.e(tag, Log.getStackTraceString(e))
            return
        }
        balanceHolder.remove(balance)
        activity.updateBot()
    }

}