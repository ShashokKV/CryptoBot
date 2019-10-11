package com.chess.cryptobot.adapter;

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
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.content.ContextHolder;

import java.util.ArrayList;
import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private List<Balance> balances;

    public BalanceAdapter(ContextHolder contextHolder) {
        List<Balance> contextBalances = contextHolder.getBalances();
        this.balances = new ArrayList<>(contextBalances.size());
        contextBalances.forEach(balance -> this.balances.add(new Balance(balance)));
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View balanceView = layoutInflater.inflate(R.layout.balance_line_layout, viewGroup, false);

        return new BalanceViewHolder(balanceView);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder balanceViewHolder, int i) {
        Balance balance = balances.get(i);

        TextView btrxView = balanceViewHolder.bittrexBalanceView;
        TextView lvcnView = balanceViewHolder.livecoinBalanceView;
        TextView cryptoNameView = balanceViewHolder.cryptoNameView;
        ImageView cryptoImageView = balanceViewHolder.cryptoImageView;

        Double bittrexAmount = balance.getAmount("bittrex");
        Double livecoinAmount = balance.getAmount("livecoin");
        btrxView.setText(bittrexAmount==null ? "0.0" : String.valueOf(balance.getAmount("bittrex")));
        lvcnView.setText(livecoinAmount==null ? "0.0" : String.valueOf(balance.getAmount("livecoin")));
        cryptoNameView.setText(balance.getCoinName());
        Bitmap bitmap = balance.getCoinIcon();
        if (bitmap != null) cryptoImageView.setImageBitmap(bitmap);
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

    class BalanceViewHolder extends RecyclerView.ViewHolder {
        TextView livecoinBalanceView;
        TextView bittrexBalanceView;
        TextView cryptoNameView;
        ImageView cryptoImageView;

        BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            livecoinBalanceView = itemView.findViewById(R.id.LIvecoinBalanceView);
            bittrexBalanceView = itemView.findViewById(R.id.BittrexBalanceView);
            cryptoNameView = itemView.findViewById(R.id.CryptoNameView);
            cryptoImageView = itemView.findViewById(R.id.CryptoImageView);
        }
    }
}
