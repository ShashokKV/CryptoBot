package com.chess.cryptobot.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chess.cryptobot.R;
import com.chess.cryptobot.task.BalanceGraphTask;
import com.chess.cryptobot.task.SerialExecutor;
import com.github.mikephil.charting.charts.LineChart;

public class BalanceGraphFragment extends Fragment {
    private SerialExecutor serialExecutor;
    private LineChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.balance_graph_fragment, container, false);

        chart = view.findViewById(R.id.balance_graph);
        serialExecutor = new SerialExecutor();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGraph();
    }

    private void updateGraph() {
        BalanceGraphTask task = new BalanceGraphTask(this);
        task.executeOnExecutor(serialExecutor);
    }

    public LineChart getChart() {
        return chart;
    }
}
