package com.chess.cryptobot.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chess.cryptobot.R;
import com.chess.cryptobot.task.GraphTask;
import com.github.mikephil.charting.charts.HorizontalBarChart;

import java.util.Objects;

public class GraphFragment extends Fragment {
    private HorizontalBarChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, container, false);

        chart = view.findViewById(R.id.chart);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            GraphTask task = new GraphTask(this, 30, getMinPercent());
            task.execute();
        }
    }

    private float getMinPercent() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        return Float.valueOf(Objects.requireNonNull(preferences.getString(
                Objects.requireNonNull(getContext())
                        .getString(R.string.min_profit_percent), "3")));
    }

    public HorizontalBarChart getChart() {
        return chart;
    }

    public void showSpinner() {
        ProgressBar spinner = Objects.requireNonNull(getActivity()).findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
    }

    public void hideSpinner() {
        ProgressBar spinner = Objects.requireNonNull(getActivity()).findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
    }
}
