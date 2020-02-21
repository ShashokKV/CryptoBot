package com.chess.cryptobot.content.balance

import android.content.Context
import androidx.fragment.app.Fragment
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.Preferences
import com.chess.cryptobot.exceptions.ItemNotFoundException
import com.chess.cryptobot.market.MarketFactory
import com.chess.cryptobot.model.Balance
import com.chess.cryptobot.model.ViewItem
import com.chess.cryptobot.task.BalanceUpdateTask
import com.chess.cryptobot.task.CoinImageTask
import com.chess.cryptobot.task.SerialExecutor
import java.util.*

class BalanceHolder(fragment: Fragment) : ContextHolder(fragment) {
    private var hasKeys: Boolean = false
    private var bittrexStatuses: Map<String, Boolean> = HashMap()
    private var binanceStatuses: Map<String, Boolean> = HashMap()
    private var iconUrls: Map<String, String>? = null
    private val serialExecutor: SerialExecutor = SerialExecutor()

    private fun checkIfHasKeys(): Boolean {
        val factory = MarketFactory()
        for (market in factory.getMarkets(this)) {
            if (market?.keysIsEmpty() != false) {
                return false
            }
        }
        return true
    }

    public override fun initPrefs(context: Context?): Preferences {
        return BalancePreferences(context)
    }

    public override fun initViewItems(itemNamesSet: Set<String>):MutableList<ViewItem> {
        itemNamesSet.forEach { coinName -> addItemToList(Balance(coinName)) }
        return viewItems
    }

    override fun init(): ContextHolder {
        super.init()
        hasKeys = checkIfHasKeys()
        return this
    }

    fun add(coinName: String) {
        val balance = Balance(coinName)
        if (!viewItems.contains(balance))
            add(Balance(coinName))
    }

    override fun add(viewItem: ViewItem) {
        super.add(viewItem)
        val balance = viewItem as Balance
        balance.setStatuses(binanceStatuses[balance.name]?: false, bittrexStatuses[balance.name]?: false)
        if (iconUrls != null) balance.coinUrl = iconUrls!![balance.name]
        updateImage(balance)
        if (hasKeys) updateAmount(balance)
    }

    public override fun updateItem(item: ViewItem) {
        val balance = item as Balance
        balance.setStatuses(binanceStatuses[balance.name]?: false, bittrexStatuses[balance.name]?: false)
        if (iconUrls != null) balance.coinUrl = iconUrls!![balance.name]
        updateImage(balance)
        if (hasKeys) updateAmount(balance)
    }

    private fun updateImage(balance: Balance) {
        val task = CoinImageTask(this)
        task.executeOnExecutor(serialExecutor, balance)
    }

    private fun updateAmount(balance: Balance) {
        val task = BalanceUpdateTask(this)
        task.executeOnExecutor(serialExecutor, balance)
    }

    @Throws(ItemNotFoundException::class)
    fun getBalanceByPosition(position: Int): Balance {
        val balanceActivity = mainFragment
        val coinName = balanceActivity.itemNameByPosition(position)
        return this.getItemByName(coinName) as Balance
    }

    fun setMinBalance(coinName: String, minBalance: Double) {
        val preferences = prefs as BalancePreferences
        preferences.setMinBalance(coinName, minBalance)
    }

    fun setCurrencyStatus(bittrexStatuses: Map<String, Boolean>, binanceStatuses: Map<String, Boolean>) {
        this.bittrexStatuses = bittrexStatuses
        this.binanceStatuses = binanceStatuses
    }

    fun setIconUrls(urls: Map<String, String>) {
        this.iconUrls = urls
    }
}
