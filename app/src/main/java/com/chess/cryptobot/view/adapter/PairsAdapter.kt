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
        holder.binanceVolumeView.text = String.format(Locale.getDefault(), "%.10f", pair.volumeMap[Market.BINANCE_MARKET])
        holder.bittrexBidView.text = String.format(Locale.getDefault(), "%.8f", pair.bidMap[Market.BITTREX_MARKET])
        holder.bittrexBidQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.bidQuantityMap[Market.BITTREX_MARKET])
        holder.bittrexAskView.text = String.format(Locale.getDefault(), "%.8f", pair.askMap[Market.BITTREX_MARKET])
        holder.bittrexAskQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.askQuantityMap[Market.BITTREX_MARKET])
        holder.bittrexVolumeView.text = String.format(Locale.getDefault(), "%.8f", pair.volumeMap[Market.BITTREX_MARKET])
        holder.livecoinBidView.text = String.format(Locale.getDefault(), "%.8f", pair.bidMap[Market.LIVECOIN_MARKET])
        holder.livecoinBidQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.bidQuantityMap[Market.LIVECOIN_MARKET])
        holder.livecoinAskView.text = String.format(Locale.getDefault(), "%.8f", pair.askMap[Market.LIVECOIN_MARKET])
        holder.livecoinAskQuantityView.text = String.format(Locale.getDefault(), "%.8f", pair.askQuantityMap[Market.LIVECOIN_MARKET])
        holder.livecoinVolumeView.text = String.format(Locale.getDefault(), "%.8f", pair.volumeMap[Market.LIVECOIN_MARKET])
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
        val livecoinAskView: TextView = itemView.findViewById(R.id.livecoin_ask)
        val livecoinAskQuantityView: TextView = itemView.findViewById(R.id.livecoin_ask_quantity)
        val livecoinBidView: TextView = itemView.findViewById(R.id.livecoin_bid)
        val livecoinBidQuantityView: TextView = itemView.findViewById(R.id.livecoin_bid_quantity)
        val livecoinVolumeView: TextView = itemView.findViewById(R.id.livecoin_volume)
        val percent: TextView = itemView.findViewById(R.id.pair_percent)

    }
}