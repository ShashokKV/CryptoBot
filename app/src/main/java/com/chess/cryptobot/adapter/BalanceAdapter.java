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

import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private List<Balance> balances;

    public BalanceAdapter(List<Balance> balances) {
        this.balances = balances;
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

        btrxView.setText(String.valueOf(balance.getBittrexAmmount()));
        lvcnView.setText(String.valueOf(balance.getLivecoinAmmount()));
        cryptoNameView.setText(balance.getCoinName());
        Bitmap bitmap = balance.getCoinIcon();
        if (bitmap!=null) cryptoImageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    public String coinNameByPosition(int position) {
        return balances.get(position).getCoinName();
    }

    public void deleteItem(int position) {
        balances.remove(position);
        this.notifyItemRemoved(position);
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

    public synchronized void updateAdapter(Balance[] balances) {
        for (Balance balance : balances) {
            int index = this.balances.indexOf(balance);
            if (index >= 0) {
                this.balances.set(index, balance);
                this.notifyItemChanged(index);
            } else {
                this.balances.add(balance);
                this.notifyItemInserted(this.balances.size()-1);
            }
        }
    }
}
