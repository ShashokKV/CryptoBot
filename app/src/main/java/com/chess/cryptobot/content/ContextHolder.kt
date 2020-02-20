package com.chess.cryptobot.content

import android.content.Context
import androidx.fragment.app.Fragment
import com.chess.cryptobot.exceptions.ItemNotFoundException
import com.chess.cryptobot.model.ViewItem
import com.chess.cryptobot.view.MainFragment
import java.util.*

abstract class ContextHolder protected constructor(private val fr: Fragment) {
    val viewItems: MutableList<ViewItem> by lazy { initViewItems(prefs.items) }

    val prefs: Preferences by lazy { initPrefs(fr.context) }

    val context: Context?
        get() = this.fr.context

    protected val mainFragment: MainFragment<*>
        get() = this.fr as MainFragment<*>

    protected abstract fun initPrefs(context: Context?): Preferences

    protected abstract fun initViewItems(itemNamesSet: Set<String>): MutableList<ViewItem>

    open fun add(viewItem: ViewItem) {
        addItemToList(viewItem)
        mainFragment.addItem()
        val preferences = prefs
        preferences.addItem(viewItem.name)
    }

    protected fun addItemToList(viewItem: ViewItem) {
        if (viewItems.contains(viewItem)) return
        viewItems.add(viewItem)
    }

    protected fun retainAll(viewItems: List<ViewItem>) {
        val invalidItems = ArrayList(viewItems)
        this.viewItems.retainAll(viewItems)

        invalidItems.forEach { viewItem ->
            if (!viewItems.contains(viewItem)) {
                removeFromPrefs(viewItem)
            }
        }
        mainFragment.updateAllItems()
    }

    private fun removeFromAdapter(viewItem: ViewItem) {
        mainFragment.deleteItemByPosition(viewItems.indexOf(viewItem))
    }

    private fun removeFromPrefs(viewItem: ViewItem) {
        prefs.removeItem(viewItem.name)
    }

    fun remove(item: ViewItem?) {
        if (item == null) return
        removeFromAdapter(item)
        viewItems.remove(item)
        removeFromPrefs(item)
    }

    fun setItem(updatedItem: ViewItem?) {
        for (item in viewItems) {
            if (item == updatedItem) {
                viewItems[viewItems.indexOf(item)] = updatedItem
                break
            }
        }

        mainFragment.updateItem(updatedItem)
    }

    open fun updateAllItems() {
        viewItems.forEach { viewItem -> this.updateItem(viewItem) }
    }

    protected abstract fun updateItem(item: ViewItem)

    @Throws(ItemNotFoundException::class)
    protected fun getItemByName(itemName: String): ViewItem {
        for (item in viewItems) {
            if (item.name == itemName) return item
        }
        throw ItemNotFoundException(itemName)
    }

    fun makeToast(message: String) {
        this.mainFragment.makeToast(message)
    }

    fun showSpinner() {
        this.mainFragment.showSpinner()
    }

    fun hideSpinner() {
        this.mainFragment.hideSpinner()
    }
}