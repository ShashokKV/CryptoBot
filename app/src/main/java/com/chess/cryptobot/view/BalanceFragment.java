package com.chess.cryptobot.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.view.adapter.BalanceAdapter;
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter;
import com.chess.cryptobot.view.adapter.SwipeBalanceCallback;
import com.chess.cryptobot.view.dialog.CryptoNameDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class BalanceFragment extends MainFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = super.onCreateView(inflater, container, savedInstanceState);

        BalanceHolder balanceHolder = (BalanceHolder) getHolder();
        FloatingActionButton addBalanceButton = Objects.requireNonNull(view).findViewById(R.id.add_fab);
        addBalanceButton.animate()
                .scaleXBy(10)
                .scaleYBy(10)
                .setDuration(300)
                .start();
        addBalanceButton.setOnClickListener(v -> {
            CryptoNameDialog nameDialog = new CryptoNameDialog(balanceHolder);
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager!=null) nameDialog.show(getFragmentManager(), "coinName");
        });

        FloatingActionButton syncBalanceButton = (view).findViewById(R.id.sync_fab);
        syncBalanceButton.animate()
                .rotation(180f)
                .setDuration(300)
                .start();
        syncBalanceButton.setOnClickListener(click -> {
            AlertDialog alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    .setMessage("Sync all balances?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.show();
        });

        return view;
    }

    @Override
    public View initFragmentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.balance_fragment, container, false);
    }

    @Override
    public ContextHolder initHolder() {
        return new BalanceHolder(this);
    }

    @Override
    public RecyclerView initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.balanceRecyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeBalanceCallback((BalanceHolder) getHolder()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return recyclerView;
    }

    @Override
    public RecyclerViewAdapter initAdapter(ContextHolder holder) {
        return new BalanceAdapter((BalanceHolder) holder);
    }

    @Override
    public SwipeRefreshLayout initSwipeRefresh(View view) {
        return (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshBalance);
    }

    @Override
    public void beforeRefresh() {

    }
}