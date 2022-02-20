package com.chess.cryptobot.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.IBinder
import androidx.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.chess.cryptobot.R
import com.chess.cryptobot.service.BotService
import com.chess.cryptobot.service.BotService.BotBinder
import com.chess.cryptobot.view.dialog.CryptoDialog
import com.chess.cryptobot.view.dialog.CryptoNameDialog
import com.chess.cryptobot.view.dialog.DialogListener
import com.chess.cryptobot.worker.BalanceWorker
import com.chess.cryptobot.worker.MarketWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), DialogListener {
    private val balanceFragment = BalanceFragment()
    private val pairFragment = PairsFragment()
    private val graphFragment = GraphPagerFragment()
    private val historyPagerFragment = HistoryPagerFragment()
    private val fragmentManager = supportFragmentManager
    private var active: Fragment? = null
    private var botIsActive = false
    private var botService: BotService? = null
    private var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        val navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener)
        val intent = intent
        var openPairs = false
        if (intent != null) {
            openPairs = intent.getBooleanExtra("openPairs", false)
        }
        if (openPairs) {
            fragmentManager.commit {
                add(R.id.include, pairFragment, "2")
                hide(pairFragment)
                show(pairFragment)
                add(R.id.include, balanceFragment, "1")
                hide(balanceFragment)
            }
            active = pairFragment
            navigation.selectedItemId = R.id.activity_pairs
        } else {
            fragmentManager.commit {
                add(R.id.include, pairFragment, "2")
                hide(pairFragment)
                add(R.id.include, balanceFragment, "1")
                show(balanceFragment)
            }
            active = balanceFragment
            navigation.selectedItemId = R.id.activity_balance
        }
        fragmentManager.commit {
            add(R.id.include, graphFragment, "3")
            hide(graphFragment)
            add(R.id.include, historyPagerFragment, "4")
            hide(historyPagerFragment)
        }
        botIsActive = BotService.isRunning
        updateBot()
        runWork()
    }

    private val mOnNavigationItemSelectedListener = NavigationBarView.OnItemSelectedListener { item: MenuItem ->
        when (item.itemId) {
            R.id.activity_balance -> {
                fragmentManager.commit {
                    hide(active!!)
                    show(balanceFragment)
                }
                active = balanceFragment
                return@OnItemSelectedListener true
            }
            R.id.activity_pairs -> {
                fragmentManager.commit {
                    hide(active!!)
                    show(pairFragment)
                }
                active = pairFragment
                return@OnItemSelectedListener true
            }
            R.id.activity_graph -> {
                fragmentManager.commit {
                    hide(active!!)
                    show(graphFragment)
                }
                active = graphFragment
                return@OnItemSelectedListener true
            }
            R.id.activity_history -> {
                fragmentManager.commit {
                    hide(active!!)
                    show(historyPagerFragment)
                }
                active = historyPagerFragment
                return@OnItemSelectedListener true
            }
        }
        return@OnItemSelectedListener false
    }

    override fun onStop() {
        if (BotService.isRunning) {
            if (isBound) {
                unbindService(boundServiceConnection)
                isBound = false
            }
        }
        super.onStop()
    }

    override fun onDialogPositiveClick(dialog: CryptoDialog?) {
        when (dialog) {
            is CryptoNameDialog -> {
                onCryptoNamePositiveClick(dialog)
            }
            else -> {
                throw IllegalArgumentException("Unknown type of " + (dialog?.javaClass?.canonicalName
                        ?: dialog.toString()))
            }
        }
    }

    private fun onCryptoNamePositiveClick(dialog: CryptoNameDialog) {
        val balanceHolder = dialog.balanceHolder
        val nameDialogView = dialog.dialog?.findViewById<EditText>(R.id.name_dialog_edit_text)
        val coinName = nameDialogView?.text.toString()
        if (coinName.isNotEmpty()) balanceHolder.add(coinName)
        updateBot()
    }

    override fun onDialogNegativeClick(dialog: CryptoDialog?) {
        dialog!!.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu.getItem(0)
        toggleIcon(item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.activity_settings) {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (id == R.id.bot_toggle) {
            toggleBot()
            toggleIcon(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleIcon(item: MenuItem) {
        if (botIsActive) {
            item.setIcon(R.drawable.outline_toggle_on_24)
        } else {
            item.setIcon(R.drawable.outline_toggle_off_24)
        }
    }

    private fun toggleBot() {
        if (BotService.isRunning) {
            stopBot()
        } else {
            startBot()
        }
    }

    fun updateBot() {
        if (!botIsActive) return
        if (!isBound) {
            val intent = Intent(this, BotService::class.java)
            bindService(intent, boundServiceConnection, Context.BIND_AUTO_CREATE)
            isBound = true
        } else {
            botService?.update()
        }
    }

    private fun startBot() {
        val intent = Intent(this, BotService::class.java)
        startForegroundService(intent)
        botIsActive = true
    }

    private fun stopBot() {
        val intent = Intent(this, BotService::class.java)
        if (isBound) {
            unbindService(boundServiceConnection)
            isBound = false
        }
        stopService(intent)
        botIsActive = false
    }

    private fun runWork() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val workPeriod = preferences.getString(getString(R.string.statistic_run_period), "30")?.toLong()
                ?: 30
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        val marketWorkRequest = PeriodicWorkRequest.Builder(MarketWorker::class.java, workPeriod, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
        val balanceWorkRequest = PeriodicWorkRequest.Builder(BalanceWorker::class.java, workPeriod, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
        val manager = WorkManager.getInstance(this)
        manager.enqueueUniquePeriodicWork("market_work", ExistingPeriodicWorkPolicy.REPLACE, marketWorkRequest)
        manager.enqueueUniquePeriodicWork("balance_work", ExistingPeriodicWorkPolicy.REPLACE, balanceWorkRequest)
    }

    private val boundServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binderBridge = service as BotBinder
            botService = binderBridge.service
            botService!!.update()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            botService = null
            isBound = false
        }
    }
}