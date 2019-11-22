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
        holder.livecoinBidView.setText(String.format(Locale.getDefault(), "%.10f", pair.getLivecoinBid()));
        holder.livecoinAskVeiw.setText(String.format(Locale.getDefault(), "%.10f", pair.getLivecoinAsk()));
        holder.livecoinVolumeView.setText(String.format(Locale.getDefault(), "%.10f", pair.getLivecoinVolume()));
        holder.bittrextBidView.setText(String.format(Locale.getDefault(), "%.10f", pair.getBittrexBid()));
        holder.bittrexAskView.setText(String.format(Locale.getDefault(), "%.10f", pair.getBittrexAsk()));
        holder.bittrexVolumeView.setText(String.format(Locale.getDefault(), "%.10f", pair.getBittrexVolume()));
        holder.percent.setText(String.valueOf(pair.getPercent()));
    }

    class PairsViewHolder extends RecyclerView.ViewHolder {
        final TextView pairNameView;
        final TextView bittrexAskView;
        final TextView bittrextBidView;
        final TextView bittrexVolumeView;
        final TextView livecoinAskVeiw;
        final TextView livecoinBidView;
        final TextView livecoinVolumeView;
        final TextView percent;

        PairsViewHolder(@NonNull View itemView) {
            super(itemView);
            pairNameView = itemView.findViewById(R.id.pair_name);
            bittrexAskView = itemView.findViewById(R.id.bittrex_ask);
            bittrextBidView = itemView.findViewById(R.id.bittrex_bid);
            bittrexVolumeView = itemView.findViewById(R.id.bittrex_volume);
            livecoinAskVeiw = itemView.findViewById(R.id.livecoin_ask);
            livecoinBidView = itemView.findViewById(R.id.livecoin_bid);
            livecoinVolumeView = itemView.findViewById(R.id.livecoin_volume);
            percent = itemView.findViewById(R.id.pair_percent);
        }
    }
}
