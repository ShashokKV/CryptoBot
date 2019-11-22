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
import com.chess.cryptobot.market.Market;
import com.chess.cryptobot.model.Balance;

public class BalanceAdapter extends RecyclerViewAdapter<BalanceAdapter.BalanceViewHolder> {
    private final RecyclerViewOnClickListener mListener;

    public BalanceAdapter(BalanceHolder balanceHolder) {
        super(balanceHolder);
        this.mListener = new BalanceViewOnClickListener(balanceHolder);
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
        Balance balance = (Balance) getItemByPosition(i);

        balanceViewHolder.bittrexBalanceView.setText(String.valueOf(balance.getAmount(Market.BITTREX_MARKET)));
        Context context = balanceViewHolder.bittrexBalanceView.getContext();
        if (!balance.getStatus(Market.BITTREX_MARKET)) {
            balanceViewHolder.bittrexBalanceView.setTextColor(context.getResources().getColor(R.color.colorError, null));
        }
        balanceViewHolder.livecoinBalanceView.setText(String.valueOf(balance.getAmount(Market.LIVECOIN_MARKET)));
        if (!balance.getStatus(Market.LIVECOIN_MARKET)) {
            balanceViewHolder.livecoinBalanceView.setTextColor(context.getResources().getColor(R.color.colorError, null));
        }
        balanceViewHolder.cryptoNameView.setText(balance.getName());
        Bitmap bitmap = balance.getCoinIcon();
        if (bitmap != null) balanceViewHolder.cryptoImageView.setImageBitmap(bitmap);
    }

    class BalanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RecyclerViewOnClickListener mOnClickListener;

        final TextView livecoinBalanceView;
        final TextView bittrexBalanceView;
        final TextView cryptoNameView;
        final ImageView cryptoImageView;

        BalanceViewHolder(View view, RecyclerViewOnClickListener onClickListener) {
            this(view);
            this.mOnClickListener = onClickListener;
            view.setOnClickListener(this);
        }

        BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            livecoinBalanceView = itemView.findViewById(R.id.LivecoinBalanceView);
            bittrexBalanceView = itemView.findViewById(R.id.BittrexBalanceView);
            cryptoNameView = itemView.findViewById(R.id.CryptoNameView);
            cryptoImageView = itemView.findViewById(R.id.CryptoImageView);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onClick(v, itemNameByPosition(getAdapterPosition()));
        }
    }
}
