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

import java.util.ArrayList;

public class TradingPairsAdapter extends RecyclerView.Adapter<TradingPairsAdapter.TradingPairsViewHolder> {
private ArrayList<TradingPair> tradingPairs;

    public TradingPairsAdapter(TradingPairsHolder tradingPairsHolder) {
        this.tradingPairs = new ArrayList<>();
        tradingPairsHolder.getTradingPairs()
                .forEach(tradingPair -> tradingPairs.add(new TradingPair(tradingPair)));
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
        TradingPair tradingPair = tradingPairs.get(position);

        holder.pairNameView.setText(tradingPair.getLIvecoinPairName());
        holder.livecoinBidView.setText(String.valueOf(tradingPair.getLivecoinBid()));
        holder.livecoinAskVeiw.setText(String.valueOf(tradingPair.getLivecoinAsk()));
        holder.bittrextBidView.setText(String.valueOf(tradingPair.getBittrexBid()));
        holder.bittrexAskView.setText(String.valueOf(tradingPair.getBittrexAsk()));
        holder.percent.setText(String.valueOf(tradingPair.getPercent()));
    }

    @Override
    public int getItemCount() {
        return tradingPairs.size();
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
            percent = itemView.findViewById(R.id.percent);
        }
    }
}
