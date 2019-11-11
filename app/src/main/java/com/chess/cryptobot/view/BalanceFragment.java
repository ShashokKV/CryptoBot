package com.chess.cryptobot.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.service.BalanceSyncService;
import com.chess.cryptobot.view.adapter.BalanceAdapter;
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter;
import com.chess.cryptobot.view.adapter.SwipeBalanceCallback;
import com.chess.cryptobot.view.dialog.CryptoNameDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class BalanceFragment extends MainFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = super.onCreateView(inflater, container, savedInstanceState);

        BalanceHolder balanceHolder = (BalanceHolder) getHolder();
        FloatingActionButton addBalanceButton = Objects.requireNonNull(view).findViewById(R.id.add_fab);
        addBalanceButton.setOnClickListener(v -> {
            PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f);
            PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(addBalanceButton, scalex, scaley);
            anim.setRepeatCount(1);
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setDuration(300);
            anim.start();

            CryptoNameDialog nameDialog = new CryptoNameDialog(balanceHolder);
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager!=null) nameDialog.show(getFragmentManager(), "coinName");
        });

        FloatingActionButton syncBalanceButton = (view).findViewById(R.id.sync_fab);
        syncBalanceButton.setOnClickListener(click -> {
            PropertyValuesHolder angle = PropertyValuesHolder.ofFloat(View.ROTATION, 360f);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(syncBalanceButton, angle);
            anim.setRepeatCount(1);
            anim.setDuration(500);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    syncBalanceButton.setRotation(0f);
                }
            });
            anim.start();

            TextView titleView = new TextView(this.getContext());
            titleView.setTextColor(getResources().getColor(R.color.colorSecondaryDark, null));
            titleView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
            titleView.setTextSize(20);
            titleView.setGravity(Gravity.CENTER);
            titleView.setText(this.getString(R.string.sync_balances_title));

            AlertDialog alertDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                    .setCustomTitle(titleView)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FragmentActivity activity = this.getActivity();
                        if (activity!=null) {
                            Intent intent = new Intent(activity, BalanceSyncService.class);
                            intent.putStringArrayListExtra("coinNames",
                                    new ArrayList<>(balanceHolder.getPrefs().getItems()));
                            activity.startService(intent);
                        }
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