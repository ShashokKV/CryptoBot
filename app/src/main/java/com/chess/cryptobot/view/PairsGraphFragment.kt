package com.chess.cryptobot.view

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import com.chess.cryptobot.R
import com.chess.cryptobot.task.PairsGraphTask
import com.chess.cryptobot.task.SerialExecutor
import com.github.mikephil.charting.charts.HorizontalBarChart
import java.util.*

class PairsGraphFragment : Fragment() {
    var chart: HorizontalBarChart? = null
        private set
    private var spinner: Spinner? = null
    private var adapter: ArrayAdapter<String?>? = null
    private var seekBar: SeekBar? = null
    private var pairName: String? = null
    private var daysToShow = 30
    private var serialExecutor: SerialExecutor? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pairs_graph_fragment, container, false)
        chart = view.findViewById(R.id.chart)
        spinner = view.findViewById(R.id.spinner)
        seekBar = view.findViewById(R.id.seekBar)
        initSeekBar()
        initSpinner()
        serialExecutor = SerialExecutor()
        return view
    }

    override fun onResume() {
        super.onResume()
        updateGraph(daysToShow, pairName)
    }

    private fun updateGraph(daysToShow: Int, pairName: String?) {
        val task = PairsGraphTask(this, daysToShow, pairName, minPercent)
        task.executeOnExecutor(serialExecutor)
    }

    private val minPercent: Float
        get() {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(context?.getString(R.string.min_profit_percent), "3")?.toFloat() ?: 3.0F
        }

    fun showSpinner() {
        val spinner = Objects.requireNonNull(activity)!!.findViewById<ProgressBar>(R.id.progressBar)
        spinner.visibility = View.VISIBLE
    }

    fun hideSpinner() {
        val spinner = Objects.requireNonNull(activity)!!.findViewById<ProgressBar>(R.id.progressBar)
        spinner.visibility = View.GONE
    }

    private fun initSeekBar() {
        seekBar?.max = 30
        seekBar?.progress = 30
        seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                daysToShow = progress
                updateGraph(progress, pairName)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun initSpinner() {
        val context = this.context ?: return

        adapter = object : ArrayAdapter<String?>(context, R.layout.spinner) {
            override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(resources.getColor(R.color.colorSecondary, null))
                }
                return view
            }
        }

        adapter?.setDropDownViewResource(R.layout.spinner_drop_down)
        spinner!!.adapter = adapter
        val items: List<String> = ArrayList()
        setSpinnerItems(items)
        spinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    val pairName = parent.getItemAtPosition(position).toString()
                    this@PairsGraphFragment.pairName = pairName
                    updateGraph(daysToShow, pairName)
                } else {
                    pairName = null
                    updateGraph(daysToShow, null)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun setSpinnerItems(pairNames: List<String>?) {
        this.context ?: return
        val items: MutableList<String?> = ArrayList()
        items.add("No filter")
        items.addAll(pairNames!!)
        adapter?.clear()
        adapter?.addAll(items)
        adapter?.notifyDataSetChanged()
    }
}