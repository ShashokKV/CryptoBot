package com.chess.cryptobot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chess.cryptobot.R
import com.chess.cryptobot.task.BalanceGraphTask
import com.chess.cryptobot.task.SerialExecutor
import com.github.mikephil.charting.charts.LineChart

class BalanceGraphFragment : Fragment() {
    private var serialExecutor: SerialExecutor? = null
    var chart: LineChart? = null
        private set

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.balance_graph_fragment, container, false)
        chart = view.findViewById(R.id.balance_graph)
        serialExecutor = SerialExecutor()
        return view
    }

    override fun onResume() {
        super.onResume()
        updateGraph()
    }

    private fun updateGraph() {
        val task = BalanceGraphTask(this)
        task.executeOnExecutor(serialExecutor)
    }

}