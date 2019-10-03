package com.chess.cryptobot.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.EditText;

import com.chess.cryptobot.R;
import com.chess.cryptobot.adapter.BalanceAdapter;
import com.chess.cryptobot.callback.SwipeBalanceCallback;
import com.chess.cryptobot.content.Preferences;
import com.chess.cryptobot.dialog.CryptoNameDialog;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.task.CoinImageTask;

import java.util.ArrayList;
import java.util.Set;

public class BalanceActivity extends AppCompatActivity implements CryptoNameDialog.CoinNameDialogListener {

    private RecyclerView balanceRecyclerView;
    private BalanceAdapter balanceAdapter;
    private Preferences preferences;
    ArrayList<Balance> balances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        balanceRecyclerView = findViewById(R.id.balanceRecyclerView);

        init();

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            CryptoNameDialog nameDialog = new CryptoNameDialog();
            nameDialog.show(getSupportFragmentManager(), "coinName");
        });
    }

    private void init() {
        preferences = new Preferences(this);
        initBalances();
        initRecyclerView();
    }

    private void initRecyclerView() {
        balanceAdapter = new BalanceAdapter(balances);
        balanceRecyclerView.setAdapter(balanceAdapter);
        balanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeBalanceCallback(balanceAdapter, preferences));
        itemTouchHelper.attachToRecyclerView(balanceRecyclerView);

        CoinImageTask task = new CoinImageTask(balanceAdapter, getApplicationContext());
        task.execute(balances.toArray(new Balance[0]));
    }

    private void initBalances() {
        balances = new ArrayList<>();
        Set<String> coinNames = preferences.getCoinNames();
        coinNames.forEach(coinName -> balances.add(new Balance(coinName)));
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText nameDialogView = dialog.getDialog().findViewById(R.id.name_dialog_edit_text);
        addBalance(nameDialogView.getText().toString());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    private void addBalance(String coinName) {
        Balance balance = new Balance(coinName);
        if (balances.contains(balance)) {
            return;
        }
        balances.add(balance);
        preferences.addCoinToBalance(coinName);

        CoinImageTask task = new CoinImageTask(balanceAdapter, getApplicationContext());
        task.execute(balance);
    }
}