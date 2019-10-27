package com.chess.cryptobot.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.view.dialog.CryptoDialog;
import com.chess.cryptobot.view.dialog.CryptoNameDialog;
import com.chess.cryptobot.view.dialog.DialogListener;
import com.chess.cryptobot.view.dialog.MinBalanceDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogListener {

    final Fragment balanceFragment = new BalanceFragment();
    final Fragment tradingPairFragment = new PairsFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private ProgressBar spinner;
    Fragment active;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager.beginTransaction().add(R.id.include, tradingPairFragment, "2").hide(tradingPairFragment).commit();
        fragmentManager.beginTransaction().add(R.id.include, balanceFragment, "1").show(balanceFragment).commit();
        active = balanceFragment;

        ProgressBar spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.activity_balance:
                fragmentManager.beginTransaction().hide(active).show(balanceFragment).commit();
                active = balanceFragment;
                return true;

            case R.id.activity_pairs:
                fragmentManager.beginTransaction().hide(active).show(tradingPairFragment).commit();
                active = tradingPairFragment;
                return true;
        }
        return false;
    };

    public void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    public void hideSpiiner() {
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onDialogPositiveClick(CryptoDialog dialog) {
        if (dialog instanceof CryptoNameDialog) {
            onCryptoNamePositiveClick((CryptoNameDialog) dialog);
        } else if (dialog instanceof MinBalanceDialog) {
            onMinBalancePositiveClick((MinBalanceDialog) dialog);
        } else {
            throw new IllegalArgumentException("Unknown type of " + dialog.getClass().getName());
        }
    }

    private void onMinBalancePositiveClick(MinBalanceDialog dialog) {
        String coinName = dialog.getCoinName();
        BalanceHolder balanceHolder = dialog.getBalanceHolder();
        EditText minBalanceView = Objects.requireNonNull(dialog.getDialog())
                .findViewById(R.id.min_balance_edit_text);
        Double minBalance = Double.valueOf(minBalanceView.getText().toString());
        balanceHolder.setMinBalance(coinName, minBalance);
    }

    private void onCryptoNamePositiveClick(CryptoNameDialog dialog) {
        BalanceHolder balanceHolder = dialog.getBalanceHolder();
        EditText nameDialogView = Objects.requireNonNull(dialog.getDialog())
                .findViewById(R.id.name_dialog_edit_text);
        String coinName = nameDialogView.getText().toString();
        if (!coinName.isEmpty()) balanceHolder.add(coinName);
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
