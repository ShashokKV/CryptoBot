package com.chess.cryptobot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.market.Market
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
        holder.binanceBidView.text = String.format(Locale.getDefault(), "%.8f", pair.bidMap[Market.BINANCE_MARKET])
        holder.binanceBidQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.bidQuantityMap[Market.BINANCE_MARKET])
        holder.binanceAskView.text = String.format(Locale.getDefault(), "%.8f", pair.askMap[Market.BINANCE_MARKET])
        holder.binanceAskQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.askQuantityMap[Market.BINANCE_MARKET])
        holder.bittrexBidView.text = String.format(Locale.getDefault(), "%.8f", pair.bidMap[Market.BITTREX_MARKET])
        holder.bittrexBidQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.bidQuantityMap[Market.BITTREX_MARKET])
        holder.bittrexAskView.text = String.format(Locale.getDefault(), "%.8f", pair.askMap[Market.BITTREX_MARKET])
        holder.bittrexAskQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.askQuantityMap[Market.BITTREX_MARKET])
        holder.poloniexBidView.text = String.format(Locale.getDefault(), "%.8f", pair.bidMap[Market.POLONIEX_MARKET])
        holder.poloniexBidQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.bidQuantityMap[Market.POLONIEX_MARKET])
        holder.poloniexAskView.text = String.format(Locale.getDefault(), "%.8f", pair.askMap[Market.POLONIEX_MARKET])
        holder.poloniexAskQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.askQuantityMap[Market.POLONIEX_MARKET])
        holder.percent.text = pair.percent.toString()
    }

    class PairsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pairNameView: TextView = itemView.findViewById(R.id.pair_name)
        val bittrexAskView: TextView = itemView.findViewById(R.id.bittrex_ask)
        val bittrexAskQuantityView: TextView = itemView.findViewById(R.id.bittrex_ask_quantity)
        val bittrexBidView: TextView = itemView.findViewById(R.id.bittrex_bid)
        val bittrexBidQuantityView: TextView = itemView.findViewById(R.id.bittrex_bid_quantity)
        val binanceAskView: TextView = itemView.findViewById(R.id.binance_ask)
        val binanceAskQuantityView: TextView = itemView.findViewById(R.id.binance_ask_quantity)
        val binanceBidView: TextView = itemView.findViewById(R.id.binance_bid)
        val binanceBidQuantityView: TextView = itemView.findViewById(R.id.binance_bid_quantity)
        val poloniexAskView: TextView = itemView.findViewById(R.id.poloniex_ask)
        val poloniexAskQuantityView: TextView = itemView.findViewById(R.id.poloniex_ask_quantity)
        val poloniexBidView: TextView = itemView.findViewById(R.id.poloniex_bid)
        val poloniexBidQuantityView: TextView = itemView.findViewById(R.id.poloniex_bid_quantity)
        val percent: TextView = itemView.findViewById(R.id.pair_percent)
    }
}