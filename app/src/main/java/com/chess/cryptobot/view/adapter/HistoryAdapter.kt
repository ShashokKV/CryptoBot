package com.chess.cryptobot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.history.HistoryHolder
import com.chess.cryptobot.model.History
import com.chess.cryptobot.view.adapter.HistoryAdapter.HistoryViewHolder
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryAdapter(holder: ContextHolder, private val state: HistoryHolder.State) : RecyclerViewAdapter<HistoryViewHolder>(holder) {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HistoryViewHolder {
        val context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)
        val historyView = layoutInflater.inflate(R.layout.history_line_layout, viewGroup, false)
        return HistoryViewHolder(historyView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItemByPosition(position) as History
        holder.timeView.text = String.format("%s\n%s", history.dateTime!!.format(DateTimeFormatter.ISO_DATE),
                history.dateTime!!.withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME))
        holder.marketView.text = history.market
        holder.nameView.text = history.currencyName
        holder.actionView.text = history.action!!.toLowerCase(Locale.getDefault())
        holder.amountView.text = String.format(Locale.US, "%.8f", history.amount)
        holder.priceView.text = if (history.price == null) "" else String.format(Locale.US, "%.8f", history.price)
        if (state === HistoryHolder.State.HISTORY) {
            holder.progressBar.visibility = View.GONE
        } else {
            holder.progressBar.progress = history.progress!!
        }
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeView: TextView = itemView.findViewById(R.id.history_time)
        val marketView: TextView = itemView.findViewById(R.id.history_market)
        val nameView: TextView = itemView.findViewById(R.id.history_currency_name)
        val actionView: TextView = itemView.findViewById(R.id.history_action)
        val amountView: TextView = itemView.findViewById(R.id.history_amount)
        val priceView: TextView = itemView.findViewById(R.id.history_price)
        val progressBar: ProgressBar = itemView.findViewById(R.id.history_progress)
    }

}