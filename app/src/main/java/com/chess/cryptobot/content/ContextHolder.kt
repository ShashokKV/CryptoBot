package com.chess.cryptobot.content

import android.content.Context
import androidx.fragment.app.Fragment
import com.chess.cryptobot.exceptions.ItemNotFoundException
import com.chess.cryptobot.model.ViewItem
import com.chess.cryptobot.view.MainFragment

abstract class ContextHolder protected constructor(private val fr: Fragment) {
    var viewItems: MutableList<ViewItem> = ArrayList()
    val context: Context = fr.requireContext()

    lateinit var prefs: Preferences

    protected val mainFragment: MainFragment<*>
        get() = this.fr as MainFragment<*>

    open fun init(): ContextHolder {
        initViewItems(initPrefs(context).items)
        return this
    }

    protected abstract fun initPrefs(context: Context): Preferences

    protected abstract fun initViewItems(itemNamesSet: HashSet<String>?): MutableList<ViewItem>

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
        val invalidItems = ArrayList(this.viewItems)
        this.viewItems.retainAll(viewItems)

        invalidItems.forEach { viewItem ->
            if (!this.viewItems.contains(viewItem)) {
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