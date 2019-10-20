package com.chess.cryptobot.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.pairs.TradingPairsHolder;
import com.chess.cryptobot.model.TradingPair;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.view.adapter.TradingPairsAdapter;

public class TradingPairsActivity extends AppCompatActivity implements AdapterActivity {

    private TradingPairsAdapter tradingPairsAdapter;
    private TradingPairsHolder tradingPairsHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trading_pairs_layout);

        tradingPairsHolder = new TradingPairsHolder(this);

        RecyclerView tradingPairsView = findViewById(R.id.tradingPairsRecycleView);
        tradingPairsAdapter = new TradingPairsAdapter(tradingPairsHolder);
        tradingPairsView.setAdapter(tradingPairsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tradingPairsHolder.updateAllItems();
    }

    @Override
    public void addItem(ViewItem item) {
        tradingPairsAdapter.addItem((TradingPair) item);
    }

    @Override
    public void updateItem(ViewItem item) {
        tradingPairsAdapter.updateItem((TradingPair) item);
    }

    @Override
    public void deleteItemByPosition(int position) {
        tradingPairsAdapter.deleteItem(position);
    }

    @Override
    public String itemNameByPosition(int position) {
        return null;
    }

    @Override
    public void makeToast(String message) {

    }
}
