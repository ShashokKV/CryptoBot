package com.chess.cryptobot.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.pairs.PairsHolder
import com.chess.cryptobot.view.adapter.PairsAdapter
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter

class PairsFragment : MainFragment<PairsAdapter.PairsViewHolder>() {
    public override fun initFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.trading_pairs_fragment, container, false)
    }

    public override fun initHolder(): ContextHolder {
        return PairsHolder(this)
    }

    public override fun initRecyclerView(view: View): RecyclerView {
        val recyclerView: RecyclerView = view.findViewById(R.id.tradingPairsRecycleView)
        val itemAnimator = recyclerView.itemAnimator as SimpleItemAnimator?
        if (itemAnimator != null) {
            itemAnimator.removeDuration = 0
            itemAnimator.supportsChangeAnimations = false
        }
        return recyclerView
    }

    public override fun initAdapter(holder: ContextHolder?): RecyclerViewAdapter<PairsAdapter.PairsViewHolder> {
        return PairsAdapter(holder!!)
    }

    public override fun initSwipeRefresh(view: View): SwipeRefreshLayout {
        return view.findViewById<View>(R.id.swipeRefreshPairs) as SwipeRefreshLayout
    }

    public override fun beforeRefresh() {
        val holder = holder as PairsHolder
        holder.resetNegativePercentPairs()
        holder.updateFromBalance()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            val pairsHolder = holder as PairsHolder
            pairsHolder.updateFromBalance()
            if (pairsHolder.hasAvailablePairs) {
                pairsHolder.updateAllItems()
            } else {
                pairsHolder.initAvailablePairs()
            }
        }
    }
}