package com.chess.cryptobot.task

import android.graphics.Color
import com.chess.cryptobot.R
import com.chess.cryptobot.model.room.CryptoBotDatabase.Companion.getInstance
import com.chess.cryptobot.model.room.ProfitPair
import com.chess.cryptobot.model.room.ProfitPairDao
import com.chess.cryptobot.view.PairsGraphFragment
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.time.LocalDateTime

class PairsGraphTask(
    pairsGraphFragment: PairsGraphFragment,
    private val daysToShow: Int,
    private val minPercent: Float
) {

    private val graphFragmentWeakReference: WeakReference<PairsGraphFragment> =
        WeakReference(pairsGraphFragment)
    private var allPairNames: List<String>? = null
    private var maxPercent = 0f
    private var count = 0
    private var dao: ProfitPairDao? = null
    private val date = LocalDateTime.now()
    private var dataSets: MutableList<IBarDataSet> = ArrayList()
    private var labels: MutableList<String> = ArrayList()

    fun doInBackground() {
        onProgressUpdate()
        val context = graphFragmentWeakReference.get()!!.context
        val database = getInstance(context!!)
        dao = database.profitPairDao
        CoroutineScope(Dispatchers.IO).launch {
            val searchDate = date.minusDays(daysToShow.toLong())
            allPairNames = dao!!.getPairNamesByDateAndMinPercent(searchDate, minPercent)

            count = dao!!.getCountByDate(date.minusDays(daysToShow.toLong()), minPercent)

            if (allPairNames!!.isEmpty())
                initDataSets()

            withContext(Dispatchers.Main) {
                onPostExecute()
            }
        }
    }

    private fun initDataSets() {
        val pairs: List<ProfitPair?> = profitPairsByAllTime(dao, date, minPercent)
        countAveragePercent(pairs)
        for (j in pairs.indices) {
            val entries: ArrayList<BarEntry> = ArrayList()
            pairs[j]?.percent?.let { BarEntry(j.toFloat(), it) }?.let { entries.add(it) }
            val dataSet = BarDataSet(entries, pairs[j]?.pairName)
            dataSet.color = Color.rgb(
                (Math.random() * 255).toInt(),
                (Math.random() * 255).toInt(),
                (Math.random() * 255).toInt()
            )
            dataSets.add(dataSet)
            labels.add(pairs[j]?.pairName ?: "")
        }
    }

    private fun onPostExecute() {
        val barChart = createChart() ?: return
        barChart.invalidate()
        val pairsGraphFragment = graphFragmentWeakReference.get()
        if (pairsGraphFragment != null) {
            pairsGraphFragment.hideSpinner()
            pairsGraphFragment.setSpinnerItems(allPairNames)
        }
    }

    private fun onProgressUpdate() {
        val pairsGraphFragment = graphFragmentWeakReference.get()
        pairsGraphFragment?.showSpinner()
    }

    private fun profitPairsByAllTime(
        dao: ProfitPairDao?,
        dateEnd: LocalDateTime,
        minPercent: Float
    ): List<ProfitPair?> {
        val dateStart = dateEnd.minusDays(daysToShow.toLong())
        return dao!!.getAllPairsByDayAndMinPercent(dateStart, dateEnd, minPercent)
    }

    private fun countAveragePercent(pairs: List<ProfitPair?>?) {
        pairs!!.forEach { pair: ProfitPair? ->
            val averagePercent: Float = pair!!.percent / count
            if (averagePercent > maxPercent) maxPercent = averagePercent
            pair.percent = averagePercent
        }

        pairs.sortedByDescending { it?.percent }
    }

    private fun createChart(): HorizontalBarChart? {
        val pairsGraphFragment = graphFragmentWeakReference.get() ?: return null
        val barWidth = 1f
        val barChart = pairsGraphFragment.chart ?: return null
        val data = BarData(dataSets)
        data.barWidth = barWidth
        data.setDrawValues(false)
        barChart.data = data
        val textColor = pairsGraphFragment.resources.getColor(R.color.colorWhite, null)
        customizeXAxis(barChart.xAxis, textColor)
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.LEFT))
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.RIGHT))
        customizeBarChart(barChart)
        return barChart
    }

    private fun customizeBarChart(barChart: HorizontalBarChart) {
        val description = Description()
        description.text = ""
        barChart.description = description
        barChart.setFitBars(true)
        barChart.extraBottomOffset = 20f
        barChart.setVisibleYRange(maxPercent, 1f, YAxis.AxisDependency.LEFT)
        barChart.setVisibleXRangeMaximum(allPairNames?.size?.toFloat() ?: 1f)
        barChart.enableScroll()
        barChart.legend.isEnabled = false
    }

    private fun customizeXAxis(xAxis: XAxis, textColor: Int) {
        val maxSize = allPairNames?.size?.toFloat() ?: 1f
        xAxis.mAxisMinimum = 0f
        xAxis.mAxisMaximum = maxSize
        xAxis.calculate(0f, maxSize)
        xAxis.granularity = 1f
        xAxis.setDrawAxisLine(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textColor = textColor

    }

    private fun customizeYAxis(yAxis: YAxis) {
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = maxPercent
        yAxis.calculate(0f, maxPercent)
        yAxis.setDrawAxisLine(false)
        yAxis.setDrawLabels(false)
    }
}