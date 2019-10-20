package com.chess.cryptobot.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.model.Balance;
import com.chess.cryptobot.model.ViewItem;
import com.chess.cryptobot.view.adapter.BalanceAdapter;
import com.chess.cryptobot.view.adapter.SwipeBalanceCallback;
import com.chess.cryptobot.view.dialog.CryptoDialog;
import com.chess.cryptobot.view.dialog.CryptoNameDialog;
import com.chess.cryptobot.view.dialog.DialogListener;
import com.chess.cryptobot.view.dialog.MinBalanceDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class BalanceActivity extends AppCompatActivity implements DialogListener, AdapterActivity {
    private BalanceHolder balanceHolder;
    private BalanceAdapter balanceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        init();
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            CryptoNameDialog nameDialog = new CryptoNameDialog();
            nameDialog.show(getSupportFragmentManager(), "coinName");
        });
    }

    private void init() {
        balanceHolder = new BalanceHolder(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView balanceRecyclerView = findViewById(R.id.balanceRecyclerView);
        balanceAdapter = new BalanceAdapter(balanceHolder);
        balanceRecyclerView.setAdapter(balanceAdapter);
        balanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeBalanceCallback(balanceHolder));
        itemTouchHelper.attachToRecyclerView(balanceRecyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        balanceHolder.updateAllItems();
    }

    public void updateItem(ViewItem balance) {
        balanceAdapter.updateItem((Balance) balance);
    }

    public void addItem(ViewItem balance) {
        balanceAdapter.addItem((Balance)balance);
    }

    public String itemNameByPosition(int position) {
        return  balanceAdapter.coinNameByPosition(position);
    }

    public void deleteItemByPosition(int position) {
        balanceAdapter.deleteItem(position);
    }

    public void makeToast(String message) {
        if (message!=null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void onMinBalancePositiveClick(MinBalanceDialog dialog) {
        String coinName = dialog.getCoinName();
        EditText minBalanceView = Objects.requireNonNull(dialog.getDialog())
                .findViewById(R.id.min_balance_edit_text);
        Double minBalance = Double.valueOf(minBalanceView.getText().toString());
        balanceHolder.setMinBalance(coinName, minBalance);
    }

    private void onCryptoNamePositiveClick(CryptoNameDialog dialog) {
        EditText nameDialogView = Objects.requireNonNull(dialog.getDialog())
                .findViewById(R.id.name_dialog_edit_text);
        String coinName = nameDialogView.getText().toString();
        if (!coinName.isEmpty()) balanceHolder.add(coinName);
    }

    @Override
    public void onDialogPositiveClick(CryptoDialog dialog) {
        if (dialog instanceof CryptoNameDialog) {
            onCryptoNamePositiveClick((CryptoNameDialog) dialog);
        }else if (dialog instanceof MinBalanceDialog) {
            onMinBalancePositiveClick((MinBalanceDialog) dialog);
        }else {
            throw new IllegalArgumentException("Unknown type of "+dialog.getClass().getName());
        }
    }

    @Override
    public void onDialogNegativeClick(CryptoDialog dialog) {
        dialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.activity_settings) {
            Intent intent = new Intent(BalanceActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}