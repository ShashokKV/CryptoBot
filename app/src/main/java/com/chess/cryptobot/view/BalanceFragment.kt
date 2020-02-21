package com.chess.cryptobot.view

import android.animation.*
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.service.BalanceSyncService
import com.chess.cryptobot.task.CoinStatusTask
import com.chess.cryptobot.view.adapter.BalanceAdapter
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter
import com.chess.cryptobot.view.adapter.SwipeBalanceCallback
import com.chess.cryptobot.view.dialog.CryptoNameDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class BalanceFragment : MainFragment<BalanceAdapter.BalanceViewHolder>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val balanceHolder = holder as BalanceHolder
        initAddButton(view, balanceHolder)
        initSyncButton(view, balanceHolder)
        initBalanceStatus(balanceHolder)
        return view
    }

    private fun initAddButton(view: View?, balanceHolder: BalanceHolder) {
        val addBalanceButton: FloatingActionButton = Objects.requireNonNull(view)!!.findViewById(R.id.add_fab)
        addBalanceButton.setOnClickListener {
            val scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f)
            val scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f)
            val anim = ObjectAnimator.ofPropertyValuesHolder(addBalanceButton, scalex, scaley)
            anim.repeatCount = 1
            anim.repeatMode = ValueAnimator.REVERSE
            anim.duration = 300
            anim.start()
            val nameDialog = CryptoNameDialog(balanceHolder)
            val fragmentManager = fragmentManager
            if (fragmentManager != null) nameDialog.show(getFragmentManager()!!, "coinName")
        }
    }

    private fun initSyncButton(view: View?, balanceHolder: BalanceHolder) {
        val syncBalanceButton: FloatingActionButton = view!!.findViewById(R.id.sync_fab)
        syncBalanceButton.setOnClickListener {
            val angle = PropertyValuesHolder.ofFloat(View.ROTATION, 360f)
            val anim = ObjectAnimator.ofPropertyValuesHolder(syncBalanceButton, angle)
            anim.repeatCount = 1
            anim.duration = 500
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    syncBalanceButton.rotation = 0f
                }
            })
            anim.start()
            val titleView = TextView(this.context)
            titleView.setTextColor(resources.getColor(R.color.colorSecondaryDark, null))
            titleView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE)
            titleView.textSize = 20f
            titleView.gravity = Gravity.CENTER
            titleView.text = this.getString(R.string.sync_balances_title)
            val alertDialog = AlertDialog.Builder(Objects.requireNonNull(context)!!)
                    .setCustomTitle(titleView)
                    .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                        val activity = this.activity
                        if (activity != null) {
                            val intent = Intent(activity, BalanceSyncService::class.java)
                            intent.putStringArrayListExtra("coinNames",
                                    ArrayList(balanceHolder.prefs.items))
                            activity.startService(intent)
                        }
                    }
                    .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                    .create()
            alertDialog.show()
        }
    }

    private fun initBalanceStatus(balanceHolder: BalanceHolder) {
        val coinStatusTask = CoinStatusTask(balanceHolder)
        coinStatusTask.execute(0)
    }

    public override fun initFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.balance_fragment, container, false)
    }

    public override fun initHolder(): ContextHolder {
        return BalanceHolder(this).init()
    }

    public override fun initRecyclerView(view: View): RecyclerView {
        val recyclerView: RecyclerView = view.findViewById(R.id.balanceRecyclerView)
        val itemTouchHelper = ItemTouchHelper(SwipeBalanceCallback((holder as BalanceHolder)))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        return recyclerView
    }

    public override fun initAdapter(holder: ContextHolder?): RecyclerViewAdapter<BalanceAdapter.BalanceViewHolder> {
        return BalanceAdapter((holder as BalanceHolder))
    }

    public override fun initSwipeRefresh(view: View): SwipeRefreshLayout {
        return view.findViewById<View>(R.id.swipeRefreshBalance) as SwipeRefreshLayout
    }

    public override fun beforeRefresh() {}
}