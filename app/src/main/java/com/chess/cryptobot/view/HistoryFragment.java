package com.chess.cryptobot.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.history.HistoryHolder;
import com.chess.cryptobot.view.adapter.HistoryAdapter;
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter;

public class HistoryFragment extends MainFragment {
    private final HistoryHolder.State state;

    public HistoryFragment() {
        this(HistoryHolder.State.HISTORY);
    }

    HistoryFragment(HistoryHolder.State state) {
        super();
        this.state = state;
    }

    @Override
    protected View initFragmentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    @Override
    protected ContextHolder initHolder() {
        return new HistoryHolder(this, state);
    }

    @Override
    protected RecyclerView initRecyclerView(View view) {
        return view.findViewById(R.id.historyRecyclerView);
    }

    @Override
    protected RecyclerViewAdapter initAdapter(ContextHolder holder) {
        return new HistoryAdapter(holder, state);
    }

    @Override
    protected SwipeRefreshLayout initSwipeRefresh(View view) {
        return (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshHistory);
    }

    @Override
    protected void beforeRefresh() {

    }
}
