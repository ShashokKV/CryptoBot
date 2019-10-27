package com.chess.cryptobot.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter;

import java.util.Objects;

public abstract class MainFragment<T extends RecyclerView.ViewHolder> extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerViewAdapter<T> adapter;
    private ContextHolder holder;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initFragmentView(inflater, container);
        holder = initHolder();

        RecyclerView recyclerView = initRecyclerView(view);
        adapter = initAdapter(holder);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider, null));
        recyclerView.addItemDecoration(dividerItemDecoration);

        swipeRefreshLayout = initSwipeRefresh(view);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorSecondary, R.color.colorSecondaryDark);

        return view;
    }

    @Override
    public void onRefresh() {
        beforeRefresh();
        swipeRefreshLayout.setRefreshing(false);
        holder.updateAllItems();
    }

    public abstract void beforeRefresh();

    public abstract View initFragmentView(LayoutInflater inflater, ViewGroup container);

    public abstract ContextHolder initHolder();

    public abstract RecyclerView initRecyclerView(View view);

    public abstract RecyclerViewAdapter<T> initAdapter(ContextHolder holder);

    public abstract SwipeRefreshLayout initSwipeRefresh(View view);

    public void addItem() { adapter.notifyItemInserted();  }

    public void updateItem(ViewItem item) {
        adapter.updateItem(item);
    }

    public void deleteItemByPosition(int position) {
        adapter.deleteItem(position);
    }

    public String itemNameByPosition(int position) {
        return adapter.itemNameByPosition(position);
    }

    public void makeToast(String message) {
        if (message!=null && !message.isEmpty()) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    public void showSpinner() {
        ProgressBar spinner = Objects.requireNonNull(getActivity()).findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
    }

    public void hideSpinner() {
        ProgressBar spinner = Objects.requireNonNull(getActivity()).findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
    }

    ContextHolder getHolder() {
        return holder;
    }
}
