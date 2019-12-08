package com.chess.cryptobot.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.model.Pair;

import java.util.Locale;

public class PairsAdapter extends RecyclerViewAdapter<PairsAdapter.PairsViewHolder> {

    public PairsAdapter(ContextHolder pairsHolder) {
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
        holder.livecoinBidView.setText(String.format(Locale.getDefault(), "%.8f", pair.getLivecoinBid()));
        holder.livecoinBidQuantityView.setText(String.format(Locale.getDefault(), "%.8f", pair.getLivecoinBidQuantity()));
        holder.livecoinAskView.setText(String.format(Locale.getDefault(), "%.8f", pair.getLivecoinAsk()));
        holder.livecoinAskQuantityView.setText(String.format(Locale.getDefault(), "%.8f", pair.getLivecoinAskQuantity()));
        holder.livecoinVolumeView.setText(String.format(Locale.getDefault(), "%.10f", pair.getLivecoinVolume()));
        holder.bittrextBidView.setText(String.format(Locale.getDefault(), "%.8f", pair.getBittrexBid()));
        holder.bittrextBidQuantityView.setText(String.format(Locale.getDefault(), "%.8f", pair.getBittrexBidQuantity()));
        holder.bittrexAskView.setText(String.format(Locale.getDefault(), "%.8f", pair.getBittrexAsk()));
        holder.bittrexAskQuantityView.setText(String.format(Locale.getDefault(), "%.8f", pair.getBittrexAskQuantity()));
        holder.bittrexVolumeView.setText(String.format(Locale.getDefault(), "%.8f", pair.getBittrexVolume()));
        holder.percent.setText(String.valueOf(pair.getPercent()));
    }

    class PairsViewHolder extends RecyclerView.ViewHolder {
        final TextView pairNameView;
        final TextView bittrexAskView;
        final TextView bittrexAskQuantityView;
        final TextView bittrextBidView;
        final TextView bittrextBidQuantityView;
        final TextView bittrexVolumeView;
        final TextView livecoinAskView;
        final TextView livecoinAskQuantityView;
        final TextView livecoinBidView;
        final TextView livecoinBidQuantityView;
        final TextView livecoinVolumeView;
        final TextView percent;

        PairsViewHolder(@NonNull View itemView) {
            super(itemView);
            pairNameView = itemView.findViewById(R.id.pair_name);
            bittrexAskView = itemView.findViewById(R.id.bittrex_ask);
            bittrexAskQuantityView = itemView.findViewById(R.id.bittrex_ask_quantity);
            bittrextBidView = itemView.findViewById(R.id.bittrex_bid);
            bittrextBidQuantityView = itemView.findViewById(R.id.bittrex_bid_quantity);
            bittrexVolumeView = itemView.findViewById(R.id.bittrex_volume);
            livecoinAskView = itemView.findViewById(R.id.livecoin_ask);
            livecoinAskQuantityView = itemView.findViewById(R.id.livecoin_ask_quantity);
            livecoinBidView = itemView.findViewById(R.id.livecoin_bid);
            livecoinBidQuantityView = itemView.findViewById(R.id.livecoin_bid_quantity);
            livecoinVolumeView = itemView.findViewById(R.id.livecoin_volume);
            percent = itemView.findViewById(R.id.pair_percent);
        }
    }
}
