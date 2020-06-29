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
import kotlin.collections.ArrayList

class BalanceFragment : MainFragment<BalanceAdapter.BalanceViewHolder>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAddButton()
        initSyncButton()
        initBalanceStatus()
    }

    private fun initAddButton() {
        val addBalanceButton: FloatingActionButton = requireView().findViewById(R.id.add_fab)
        addBalanceButton.setOnClickListener {
            val scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f)
            val scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f)
            val anim = ObjectAnimator.ofPropertyValuesHolder(addBalanceButton, scalex, scaley)
            anim.repeatCount = 1
            anim.repeatMode = ValueAnimator.REVERSE
            anim.duration = 300
            anim.start()
            CryptoNameDialog(holder as BalanceHolder).show(parentFragmentManager, "coinName")
        }
    }

    private fun initSyncButton() {
        val syncBalanceButton: FloatingActionButton = requireView().findViewById(R.id.sync_fab)
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
            val alertDialog = AlertDialog.Builder(requireContext())
                    .setCustomTitle(titleView)
                    .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                        val activity = this.activity
                        if (activity != null) {
                            val intent = Intent(activity, BalanceSyncService::class.java)
                            intent.putStringArrayListExtra("coinNames",
                                    holder.prefs.items?.let { it1 -> ArrayList(it1) })
                            intent.putExtra("makeNotifications", true)
                            activity.startService(intent)
                        }
                    }
                    .setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                    .create()
            alertDialog.show()
        }
    }

    private fun initBalanceStatus() {
        val coinStatusTask = CoinStatusTask(holder)
        coinStatusTask.execute(0)
    }

    public override fun initFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.balance_fragment, container, false)
    }

    public override fun initHolder(): ContextHolder {
        return BalanceHolder(this)
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