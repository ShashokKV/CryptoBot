package com.chess.cryptobot.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chess.cryptobot.R
import com.chess.cryptobot.content.balance.BalanceHolder
import com.chess.cryptobot.market.Market
import com.chess.cryptobot.model.Balance
import com.chess.cryptobot.view.adapter.BalanceAdapter.BalanceViewHolder
import java.util.*

class BalanceAdapter(balanceHolder: BalanceHolder) : RecyclerViewAdapter<BalanceViewHolder>(balanceHolder) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): BalanceViewHolder {
        val context = viewGroup.context
        val layoutInflater = LayoutInflater.from(context)
        val balanceView = layoutInflater.inflate(R.layout.balance_line_layout, viewGroup, false)
        return BalanceViewHolder(balanceView)
    }

    override fun onBindViewHolder(balanceViewHolder: BalanceViewHolder, i: Int) {
        val balance = getItemByPosition(i) as Balance
        balanceViewHolder.bittrexBalanceView.text = String.format(Locale.US, "%.8f", balance.getAmount(Market.BITTREX_MARKET))
        val context = balanceViewHolder.bittrexBalanceView.context
        if (!balance.getStatus(Market.BITTREX_MARKET)) {
            balanceViewHolder.bittrexBalanceView.setTextColor(context.resources.getColor(R.color.colorError, null))
        }
        balanceViewHolder.binanceBalanceView.text = String.format(Locale.US, "%.8f", balance.getAmount(Market.BINANCE_MARKET))
        if (!balance.getStatus(Market.BINANCE_MARKET)) {
            balanceViewHolder.binanceBalanceView.setTextColor(context.resources.getColor(R.color.colorError, null))
        }
        balanceViewHolder.poloniexBalanceView.text = String.format(Locale.US, "%.8f", balance.getAmount(Market.POLONIEX_MARKET))
        if (!balance.getStatus(Market.POLONIEX_MARKET)) {
            balanceViewHolder.poloniexBalanceView.setTextColor(context.resources.getColor(R.color.colorError, null))
        }
        balanceViewHolder.cryptoNameView.text = balance.name
        val bitmap = balance.coinIcon
        if (bitmap != null) balanceViewHolder.cryptoImageView.setImageBitmap(bitmap)
    }

    class BalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binanceBalanceView: TextView = itemView.findViewById(R.id.BinanceBalanceView)
        val bittrexBalanceView: TextView = itemView.findViewById(R.id.BittrexBalanceView)
        val poloniexBalanceView: TextView = itemView.findViewById(R.id.PoloniexBalanceView)
        val cryptoNameView: TextView = itemView.findViewById(R.id.CryptoNameView)
        val cryptoImageView: ImageView = itemView.findViewById(R.id.CryptoImageView)
    }
}