package com.chess.cryptobot.content.history

import android.content.Context
import androidx.fragment.app.Fragment
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.Preferences
import com.chess.cryptobot.model.ViewItem
import com.chess.cryptobot.task.HistoryTask

class HistoryHolder(fragment: Fragment, private val state: State) : ContextHolder(fragment) {

    init {
        init()
        updateAllItems()
    }

    override fun initPrefs(context: Context): Preferences {
        prefs = HistoryPreferences(context)
        return prefs
    }

    override fun initViewItems(itemNamesSet: HashSet<String>?):MutableList<ViewItem> {
        return viewItems
    }

    override fun updateAllItems() {
        viewItems = ArrayList()
        val task = HistoryTask(this, state)
        task.doInBackground(0)
    }

    override fun updateItem(item: ViewItem) {

    }

    enum class State {
        HISTORY,
        ORDERS
    }
}
