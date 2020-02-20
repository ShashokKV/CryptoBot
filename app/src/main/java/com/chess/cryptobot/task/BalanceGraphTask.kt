package com.chess.cryptobot.task

import android.graphics.Color
import android.os.AsyncTask
import com.chess.cryptobot.R
import com.chess.cryptobot.model.room.BtcBalance
import com.chess.cryptobot.model.room.CryptoBotDatabase
import com.chess.cryptobot.view.BalanceGraphFragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.lang.ref.WeakReference
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Consumer
import kotlin.math.roundToLong

class BalanceGraphTask(balanceGraphFragment: BalanceGraphFragment) : AsyncTask<Void?, Int?, Void?>() {

    private val graphFragmentWeakReference: WeakReference<BalanceGraphFragment> = WeakReference(balanceGraphFragment)
    private var dataSets: List<ILineDataSet?>? = null
    private var minTime = 0.0f
    private var maxTime = 0.0f
    private var maxBalance = 0.0f
    private var minBalance = 0.0f

    override fun doInBackground(vararg params: Void?): Void? {
        val context = graphFragmentWeakReference.get()!!.context
        val database = CryptoBotDatabase.getInstance(context)
        val dao = database!!.btcBalanceDao
        val dateEnd = LocalDateTime.now()
        val dateStart = dateEnd.minusDays(30)
        val balances = dao!!.getByDate(dateStart, dateEnd)
        dataSets = createDataSets(balances)
        return null
    }

    override fun onPostExecute(param: Void?) {
        val lineChart = createChart() ?: return
        val startPosition = lineChart.xChartMax - MAX_X_RANGE
        if (startPosition > 0) {
            lineChart.moveViewToX(startPosition)
        } else {
            lineChart.invalidate()
        }
    }

    private fun createDataSets(balances: List<BtcBalance>?): List<ILineDataSet?> {
        val entries: MutableList<Entry> = ArrayList()
        val dataSet = LineDataSet(entries, "BTC")
        if (balances==null) return ArrayList()
        balances.forEach(Consumer { btcBalance: BtcBalance ->
            val balance = btcBalance.balance
            val time = floatDateTime(btcBalance.dateCreated)
            entries.add(Entry(floatDateTime(btcBalance.dateCreated), balance))
            if (balance > maxBalance) maxBalance = balance
            if (minBalance == 0f) minBalance = maxBalance
            if (minBalance > balance) minBalance = balance
            if (time > maxTime) maxTime = time
            if (minTime == 0f) minTime = time
            if (minTime > time) minTime = time
        })

        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.color = Color.rgb(250, 87, 136)
        dataSet.setDrawCircles(false)
        val dataSets: MutableList<ILineDataSet> = ArrayList()
        dataSets.add(dataSet)
        return dataSets
    }

    private fun floatDateTime(dateTime: LocalDateTime?): Float {
        return dateTime!!.toEpochSecond(ZoneOffset.systemDefault().rules.getOffset(Instant.now())).toFloat()
    }

    private fun createChart(): LineChart? {
        val balanceGraphFragment = graphFragmentWeakReference.get() ?: return null
        if (dataSets == null) return null
        val lineChart = balanceGraphFragment.chart ?: return null
        val data = LineData(dataSets)
        data.setDrawValues(false)
        lineChart.data = data
        val textColor = balanceGraphFragment.resources.getColor(R.color.colorWhite, null)
        customizeXAxis(lineChart.xAxis, textColor)
        customizeYAxis(lineChart.getAxis(YAxis.AxisDependency.LEFT), textColor)
        customizeYAxis(lineChart.getAxis(YAxis.AxisDependency.RIGHT), textColor)
        customizeLegend(lineChart.legend, textColor)
        customizeChart(lineChart)
        return lineChart
    }

    private fun customizeChart(lineChart: LineChart) {
        val description = Description()
        description.text = ""
        lineChart.description = description
        lineChart.extraBottomOffset = 20f
        lineChart.setVisibleXRangeMaximum(MAX_X_RANGE)
        lineChart.enableScroll()
    }

    private fun customizeXAxis(xAxis: XAxis, textColor: Int) {
        xAxis.calculate(minTime, maxTime)
        xAxis.granularity = 1800f
        xAxis.isGranularityEnabled = true
        xAxis.labelCount = 5
        xAxis.setCenterAxisLabels(true)
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                return LocalDateTime.ofEpochSecond(value.roundToLong(), 0,
                        ZoneOffset.systemDefault().rules.getOffset(Instant.now()))
                        .format(DateTimeFormatter.ofPattern("dd.MM HH:mm"))
            }
        }
        xAxis.valueFormatter = formatter
        xAxis.textColor = textColor
    }

    private fun customizeYAxis(yAxis: YAxis, textColor: Int) {
        yAxis.calculate(minBalance, maxBalance)
        yAxis.setCenterAxisLabels(true)
        yAxis.textColor = textColor
    }

    private fun customizeLegend(legend: Legend, textColor: Int) {
        legend.textColor = textColor
        legend.isWordWrapEnabled = true
    }

    companion object {
        private const val MAX_X_RANGE = 450000f
    }

}