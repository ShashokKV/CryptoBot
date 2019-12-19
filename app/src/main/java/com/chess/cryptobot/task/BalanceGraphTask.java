package com.chess.cryptobot.task;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.chess.cryptobot.R;
import com.chess.cryptobot.model.room.BtcBalance;
import com.chess.cryptobot.model.room.BtcBalanceDao;
import com.chess.cryptobot.model.room.CryptoBotDatabase;
import com.chess.cryptobot.view.BalanceGraphFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.ref.WeakReference;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BalanceGraphTask extends AsyncTask<Void, Integer, Void> {
    private final WeakReference<BalanceGraphFragment> graphFragmentWeakReference;
    private List<ILineDataSet> dataSets;
    private float minTime = 0.0f;
    private float maxTime = 0.0f;
    private float maxBalance = 0.0f;
    private float minBalance = 0.0f;

    public BalanceGraphTask(BalanceGraphFragment balanceGraphFragment) {
        this.graphFragmentWeakReference = new WeakReference<>(balanceGraphFragment);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Context context = graphFragmentWeakReference.get().getContext();
        CryptoBotDatabase database = CryptoBotDatabase.getInstance(context);
        BtcBalanceDao dao = database.getBtcBalanceDao();

        LocalDateTime dateEnd = LocalDateTime.now();
        LocalDateTime dateStart = dateEnd.minusDays(30);
        List<BtcBalance> balances = dao.getByDate(dateStart, dateEnd);

        dataSets = createDataSets(balances);
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        LineChart lineChart = createChart();
        if (lineChart == null) return;
        lineChart.invalidate();
    }

    private List<ILineDataSet> createDataSets(List<BtcBalance> balances) {
        List<Entry> entries = new ArrayList<>();

        balances.forEach(btcBalance -> {
            float balance = btcBalance.getBalance();
            float time = floatDateTime(btcBalance.getDateCreated());
            entries.add(new Entry(floatDateTime(btcBalance.getDateCreated()), balance));
            if (balance > maxBalance) maxBalance = balance;
            if (minBalance == 0) minBalance = maxBalance;
            if (minBalance > balance) minBalance = balance;

            if (time > maxTime) maxTime = time;
            if (minTime == 0) minTime = time;
            if (minTime > time) minTime = time;
        });

        LineDataSet dataSet = new LineDataSet(entries, "BTC");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.rgb(250, 87, 136));

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        return dataSets;
    }

    private float floatDateTime(LocalDateTime dateTime) {
        return (float) dateTime.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
    }

    private LineChart createChart() {
        BalanceGraphFragment balanceGraphFragment = graphFragmentWeakReference.get();
        if (balanceGraphFragment == null) return null;
        if (dataSets == null) return null;

        LineChart lineChart = balanceGraphFragment.getChart();

        LineData data = new LineData(dataSets);
        data.setValueTextSize(14f);
        data.setValueTextColor(-1);
        lineChart.setData(data);

        int textColor = balanceGraphFragment.getResources().getColor(R.color.colorWhite, null);

        customizeXAxis(lineChart.getXAxis(), textColor);
        customizeYAxis(lineChart.getAxis(YAxis.AxisDependency.LEFT), textColor);
        customizeYAxis(lineChart.getAxis(YAxis.AxisDependency.RIGHT), textColor);
        customizeLegend(lineChart.getLegend(), textColor);
        customizeChart(lineChart);

        return lineChart;
    }

    private void customizeChart(LineChart lineChart) {
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.setExtraBottomOffset(20f);
        lineChart.setVisibleXRangeMaximum(450000f);
        lineChart.enableScroll();
    }

    private void customizeXAxis(XAxis xAxis, int textColor) {
        xAxis.calculate(minTime, maxTime);
        xAxis.setGranularity(1800f);
        xAxis.setGranularityEnabled(true);

        xAxis.setCenterAxisLabels(true);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return LocalDateTime.ofEpochSecond(Math.round(value), 0,
                        ZoneOffset.systemDefault().getRules().getOffset(Instant.now()))
                        .format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
            }
        };
        xAxis.setValueFormatter(formatter);
        xAxis.setTextColor(textColor);
    }

    private void customizeYAxis(YAxis yAxis, int textColor) {
        yAxis.calculate(minBalance, maxBalance);
        yAxis.setCenterAxisLabels(true);
        yAxis.setTextColor(textColor);
    }

    private void customizeLegend(Legend legend, int textColor) {
        legend.setTextColor(textColor);
        legend.setWordWrapEnabled(true);
    }
}
