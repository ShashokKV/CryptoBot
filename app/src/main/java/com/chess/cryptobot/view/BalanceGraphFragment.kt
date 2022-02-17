package com.chess.cryptobot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.task.BalanceGraphTask
import com.github.mikephil.charting.charts.LineChart

class BalanceGraphFragment : Fragment() {
    private var spinner: Spinner? = null
    private var adapter: ArrayAdapter<String?>? = null
    var chart: LineChart? = null
        private set

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.balance_graph_fragment, container, false)
        chart = view.findViewById(R.id.balance_graph)
        spinner = view.findViewById(R.id.balance_spinner)
        initSpinner()
        return view
    }

    private fun initSpinner() {
        val context = this.context ?: return

        adapter = object : ArrayAdapter<String?>(context, R.layout.spinner) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                tv.setTextColor(resources.getColor(R.color.colorSecondary, null))

                return view
            }
        }

        adapter?.setDropDownViewResource(R.layout.spinner_drop_down)
        spinner!!.adapter = adapter
        initAdapterWithCoinNames()
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val coinName = parent.getItemAtPosition(position).toString()
                updateGraph(coinName)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initAdapterWithCoinNames() {
        val context = this.context ?: return
        val coinNames: MutableList<String> = ArrayList<String>()
        coinNames.add("USD total")
        coinNames.add("BTC total")
        BalancePreferences(context).items?.let { coinNames.addAll(it) }
        adapter?.clear()
        adapter?.addAll(coinNames)
        adapter?.notifyDataSetChanged()
    }

    private fun updateGraph(coinName: String) {
        val task = BalanceGraphTask(this, coinName)
        task.doInBackground()
    }

}