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

class BalanceHolder(fragment: Fragment) : ContextHolder(fragment) {
    private var hasKeys: Boolean = false
    private var bittrexStatuses: Map<String, Boolean> = HashMap()
    private var binanceStatuses: Map<String, Boolean> = HashMap()
    private var poloniexStatuses: Map<String, Boolean> = HashMap()
    private var iconUrls: Map<String, String?> = HashMap()
    var availableCoins = ArrayList<String>()

    init {
        super.init()
        hasKeys = checkIfHasKeys()
    }

    private fun checkIfHasKeys(): Boolean {
        for (market in MarketFactory.getInstance(context).getMarkets()) {
            if (market.keysIsEmpty()) {
                return false
            }
        }
        return true
    }

    public override fun initPrefs(context: Context): Preferences {
        prefs = BalancePreferences(context)
        return prefs
    }

    public override fun initViewItems(itemNamesSet: HashSet<String>?):MutableList<ViewItem> {
        itemNamesSet?.forEach { coinName -> addItemToList(Balance(coinName)) }
        return viewItems
    }

    fun add(coinName: String) {
        val balance = Balance(coinName)
        if (!viewItems.contains(balance))
            add(Balance(coinName))
    }

    override fun add(viewItem: ViewItem) {
        super.add(viewItem)
        updateItem(viewItem)
    }

    public override fun updateItem(item: ViewItem) {
        val balance = item as Balance
        balance.setStatuses(binanceStatuses[balance.name]?: true,
                bittrexStatuses[balance.name]?: true,
                poloniexStatuses[balance.name]?: true)
        balance.coinUrl = iconUrls[balance.name]
        updateImage(balance)
        if (hasKeys) updateAmount(balance)
    }

    private fun updateImage(balance: Balance) {
        val task = CoinImageTask(this)
        task.doInBackground(balance)
    }

    private fun updateAmount(balance: Balance) {
        val task = BalanceUpdateTask(this)
        task.doInBackground(balance)
    }

    @Throws(ItemNotFoundException::class)
    fun getBalanceByPosition(position: Int): Balance {
        val balanceActivity = mainFragment
        val coinName = balanceActivity.itemNameByPosition(position)
        return this.getItemByName(coinName) as Balance
    }

    fun setCurrencyStatus(bittrexStatuses: Map<String, Boolean>, binanceStatuses: Map<String, Boolean>, poloniexStatuses: Map<String, Boolean>) {
        this.bittrexStatuses = bittrexStatuses
        this.binanceStatuses = binanceStatuses
        this.poloniexStatuses = poloniexStatuses
    }

    fun setIconUrls(urls: Map<String, String?>) {
        this.iconUrls = urls
    }
}
