package com.chess.cryptobot.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.chess.cryptobot.R;
import com.chess.cryptobot.content.balance.BalanceHolder;
import com.chess.cryptobot.service.BotService;
import com.chess.cryptobot.view.dialog.CryptoDialog;
import com.chess.cryptobot.view.dialog.CryptoNameDialog;
import com.chess.cryptobot.view.dialog.DialogListener;
import com.chess.cryptobot.view.dialog.MinBalanceDialog;
import com.chess.cryptobot.worker.MarketWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements DialogListener {

    private final BalanceFragment balanceFragment = new BalanceFragment();
    private final PairsFragment pairFragment = new PairsFragment();
    private final GraphFragment graphFragment = new GraphFragment();
    private final HistoryPagerFragment historyPagerFragment = new HistoryPagerFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active;
    private boolean botIsActive;
    private BotService botService;
    private boolean isBound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        boolean openPairs = false;
        if (intent != null) {
            openPairs = intent.getBooleanExtra("openPairs", false);
        }
        if (openPairs) {
            fragmentManager.beginTransaction().add(R.id.include, pairFragment, "2").hide(pairFragment).show(pairFragment).commit();
            fragmentManager.beginTransaction().add(R.id.include, balanceFragment, "1").hide(balanceFragment).commit();
            active = pairFragment;
        } else {
            fragmentManager.beginTransaction().add(R.id.include, pairFragment, "2").hide(pairFragment).commit();
            fragmentManager.beginTransaction().add(R.id.include, balanceFragment, "1").show(balanceFragment).commit();
            active = balanceFragment;
        }
        fragmentManager.beginTransaction().add(R.id.include, graphFragment, "3").hide(graphFragment).commit();
        fragmentManager.beginTransaction().add(R.id.include, historyPagerFragment, "4").hide(historyPagerFragment).commit();

        botIsActive = BotService.isRunning;
        updateBot();
        runWork();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.activity_balance:
                fragmentManager.beginTransaction().hide(active).show(balanceFragment).commit();
                active = balanceFragment;
                return true;

            case R.id.activity_pairs:
                fragmentManager.beginTransaction().hide(active).show(pairFragment).commit();
                active = pairFragment;
                return true;

            case R.id.activity_graph:
                fragmentManager.beginTransaction().hide(active).show(graphFragment).commit();
                active = graphFragment;
                return true;
            case R.id.activity_history:
                fragmentManager.beginTransaction().hide(active).show(historyPagerFragment).commit();
                active = historyPagerFragment;
                return true;
        }
        return false;
    };

    @Override
    protected void onStop() {
        if (BotService.isRunning) {
            if (isBound) {
                unbindService(boundServiceConnection);
                isBound = false;
            }
        }
        super.onStop();
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
        MenuItem item = menu.getItem(0);
        toggleIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.activity_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.bot_toggle) {
            toggleBot();
            toggleIcon(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleIcon(MenuItem item) {
        if (botIsActive) {
            item.setIcon(R.drawable.outline_toggle_on_24);
        } else {
            item.setIcon(R.drawable.outline_toggle_off_24);
        }
    }

    private void toggleBot() {
        if (BotService.isRunning) {
            stopBot();
        } else {
            startBot();
        }
    }

    public void updateBot() {
        if (!botIsActive) return;
        if (!isBound) {
            Intent intent = new Intent(this, BotService.class);
            bindService(intent, boundServiceConnection, BIND_AUTO_CREATE);
            isBound = true;
        } else {
            botService.update();
        }
    }

    private void startBot() {
        Intent intent = new Intent(this, BotService.class);
        startForegroundService(intent);
        botIsActive = true;
    }

    private void stopBot() {
        Intent intent = new Intent(this, BotService.class);
        if (isBound) {
            unbindService(boundServiceConnection);
            isBound = false;
        }

        stopService(intent);
        botIsActive = false;
    }

    private void runWork() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long workPeriod = Long.valueOf(Objects.requireNonNull(preferences.getString(getString(R.string.statistic_run_period), "30")));

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MarketWorker.class, workPeriod, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("market_work", ExistingPeriodicWorkPolicy.REPLACE, workRequest);
    }


    private final ServiceConnection boundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            BotService.BotBinder binderBridge = (BotService.BotBinder) service;
            botService = binderBridge.getService();
            botService.update();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            botService = null;
            isBound = false;
        }
    };
}
