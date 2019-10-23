package com.chess.cryptobot.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.balance.BalancePreferences;
import com.chess.cryptobot.content.pairs.TradingPairsHolder;
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter;
import com.chess.cryptobot.view.adapter.TradingPairsAdapter;

public class TradingPairsFragment extends MainFragment {

    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.trading_pairs_fragment, container, false);
    }

    @Override
    public ContextHolder initHolder() {
        return new TradingPairsHolder(this);
    }

    @Override
    public RecyclerView initRecyclerView(View view) {
        return view.findViewById(R.id.tradingPairsRecycleView);
    }

    @Override
    public RecyclerViewAdapter initAdapter(ContextHolder holder) {
        return new TradingPairsAdapter((TradingPairsHolder) holder);
    }

    @Override
    public void onStart() {
        super.onStart();
        getHolder().updateAllItems();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        TradingPairsHolder tradingPairsHolder = (TradingPairsHolder) getHolder();
        if(!hidden) {
            BalancePreferences balancePreferences = new BalancePreferences(getContext());
            tradingPairsHolder.updateFromBalance(balancePreferences.getItemsSet());
            tradingPairsHolder.updateAllItems();
        }
    }
}
