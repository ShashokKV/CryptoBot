package com.chess.cryptobot.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.pairs.TradingPairsHolder;
import com.chess.cryptobot.model.TradingPair;
import com.chess.cryptobot.model.ViewItem;

import java.util.Locale;

public class TradingPairsAdapter extends RecyclerViewAdapter<TradingPairsAdapter.TradingPairsViewHolder> {

    public TradingPairsAdapter(TradingPairsHolder tradingPairsHolder) {
        super(tradingPairsHolder);
    }

    @Override
    public TradingPair copyItem(ViewItem item) {
        return (TradingPair) item.copy();
    }

    @NonNull
    @Override
    public TradingPairsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        return new TradingPairsViewHolder(layoutInflater.inflate(R.layout.trading_pair_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TradingPairsViewHolder holder, int position) {
        TradingPair tradingPair = (TradingPair) getItemByPosition(position);

        holder.pairNameView.setText(tradingPair.getName());
        holder.livecoinBidView.setText(String.format (Locale.getDefault(), "%.10f", tradingPair.getLivecoinBid()));
        holder.livecoinAskVeiw.setText(String.format (Locale.getDefault(), "%.10f", tradingPair.getLivecoinAsk()));
        holder.bittrextBidView.setText(String.format (Locale.getDefault(), "%.10f", tradingPair.getBittrexBid()));
        holder.bittrexAskView.setText(String.format (Locale.getDefault(), "%.10f", tradingPair.getBittrexAsk()));
        holder.percent.setText(String.valueOf(tradingPair.getPercent()));
    }

    class TradingPairsViewHolder extends RecyclerView.ViewHolder {
        TextView pairNameView;
        TextView bittrexAskView;
        TextView bittrextBidView;
        TextView livecoinAskVeiw;
        TextView livecoinBidView;
        TextView percent;

        TradingPairsViewHolder(@NonNull View itemView) {
            super(itemView);
            pairNameView = itemView.findViewById(R.id.pair_name);
            bittrexAskView = itemView.findViewById(R.id.bittrex_ask);
            bittrextBidView = itemView.findViewById(R.id.bittrex_bid);
            livecoinAskVeiw = itemView.findViewById(R.id.livecoin_ask);
            livecoinBidView = itemView.findViewById(R.id.livecoin_bid);
            percent = itemView.findViewById(R.id.pair_percent);
        }
    }
}
