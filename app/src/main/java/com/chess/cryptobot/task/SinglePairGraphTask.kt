package com.chess.cryptobot.task

import android.graphics.Color
import android.os.AsyncTask
import com.chess.cryptobot.R
import com.chess.cryptobot.model.room.CryptoBotDatabase.Companion.getInstance
import com.chess.cryptobot.model.room.ProfitPair
import com.chess.cryptobot.model.room.ProfitPairDao
import com.chess.cryptobot.view.PairsGraphFragment
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SinglePairGraphTask(pairsGraphFragment: PairsGraphFragment, private val daysToShow: Int, private val pairName: String, private val minPercent: Float) : AsyncTask<Void?, Int?, Void?>() {

    private val graphFragmentWeakReference: WeakReference<PairsGraphFragment> = WeakReference(pairsGraphFragment)
    private var allPairNames: List<String>? = listOf(pairName)
    private var dataSets: List<IBarDataSet>? = null
    private var xAxisNames: MutableList<String>? = null
    private var maxPercent = 0f
    private var dao: ProfitPairDao? = null
    private var date = LocalDateTime.now()

    override fun doInBackground(vararg params: Void?): Void? {
        val context = graphFragmentWeakReference.get()!!.context
        val database = getInstance(context!!)
        dao = database.profitPairDao

        val entriesGroups = initEntriesGroup()
        xAxisNames = ArrayList()
        for (i in 0..daysToShow) {
            val pair = profitPairsByDay(dao, date, minPercent)
            countAveragePercent(pair)
            addToEntriesGroup(entriesGroups, pair, i)
            addToXAxisNames(date)
            date = date.minusDays(1)
        }

        dataSets = createDataSets(entriesGroups)
        return null
    }

    override fun onPostExecute(param: Void?) {
        val barChart = createChart() ?: return
        barChart.invalidate()
        val pairsGraphFragment = graphFragmentWeakReference.get()
        if (pairsGraphFragment != null) {
            pairsGraphFragment.hideSpinner()
            pairsGraphFragment.setSpinnerItems(allPairNames)
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        val pairsGraphFragment = graphFragmentWeakReference.get()
        pairsGraphFragment?.showSpinner()
    }

    private fun profitPairsByDay(dao: ProfitPairDao?, date: LocalDateTime, minPercent: Float): ProfitPair {
        val dateStart = date.toLocalDate().atStartOfDay()
        val dateEnd = date.toLocalDate().atTime(LocalTime.MAX)
        return dao!!.getPairByDayAndMinPercent(pairName, dateStart, dateEnd, minPercent)
    }

    private fun countAveragePercent(pair: ProfitPair) {
        if (pair.id == 0) {
            pair.percent = 0f
            return
        }
        val averagePercent: Float = pair.percent
        if (averagePercent > maxPercent) maxPercent = averagePercent
        pair.percent = averagePercent
    }

    private fun initEntriesGroup(): List<MutableList<BarEntry>> {
        val entriesGroups: MutableList<MutableList<BarEntry>> = ArrayList()
        entriesGroups.add(ArrayList())
        return entriesGroups
    }

    private fun addToEntriesGroup(entriesGroups: List<MutableList<BarEntry>>, pair: ProfitPair?, barEntryIndex: Int) {
        pair?.percent?.let { BarEntry(barEntryIndex.toFloat(), it) }?.let { entriesGroups[0].add(it) }
    }

    private fun createDataSets(entriesGroups: List<MutableList<BarEntry>>): List<IBarDataSet> {
        val color = Color.rgb((Math.random() * 255).toInt(), (Math.random() * 255).toInt(), (Math.random() * 255).toInt())
        val dataSets: MutableList<IBarDataSet> = ArrayList()
        for (entryGroup in entriesGroups) {
            val dataSet: DataSet<*> = BarDataSet(entryGroup, pairName)
            dataSet.color = color
            dataSets.add(dataSet as IBarDataSet)
        }
        return dataSets
    }

    private fun addToXAxisNames(date: LocalDateTime) {
        xAxisNames!!.add(date.format(DateTimeFormatter.ofPattern("dd.MM")))
    }

    private fun createChart(): HorizontalBarChart? {
        val pairsGraphFragment = graphFragmentWeakReference.get() ?: return null
        if (dataSets == null) return null
        val groupSpace = 0.00f
        val barSpace = 0.00f
        val barWidth = calculateBarWidth(dataSets!!.size)
        val barChart = pairsGraphFragment.chart ?: return null
        val data = BarData(dataSets?.reversed())
        data.barWidth = barWidth
        data.setDrawValues(false)
        barChart.data = data
        val textColor = pairsGraphFragment.resources.getColor(R.color.colorWhite, null)
        customizeXAxis(barChart.xAxis, textColor, xAxisNames)
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.LEFT), textColor)
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.RIGHT), textColor)
        customizeBarChart(barChart)
        if (dataSets!!.size > 1) barChart.groupBars(-1f, groupSpace, barSpace)
        return barChart
    }

    private fun customizeBarChart(barChart: HorizontalBarChart) {
        val description = Description()
        description.text = ""
        barChart.description = description
        barChart.setFitBars(true)
        barChart.extraBottomOffset = 20f
        barChart.setVisibleYRange(maxPercent, 1f, YAxis.AxisDependency.LEFT)
        barChart.setVisibleXRange(daysToShow.toFloat(), 1f)
        barChart.enableScroll()
        barChart.legend.isEnabled = false
    }

    private fun calculateBarWidth(elementsCount: Int): Float {
        return if (elementsCount == 0) 1f else 1f / elementsCount
    }

    private fun customizeXAxis(xAxis: XAxis, textColor: Int, axisNames: List<String>?) {
        xAxis.mAxisMinimum = 0f
        xAxis.mAxisMaximum = daysToShow.toFloat()
        xAxis.calculate(0f, daysToShow.toFloat())
        xAxis.granularity = 1f
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                if (value < 0) {
                    return ""
                } else if (value >= axisNames!!.size) {
                    return ""
                }
                return axisNames[value.toInt()]
            }
        }
        xAxis.valueFormatter = formatter
        xAxis.textColor = textColor
    }

    private fun customizeYAxis(yAxis: YAxis, textColor: Int) {
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = maxPercent
        yAxis.calculate(0f, maxPercent)
        yAxis.setCenterAxisLabels(true)
        yAxis.setDrawLabels(true)
        yAxis.textColor = textColor
    }
}