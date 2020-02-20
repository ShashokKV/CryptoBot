package com.chess.cryptobot.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.history.HistoryHolder
import com.chess.cryptobot.view.adapter.HistoryAdapter
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter

class HistoryFragment(private var state: HistoryHolder.State? = HistoryHolder.State.HISTORY) : MainFragment<HistoryAdapter.HistoryViewHolder>() {

    override fun beforeRefresh() {
    }

    override fun initFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun initHolder(): ContextHolder {
        return HistoryHolder(this, state as HistoryHolder.State)
    }

    override fun initRecyclerView(view: View): RecyclerView {
        return view.findViewById(R.id.historyRecyclerView)
    }

    override fun initAdapter(holder: ContextHolder?): RecyclerViewAdapter<HistoryAdapter.HistoryViewHolder> {
        return HistoryAdapter(holder as HistoryHolder, state as HistoryHolder.State)
    }

    override fun initSwipeRefresh(view: View): SwipeRefreshLayout {
        return view.findViewById(R.id.swipeRefreshHistory) as SwipeRefreshLayout
    }
}
