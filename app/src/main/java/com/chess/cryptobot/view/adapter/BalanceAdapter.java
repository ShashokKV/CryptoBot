package com.chess.cryptobot.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.model.Balance;

import java.util.ArrayList;
import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private List<Balance> balances;
    private RecyclerViewOnClickListener mListener;
    private BalanceHolder balanceHolder;

    public BalanceAdapter(BalanceHolder balanceHolder) {
        this.balanceHolder = balanceHolder;
        List<Balance> contextBalances = balanceHolder.getBalances();
        this.balances = new ArrayList<>(contextBalances.size());
        contextBalances.forEach(balance -> this.balances.add(new Balance(balance)));
        this.mListener = new BalanceViewOnClickListener();
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View balanceView = layoutInflater.inflate(R.layout.balance_line_layout, viewGroup, false);
        return new BalanceViewHolder(balanceView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder balanceViewHolder, int i) {
        Balance balance = balances.get(i);

        balanceViewHolder.bittrexBalanceView.setText(String.valueOf(balance.getAmount("bittrex")));
        balanceViewHolder.livecoinBalanceView.setText(String.valueOf(balance.getAmount("livecoin")));
        balanceViewHolder.cryptoNameView.setText(balance.getCoinName());
        Bitmap bitmap = balance.getCoinIcon();
        if (bitmap != null) balanceViewHolder.cryptoImageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    public String coinNameByPosition(int position) {
        return balances.get(position).getCoinName();
    }

    public void addItem(Balance balance) {
        this.balances.add(new Balance(balance));
        this.notifyItemInserted(getItemCount());
    }

    public void deleteItem(int position) {
        balances.remove(position);
        this.notifyItemRemoved(position);
    }

    public void updateItem(Balance balance) {
        int index = this.balances.indexOf(balance);
        if (index >= 0) {
            this.balances.set(index, new Balance(balance));
            this.notifyItemChanged(index);
        }
    }

    private BalanceHolder getBalanceHolder() {
        return balanceHolder;
    }

    class BalanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RecyclerViewOnClickListener mOnClickListener;

        TextView livecoinBalanceView;
        TextView bittrexBalanceView;
        TextView cryptoNameView;
        ImageView cryptoImageView;

        BalanceViewHolder(View view, RecyclerViewOnClickListener onClickListener) {
            this(view);
            this.mOnClickListener = onClickListener;
            view.setOnClickListener(this);
        }

        BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            livecoinBalanceView = itemView.findViewById(R.id.LIvecoinBalanceView);
            bittrexBalanceView = itemView.findViewById(R.id.BittrexBalanceView);
            cryptoNameView = itemView.findViewById(R.id.CryptoNameView);
            cryptoImageView = itemView.findViewById(R.id.CryptoImageView);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onClick(v, getBalanceHolder(), coinNameByPosition(getAdapterPosition()));
        }
    }
}
