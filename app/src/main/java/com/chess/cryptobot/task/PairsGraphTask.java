package com.chess.cryptobot.task;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.chess.cryptobot.R;
import com.chess.cryptobot.model.room.CryptoBotDatabase;
import com.chess.cryptobot.model.room.ProfitPair;
import com.chess.cryptobot.model.room.ProfitPairDao;
import com.chess.cryptobot.view.PairsGraphFragment;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PairsGraphTask extends AsyncTask<Void, Integer, Void> {
    private final WeakReference<PairsGraphFragment> graphFragmentWeakReference;
    private List<String> allPairNames;
    private List<IBarDataSet> dataSets;
    private List<String> xAxisNames;
    private final int daysToShow;
    private final String pairName;
    private final float minPercent;
    private float maxPercent;

    public PairsGraphTask(PairsGraphFragment pairsGraphFragment, int daysToShow, String pairName, float minPercent) {
        this.graphFragmentWeakReference = new WeakReference<>(pairsGraphFragment);
        this.daysToShow = daysToShow;
        this.minPercent = minPercent;
        this.pairName = pairName;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Context context = graphFragmentWeakReference.get().getContext();
        CryptoBotDatabase database = CryptoBotDatabase.getInstance(context);
        ProfitPairDao dao = database.getProfitPairDao();

        LocalDateTime date = LocalDateTime.now();
        LocalDateTime searchDate = date.minusDays(daysToShow);

        if (pairName!=null) {
            allPairNames = new ArrayList<>();
            allPairNames.add(pairName);
        }else {
            allPairNames = dao.getPairNamesByDateAndMinPercent(searchDate, minPercent);
        }
        if (allPairNames.isEmpty()) return null;
        List<List<BarEntry>> entriesGroups = initEntriesGroup();
        xAxisNames = new ArrayList<>();

        for (int i = 0; i <= daysToShow; i++) {
            List<ProfitPair> pairs = getProfitPairsByDayAndMinPercent(dao, date, minPercent);
            countApproximatePercent(pairs);
            addToEntriesGroup(entriesGroups, pairs, i);
            addToXAxisNames(date);
            date = date.minusDays(1);
        }

        dataSets = createDataSets(entriesGroups, allPairNames);
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        HorizontalBarChart barChart = createChart();
        if (barChart == null) return;
        barChart.invalidate();
        PairsGraphFragment pairsGraphFragment = graphFragmentWeakReference.get();
        if (pairsGraphFragment != null) {
            pairsGraphFragment.hideSpinner();
            pairsGraphFragment.setSpinnerItems(allPairNames);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        PairsGraphFragment pairsGraphFragment = graphFragmentWeakReference.get();
        if (pairsGraphFragment != null) {
            pairsGraphFragment.showSpinner();
        }
    }

    private List<ProfitPair> getProfitPairsByDayAndMinPercent(ProfitPairDao dao, LocalDateTime date, Float minPercent) {
        LocalDateTime dateStart = date.toLocalDate().atStartOfDay();
        LocalDateTime dateEnd = date.toLocalDate().atTime(LocalTime.MAX);
        return dao.getPairsByDayAndMinPercent(pairName, dateStart, dateEnd, minPercent);
    }

    private void countApproximatePercent(List<ProfitPair> pairs) {
        pairs.forEach(pair -> {
            float approximatePercent = pair.getPercent() / pair.getId();
            if (approximatePercent > maxPercent) maxPercent = approximatePercent;
            pair.setPercent(approximatePercent);
        });
    }

    private List<List<BarEntry>> initEntriesGroup() {
        List<List<BarEntry>> entriesGroups = new ArrayList<>();
        for (int i = 0; i < allPairNames.size(); i++) {
            entriesGroups.add(new ArrayList<>());
        }
        return entriesGroups;
    }

    private void addToEntriesGroup(List<List<BarEntry>> entriesGroups, List<ProfitPair> pairs, int barEntryIndex) {
        for (int j = 0; j < allPairNames.size(); j++) {
            entriesGroups.get(j).add(new BarEntry(barEntryIndex, percentFromPairByName(allPairNames.get(j), pairs)));
        }
    }

    private List<IBarDataSet> createDataSets(List<List<BarEntry>> entriesGroups, List<String> allPairNames) {
        int[] colors = generateColors(allPairNames.size());
        List<IBarDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < allPairNames.size(); i++) {
            DataSet dataSet = new BarDataSet(entriesGroups.get(i), allPairNames.get(i));
            dataSet.setColor(colors[i]);
            dataSets.add((IBarDataSet) dataSet);
        }
        return dataSets;
    }

    private Float percentFromPairByName(String pairName, List<ProfitPair> pairs) {
        for (ProfitPair pair : pairs) {
            if (pair.getPairName().equals(pairName)) {
                return pair.getPercent();
            }
        }
        return 0.0f;
    }

    private void addToXAxisNames(LocalDateTime date) {
        xAxisNames.add(String.format("%s.%s", date.getDayOfMonth(), date.getMonthValue()));
    }

    private HorizontalBarChart createChart() {
        PairsGraphFragment pairsGraphFragment = graphFragmentWeakReference.get();
        if (pairsGraphFragment == null) return null;
        if (dataSets == null) return null;

        float groupSpace = 0.00f;
        float barSpace = 0.00f;
        float barWidth = calculateBarWidth(dataSets.size());

        HorizontalBarChart barChart = pairsGraphFragment.getChart();

        BarData data = new BarData(dataSets);
        data.setBarWidth(barWidth);
        data.setDrawValues(false);
        barChart.setData(data);

        int textColor = pairsGraphFragment.getResources().getColor(R.color.colorWhite, null);

        customizeXAxis(barChart.getXAxis(), textColor, xAxisNames);
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.LEFT), textColor);
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.RIGHT), textColor);
        customizeLegend(barChart.getLegend(), textColor);
        customizeBarChart(barChart);
        if (dataSets.size() > 1) barChart.groupBars(0, groupSpace, barSpace);

        return barChart;
    }

    private void customizeBarChart(HorizontalBarChart barChart) {
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.setFitBars(true);
        barChart.setExtraBottomOffset(20f);
        barChart.setVisibleXRangeMaximum(4f);
        barChart.enableScroll();
    }

    private float calculateBarWidth(int elementsCount) {
        if (elementsCount == 0) return 1f;
        return 1f / elementsCount;
    }

    private void customizeXAxis(XAxis xAxis, int textColor, List<String> axisNames) {
        xAxis.calculate(0, daysToShow);

        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (value < 0) {
                    return "";
                } else if (value >= axisNames.size()) {
                    return "";
                }
                return axisNames.get((int) value);
            }
        };
        xAxis.setValueFormatter(formatter);
        xAxis.setTextColor(textColor);
    }

    private void customizeYAxis(YAxis yAxis, int textColor) {
        yAxis.calculate(0, maxPercent);
        yAxis.setCenterAxisLabels(true);
        yAxis.setTextColor(textColor);
    }

    private int[] generateColors(int colorsCount) {
        int[] colors = new int[colorsCount];
        for (int i = 0; i < colorsCount; i++) {
            colors[i] = Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        }
        return colors;
    }

    private void customizeLegend(Legend legend, int textColor) {
        legend.setTextColor(textColor);
        legend.setWordWrapEnabled(true);
    }
}
