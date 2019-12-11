package com.chess.cryptobot.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.content.history.HistoryHolder;
import com.chess.cryptobot.model.History;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static android.view.View.GONE;

public class HistoryAdapter extends RecyclerViewAdapter<HistoryAdapter.HistoryViewHolder> {
    private final HistoryHolder.State state;

    public HistoryAdapter(ContextHolder holder, HistoryHolder.State state) {
        super(holder);
        this.state = state;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View historyView = layoutInflater.inflate(R.layout.history_line_layout, viewGroup, false);
        return new HistoryAdapter.HistoryViewHolder(historyView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = (History) getItemByPosition(position);

        holder.timeView.setText(String.format("%s\n%s", history.getDateTime().format(DateTimeFormatter.ISO_DATE),
                history.getDateTime().withNano(0).format(DateTimeFormatter.ISO_LOCAL_TIME)));
        holder.marketView.setText(history.getMarket());
        holder.nameView.setText(history.getCurrencyName());
        holder.actionView.setText(history.getAction().toLowerCase());
        holder.amountView.setText(String.format(Locale.US, "%.8f", history.getAmount()));
        holder.priceView.setText(history.getPrice() == null ? "" : String.format(Locale.US, "%.8f", history.getPrice()));
        if (this.state == HistoryHolder.State.HISTORY) {
            holder.progressBar.setVisibility(GONE);
        } else {
            holder.progressBar.setProgress(history.getProgress());
        }
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        final TextView timeView;
        final TextView marketView;
        final TextView nameView;
        final TextView actionView;
        final TextView amountView;
        final TextView priceView;
        final ProgressBar progressBar;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            timeView = itemView.findViewById(R.id.history_time);
            marketView = itemView.findViewById(R.id.history_market);
            nameView = itemView.findViewById(R.id.history_currency_name);
            actionView = itemView.findViewById(R.id.history_action);
            amountView = itemView.findViewById(R.id.history_amount);
            priceView = itemView.findViewById(R.id.history_price);
            progressBar = itemView.findViewById(R.id.history_progress);
        }
    }
}
