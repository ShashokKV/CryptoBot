package com.chess.cryptobot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chess.cryptobot.R;
import com.chess.cryptobot.adapter.BalanceAdapter;
import com.chess.cryptobot.callback.SwipeBalanceCallback;
import com.chess.cryptobot.dialog.CryptoNameDialog;
import com.chess.cryptobot.content.ContextHolder;
import com.chess.cryptobot.service.BalanceUpdateService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class BalanceActivity extends AppCompatActivity
        implements CryptoNameDialog.CoinNameDialogListener {

    private RecyclerView balanceRecyclerView;
    private BalanceUpdateService mService;
    private ContextHolder contextHolder;
    private BalanceAdapter balanceAdapter;
    private boolean mBound;

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
        contextHolder = new ContextHolder(this);
        initRecyclerView();
        //initBalanceService();
    }

    private void initRecyclerView() {
        balanceAdapter = new BalanceAdapter(contextHolder);
        balanceRecyclerView.setAdapter(balanceAdapter);
        balanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeBalanceCallback(contextHolder));
        itemTouchHelper.attachToRecyclerView(balanceRecyclerView);
    }

/*
    private void initBalanceService() {
        Intent intent = new Intent(this, BalanceUpdateService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
*/
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText nameDialogView = Objects.requireNonNull(dialog.getDialog())
                .findViewById(R.id.name_dialog_edit_text);
        String coinName = nameDialogView.getText().toString();
        if (!coinName.isEmpty()) contextHolder.add(coinName);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
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
/*
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BalanceUpdateService.BalanceBinder binder = (BalanceUpdateService.BalanceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };*/

    public BalanceAdapter getBalanceAdapter() {
        return balanceAdapter;
    }
}