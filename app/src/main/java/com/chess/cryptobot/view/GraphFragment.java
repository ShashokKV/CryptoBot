package com.chess.cryptobot.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chess.cryptobot.R;
import com.chess.cryptobot.task.GraphTask;
import com.chess.cryptobot.task.SerialExecutor;
import com.github.mikephil.charting.charts.HorizontalBarChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GraphFragment extends Fragment {
    private HorizontalBarChart chart;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private SeekBar seekBar;
    private String pairName = null;
    private Integer daysToShow = 30;
    private SerialExecutor serialExecutor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment, container, false);

        chart = view.findViewById(R.id.chart);
        spinner = view.findViewById(R.id.spinner);
        seekBar = view.findViewById(R.id.seekBar);
        initSeekBar();
        initSpinner();
        serialExecutor = new SerialExecutor();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateGraph(daysToShow, null);
        }
    }

    private void updateGraph(int daysToShow, String pairName) {
        GraphTask task = new GraphTask(this, daysToShow, pairName, getMinPercent());
        task.executeOnExecutor(serialExecutor);
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

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                GraphFragment.this.daysToShow = progress;
                updateGraph(progress, GraphFragment.this.pairName);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initSpinner() {
        Context context = this.getContext();
        if (context == null) return;

        adapter = new ArrayAdapter<String>(context, R.layout.spinner) {

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(getResources().getColor(R.color.colorSecondary, null));
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinner.setAdapter(adapter);
        List<String> items = new ArrayList<>();
        setSpinnerItems(items);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String pairName = parent.getItemAtPosition(position).toString();
                    GraphFragment.this.pairName = pairName;
                    updateGraph(GraphFragment.this.daysToShow, pairName);
                } else {
                    GraphFragment.this.pairName = null;
                    updateGraph(GraphFragment.this.daysToShow, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSpinnerItems(List<String> pairNames) {
        Context context = this.getContext();
        if (context == null) return;
        List<String> items = new ArrayList<>();
        items.add("No filter");
        items.addAll(pairNames);
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }
}
