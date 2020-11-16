package com.chess.cryptobot.content

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.core.content.edit
import kotlin.collections.HashSet

abstract class Preferences protected constructor(context: Context) {
    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val preferenceKey: String by lazy { initPrefKey(context) }

    var items: HashSet<String>?
        get() = sharedPreferences.getStringSet(preferenceKey, HashSet())?.toHashSet()
        set(items) = updateItemSet(items)

    protected abstract fun initPrefKey(context: Context): String

    open fun addItem(itemName: String) {
        val itemsSet = items
        if (itemsSet != null) {
            if (itemsSet.contains(itemName)) return
        }
        itemsSet?.add(itemName)
        updateItemSet(itemsSet)
    }

    internal fun removeItem(itemName: String) {
        val itemsSet = items
        itemsSet?.remove(itemName)
        updateItemSet(itemsSet)
    }

    private fun updateItemSet(itemsSet: HashSet<String>?) {
        sharedPreferences.edit { putStringSet(preferenceKey, itemsSet) }
        val editor = sharedPreferences.edit()
        editor.putStringSet(preferenceKey, itemsSet)
        editor.apply()
    }
}
