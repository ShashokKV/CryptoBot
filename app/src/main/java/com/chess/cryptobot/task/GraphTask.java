package com.chess.cryptobot.task;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.room.Room;

import com.chess.cryptobot.R;
import com.chess.cryptobot.model.room.CryptoBotDatabase;
import com.chess.cryptobot.model.room.ProfitPair;
import com.chess.cryptobot.model.room.ProfitPairDao;
import com.chess.cryptobot.view.GraphFragment;
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
import java.util.Collections;
import java.util.List;

public class GraphTask extends AsyncTask<Void, Integer, HorizontalBarChart> {
    private WeakReference<GraphFragment> graphFragmentWeakReference;
    private List<String> allPairNames;
    private int daysToShow;
    private float minPercent;
    private float maxPercent;

    public GraphTask(GraphFragment graphFragment, int daysToShow, float minPercent) {
        this.graphFragmentWeakReference = new WeakReference<>(graphFragment);
        this.daysToShow = daysToShow;
        this.minPercent = minPercent;
    }

    @Override
    protected HorizontalBarChart doInBackground(Void... voids) {
        LocalDateTime date = LocalDateTime.now();
        CryptoBotDatabase database = getDatabase();
        if (database == null) return null;
        ProfitPairDao dao = getDatabase().getProfitPairDao();
        LocalDateTime searchDate = date.minusDays(daysToShow);

        allPairNames = dao.getPairNamesByDateAndMinPercent(searchDate, minPercent);
        if (allPairNames.isEmpty()) return null;
        List<List<BarEntry>> entriesGroups = initEntriesGroup();
        List<String> daysForXAxis = new ArrayList<>();

        for (int i = 0; i <= daysToShow; i++) {
            List<ProfitPair> pairs = getProfitPairsByDayAndMinPercent(dao, date, minPercent);
            countApproximatePercent(pairs);
            Collections.sort(pairs);
            addToEntriesGroup(entriesGroups, pairs, i);
            addToXAxisNames(daysForXAxis, date);
            date = date.minusDays(1);
        }

        List<IBarDataSet> dataSets = createDataSets(entriesGroups, allPairNames);
        return updateChart(dataSets, daysForXAxis);
    }

    @Override
    protected void onPostExecute(HorizontalBarChart barChart) {
        if (barChart == null) return;
        barChart.invalidate();
        GraphFragment graphFragment = graphFragmentWeakReference.get();
        if (graphFragment != null) {
            graphFragment.hideSpinner();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        GraphFragment graphFragment = graphFragmentWeakReference.get();
        if (graphFragment != null) {
            graphFragment.showSpinner();
        }
    }

    private List<ProfitPair> getProfitPairsByDayAndMinPercent(ProfitPairDao dao, LocalDateTime date, Float minPercent) {
        LocalDateTime dateStart = date.toLocalDate().atStartOfDay();
        LocalDateTime dateEnd = date.toLocalDate().atTime(LocalTime.MAX);
        return dao.getPairsByDayAndMinPercent(dateStart, dateEnd, minPercent);
    }

    private void countApproximatePercent(List<ProfitPair> pairs) {
        pairs.forEach(pair -> {
            float approximatePercent = pair.getPercent() / pair.getId();
            if (approximatePercent>maxPercent) maxPercent = approximatePercent;
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
            dataSets.add((IBarDataSet)dataSet);
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

    private void addToXAxisNames(List<String> daysForXAxis, LocalDateTime date) {
        daysForXAxis.add(String.format("%s.%s", date.getDayOfMonth(), date.getMonthValue()));
    }

    private HorizontalBarChart updateChart(List<IBarDataSet> dataSets, List<String> xAxisNames) {
        GraphFragment graphFragment = graphFragmentWeakReference.get();
        if (graphFragment == null) return null;

        float groupSpace = 0.00f;
        float barSpace = 0.00f;
        float barWidth = calculateBarWidth(dataSets.size(), groupSpace, barSpace);

        HorizontalBarChart barChart = graphFragment.getChart();

        BarData data = new BarData(dataSets);
        data.setBarWidth(barWidth);
        data.setDrawValues(false);
        barChart.setData(data);

        int textColor = graphFragment.getResources().getColor(R.color.colorWhite, null);

        customizeXAxis(barChart.getXAxis(), textColor, xAxisNames);
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.LEFT), textColor);
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.RIGHT), textColor);
        customizeLegend(barChart.getLegend(), textColor);
        customizeBarChart(barChart);
        if (dataSets.size()>1) barChart.groupBars(0, groupSpace, barSpace);

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

    private float calculateBarWidth(int elementsCount, float groupSpace, float barSpace) {
        // (barSpace + barWidth) * elementsCount + groupSpace = 1
        if (elementsCount==0) return 1f-barSpace;
        return ((1f-barSpace)/elementsCount - groupSpace);
    }

    private void customizeXAxis(XAxis xAxis, int textColor, List<String> axisNames) {
        xAxis.calculate(0, daysToShow);

        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (value<0) {
                    return "";
                }else if(value>=axisNames.size()) {
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
        for (int i=0; i<colorsCount; i++) {
            colors[i] = Color.rgb((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
        }
        return colors;
    }

    private void customizeLegend(Legend legend, int textColor) {
        legend.setTextColor(textColor);
        legend.setWordWrapEnabled(true);
    }

    private CryptoBotDatabase getDatabase() {

        GraphFragment graphFragment = graphFragmentWeakReference.get();
        if (graphFragment == null) return null;
        Context context = graphFragment.getContext();
        if (context == null) return null;

        return Room.databaseBuilder(context, CryptoBotDatabase.class, "cryptobotDB")
                .enableMultiInstanceInvalidation()
                .build();
    }
}
