package com.chess.cryptobot.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.pairs.PairsHolder;
import com.chess.cryptobot.view.adapter.PairsAdapter;
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter;

public class PairsFragment extends MainFragment {

    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.trading_pairs_fragment, container, false);
    }

    @Override
    public ContextHolder initHolder() {
        return new PairsHolder(this);
    }

    @Override
    public RecyclerView initRecyclerView(View view) {
        return view.findViewById(R.id.tradingPairsRecycleView);
    }

    @Override
    public RecyclerViewAdapter initAdapter(ContextHolder holder) {
        return new PairsAdapter((PairsHolder) holder);
    }

    @Override
    public SwipeRefreshLayout initSwipeRefresh(View view) {
        return (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshPairs);
    }

    @Override
    public void beforeRefresh() {
        PairsHolder holder = (PairsHolder) getHolder();
        holder.resetNegativePercentPairs();
        holder.updateFromBalance();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            PairsHolder pairsHolder = (PairsHolder) getHolder();
            pairsHolder.updateFromBalance();
            pairsHolder.initAvailablePairs();
        }
    }
}
