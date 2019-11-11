package com.chess.cryptobot.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.pairs.PairsHolder;
import com.chess.cryptobot.model.Pair;

import java.util.Locale;

public class PairsAdapter extends RecyclerViewAdapter<PairsAdapter.PairsViewHolder> {

    public PairsAdapter(PairsHolder pairsHolder) {
        super(pairsHolder);
    }

    @NonNull
    @Override
    public PairsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        return new PairsViewHolder(layoutInflater.inflate(R.layout.trading_pair_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PairsViewHolder holder, int position) {
        Pair pair = (Pair) getItemByPosition(position);

        holder.pairNameView.setText(pair.getName());
        holder.livecoinBidView.setText(String.format (Locale.getDefault(), "%.10f", pair.getLivecoinBid()));
        holder.livecoinAskVeiw.setText(String.format (Locale.getDefault(), "%.10f", pair.getLivecoinAsk()));
        holder.bittrextBidView.setText(String.format (Locale.getDefault(), "%.10f", pair.getBittrexBid()));
        holder.bittrexAskView.setText(String.format (Locale.getDefault(), "%.10f", pair.getBittrexAsk()));
        holder.percent.setText(String.valueOf(pair.getPercent()));
    }

    class PairsViewHolder extends RecyclerView.ViewHolder {
        TextView pairNameView;
        TextView bittrexAskView;
        TextView bittrextBidView;
        TextView livecoinAskVeiw;
        TextView livecoinBidView;
        TextView percent;

        PairsViewHolder(@NonNull View itemView) {
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