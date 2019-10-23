package com.chess.cryptobot.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

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
        FloatingActionButton floatingActionButton = Objects.requireNonNull(view).findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            CryptoNameDialog nameDialog = new CryptoNameDialog(balanceHolder);
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager!=null) nameDialog.show(getFragmentManager(), "coinName");
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
    public void onStart() {
        super.onStart();
        getHolder().updateAllItems();
    }
}