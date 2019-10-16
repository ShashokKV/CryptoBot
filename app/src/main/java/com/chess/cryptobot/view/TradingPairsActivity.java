package com.chess.cryptobot.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.pairs.TradingPairsHolder;
import com.chess.cryptobot.view.adapter.TradingPairsAdapter;

public class TradingPairsActivity extends AppCompatActivity {
private RecyclerView tradingPairsView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trading_pairs_layout);

        TradingPairsHolder tradingPairsHolder = new TradingPairsHolder(this);

        tradingPairsView = findViewById(R.id.tradingPairsRecycleView);
        TradingPairsAdapter adapter = new TradingPairsAdapter(tradingPairsHolder);
        tradingPairsView.setAdapter(adapter);


    }
}
