package com.chess.cryptobot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.view.adapter.PairsAdapter.PairsViewHolder
import java.util.*

class PairsAdapter(pairsHolder: ContextHolder) : RecyclerViewAdapter<PairsViewHolder>(pairsHolder) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        return PairsViewHolder(layoutInflater.inflate(R.layout.trading_pair_line, parent, false))
    }

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        val pair = getItemByPosition(position) as Pair
        holder.pairNameView.text = pair.name
        holder.binanceBidView.text = String.format(Locale.getDefault(), "%.8f", pair.binanceBid)
        holder.binanceBidQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.binanceBidQuantity)
        holder.binanceAskView.text = String.format(Locale.getDefault(), "%.8f", pair.binanceAsk)
        holder.binanceAskQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.binanceAskQuantity)
        holder.binanceVolumeView.text = String.format(Locale.getDefault(), "%.10f", pair.binanceVolume)
        holder.bittrexBidView.text = String.format(Locale.getDefault(), "%.8f", pair.bittrexBid)
        holder.bittrexBidQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.bittrexBidQuantity)
        holder.bittrexAskView.text = String.format(Locale.getDefault(), "%.8f", pair.bittrexAsk)
        holder.bittrexAskQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.bittrexAskQuantity)
        holder.bittrexVolumeView.text = String.format(Locale.getDefault(), "%.8f", pair.bittrexVolume)
        holder.percent.text = pair.percent.toString()
    }

    inner class PairsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pairNameView: TextView = itemView.findViewById(R.id.pair_name)
        val bittrexAskView: TextView = itemView.findViewById(R.id.bittrex_ask)
        val bittrexAskQuantityView: TextView = itemView.findViewById(R.id.bittrex_ask_quantity)
        val bittrexBidView: TextView = itemView.findViewById(R.id.bittrex_bid)
        val bittrexBidQuantityView: TextView = itemView.findViewById(R.id.bittrex_bid_quantity)
        val bittrexVolumeView: TextView = itemView.findViewById(R.id.bittrex_volume)
        val binanceAskView: TextView = itemView.findViewById(R.id.binance_ask)
        val binanceAskQuantityView: TextView = itemView.findViewById(R.id.binance_ask_quantity)
        val binanceBidView: TextView = itemView.findViewById(R.id.binance_bid)
        val binanceBidQuantityView: TextView = itemView.findViewById(R.id.binance_bid_quantity)
        val binanceVolumeView: TextView = itemView.findViewById(R.id.binance_volume)
        val percent: TextView = itemView.findViewById(R.id.pair_percent)

    }
}