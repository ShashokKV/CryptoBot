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
import java.util.*
import java.util.function.Consumer

class PairsGraphTask(pairsGraphFragment: PairsGraphFragment, private val daysToShow: Int, private val pairName: String?, private val minPercent: Float) : AsyncTask<Void?, Int?, Void?>() {

    private val graphFragmentWeakReference: WeakReference<PairsGraphFragment> = WeakReference(pairsGraphFragment)
    private var allPairNames: List<String>? = null
    private var dataSets: List<IBarDataSet>? = null
    private var xAxisNames: MutableList<String>? = null
    private var maxPercent = 0f

    override fun doInBackground(vararg params: Void?): Void? {
        val context = graphFragmentWeakReference.get()!!.context
        val database = getInstance(context)
        val dao = database!!.profitPairDao
        var date = LocalDateTime.now()
        val searchDate = date.minusDays(daysToShow.toLong())
        if (pairName != null) {
            allPairNames = ArrayList()
            (allPairNames as ArrayList<String>).add(pairName)
        } else {
            allPairNames = dao!!.getPairNamesByDateAndMinPercent(searchDate, minPercent)
        }
        if (allPairNames!!.isEmpty()) return null
        val entriesGroups = initEntriesGroup()
        xAxisNames = ArrayList()
        for (i in 0..daysToShow) {
            val pairs = getProfitPairsByDayAndMinPercent(dao, date, minPercent)
            countApproximatePercent(pairs)
            addToEntriesGroup(entriesGroups, pairs, i)
            addToXAxisNames(date)
            date = date.minusDays(1)
        }
        dataSets = createDataSets(entriesGroups, allPairNames)
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

    private fun getProfitPairsByDayAndMinPercent(dao: ProfitPairDao?, date: LocalDateTime, minPercent: Float): List<ProfitPair?>? {
        val dateStart = date.toLocalDate().atStartOfDay()
        val dateEnd = date.toLocalDate().atTime(LocalTime.MAX)
        return dao!!.getPairsByDayAndMinPercent(pairName, dateStart, dateEnd, minPercent)
    }

    private fun countApproximatePercent(pairs: List<ProfitPair?>?) {
        pairs!!.forEach(Consumer { pair: ProfitPair? ->
            val approximatePercent = pair!!.percent!! / pair.id
            if (approximatePercent > maxPercent) maxPercent = approximatePercent
            pair.percent = approximatePercent
        })
    }

    private fun initEntriesGroup(): List<MutableList<BarEntry>> {
        val entriesGroups: MutableList<MutableList<BarEntry>> = ArrayList()
        for (i in allPairNames!!.indices) {
            entriesGroups.add(ArrayList())
        }
        return entriesGroups
    }

    private fun addToEntriesGroup(entriesGroups: List<MutableList<BarEntry>>, pairs: List<ProfitPair?>?, barEntryIndex: Int) {
        for (j in allPairNames!!.indices) {
            entriesGroups[j].add(BarEntry(barEntryIndex.toFloat(), percentFromPairByName(allPairNames!![j], pairs)!!))
        }
    }

    private fun createDataSets(entriesGroups: List<MutableList<BarEntry>>, allPairNames: List<String?>?): List<IBarDataSet> {
        val colors = generateColors(allPairNames!!.size)
        val dataSets: MutableList<IBarDataSet> = ArrayList()
        for (i in allPairNames.indices) {
            val dataSet: DataSet<*> = BarDataSet(entriesGroups[i], allPairNames[i])
            dataSet.color = colors[i]
            dataSets.add(dataSet as IBarDataSet)
        }
        return dataSets
    }

    private fun percentFromPairByName(pairName: String?, pairs: List<ProfitPair?>?): Float? {
        for (pair in pairs!!) {
            if (pair!!.pairName == pairName) {
                return pair.percent
            }
        }
        return 0.0f
    }

    private fun addToXAxisNames(date: LocalDateTime) {
        xAxisNames!!.add(String.format("%s.%s", date.dayOfMonth, date.monthValue))
    }

    private fun createChart(): HorizontalBarChart? {
        val pairsGraphFragment = graphFragmentWeakReference.get() ?: return null
        if (dataSets == null) return null
        val groupSpace = 0.00f
        val barSpace = 0.00f
        val barWidth = calculateBarWidth(dataSets!!.size)
        val barChart = pairsGraphFragment.chart ?: return null
        val data = BarData(dataSets)
        data.barWidth = barWidth
        data.setDrawValues(false)
        barChart.data = data
        val textColor = pairsGraphFragment.resources.getColor(R.color.colorWhite, null)
        customizeXAxis(barChart.xAxis, textColor, xAxisNames)
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.LEFT), textColor)
        customizeYAxis(barChart.getAxis(YAxis.AxisDependency.RIGHT), textColor)
        customizeLegend(barChart.legend, textColor)
        customizeBarChart(barChart)
        if (dataSets!!.size > 1) barChart.groupBars(0f, groupSpace, barSpace)
        return barChart
    }

    private fun customizeBarChart(barChart: HorizontalBarChart) {
        val description = Description()
        description.text = ""
        barChart.description = description
        barChart.setFitBars(true)
        barChart.extraBottomOffset = 20f
        barChart.setVisibleXRangeMaximum(4f)
        barChart.enableScroll()
    }

    private fun calculateBarWidth(elementsCount: Int): Float {
        return if (elementsCount == 0) 1f else 1f / elementsCount
    }

    private fun customizeXAxis(xAxis: XAxis, textColor: Int, axisNames: List<String>?) {
        xAxis.calculate(0f, daysToShow.toFloat())
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
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
        yAxis.calculate(0f, maxPercent)
        yAxis.setCenterAxisLabels(true)
        yAxis.textColor = textColor
    }

    private fun generateColors(colorsCount: Int): IntArray {
        val colors = IntArray(colorsCount)
        for (i in 0 until colorsCount) {
            colors[i] = Color.rgb((Math.random() * 255).toInt(), (Math.random() * 255).toInt(), (Math.random() * 255).toInt())
        }
        return colors
    }

    private fun customizeLegend(legend: Legend, textColor: Int) {
        legend.textColor = textColor
        legend.isWordWrapEnabled = true
    }

}