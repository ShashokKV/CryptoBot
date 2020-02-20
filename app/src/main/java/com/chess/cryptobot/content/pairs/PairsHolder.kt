package com.chess.cryptobot.content.pairs

import android.content.Context
import androidx.fragment.app.Fragment
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.content.Preferences
import com.chess.cryptobot.content.balance.BalancePreferences
import com.chess.cryptobot.model.Pair
import com.chess.cryptobot.model.ViewItem
import com.chess.cryptobot.task.AvailablePairsTask
import com.chess.cryptobot.task.PairsUpdateTask
import com.chess.cryptobot.task.SerialExecutor
import java.util.*

class PairsHolder(fr: Fragment) : ContextHolder(fr) {
    private var invalidPairs: MutableList<String> = ArrayList()
    private var availablePairs: MutableList<String> = ArrayList()
    private var bittrexVolumes: Map<String, Double> = HashMap()
    private var binanceVolumes: Map<String, Double> = HashMap()
    private var negativePercentPairs: MutableList<String> = ArrayList()
    private var allPairsPrefs: AllPairsPreferences? = null
    private val serialExecutor: SerialExecutor = SerialExecutor()

    fun initAvailablePairs() {
        val availablePairsTask = AvailablePairsTask(this)
        availablePairsTask.executeOnExecutor(serialExecutor, 0)
    }

    public override fun initPrefs(context: Context?): Preferences {
        allPairsPrefs = AllPairsPreferences(context)
        return PairsPreferences(context)
    }

    public override fun initViewItems(itemNamesSet: Set<String>):MutableList<ViewItem> {
        itemNamesSet.forEach { pairName -> if (isValidPair(pairName)) addItemToList(Pair.fromPairName(pairName)) }
        return viewItems
    }

    fun updateFromBalance() {
        val balancePreferences = BalancePreferences(context)
        val coinNames = balancePreferences.items
        val balancePairs = ArrayList<ViewItem>()
        for (baseName in coinNames) {
            for (marketName in coinNames) {
                if (baseName != marketName) {
                    val pair = Pair(baseName, marketName)
                    val pairName = pair.name
                    if (isValidPair(pairName) && !negativePercentPairs.contains(pairName)) {
                        balancePairs.add(pair)
                    }
                }
            }
        }
        removeIfNotExists(balancePairs)
        addIfNotExists(balancePairs)
    }

    private fun isValidPair(pairName: String): Boolean {
        return if (this.availablePairs.isEmpty()) {
            !invalidPairs.contains(pairName)
        } else {
            availablePairs.contains(pairName) && !invalidPairs.contains(pairName)
        }
    }

    private fun addIfNotExists(balancePairs: List<ViewItem>) {
        balancePairs.forEach { tradingPair ->
            if (!viewItems.contains(tradingPair)) {
                add(tradingPair)
            }
        }
    }

    private fun removeIfNotExists(balancePairs: List<ViewItem>) {
        this.retainAll(balancePairs)
    }

    fun removeInvalidPairs() {
        val pairs = ArrayList<ViewItem>()
        availablePairs.forEach { pairName -> pairs.add(Pair.fromPairName(pairName)) }
        this.retainAll(pairs)
    }

    fun setVolumes(bittrexVolumes: Map<String, Double>, binanceVolumes: Map<String, Double>) {
        this.bittrexVolumes = bittrexVolumes
        this.binanceVolumes = binanceVolumes
    }

    public override fun updateItem(item: ViewItem) {
        val pair = item as Pair
        pair.binanceVolume = binanceVolumes[pair.name]?: 0.0
        pair.bittrexVolume = bittrexVolumes[pair.name]?: 0.0
        val task = PairsUpdateTask(this)
        task.executeOnExecutor(serialExecutor, pair)
    }

    fun addToInvalidPairs(pair: Pair) {
        val pairName = pair.name
        if (!invalidPairs.contains(pairName)) invalidPairs.add(pairName)
    }

    fun addToNegativePercentPairs(pair: Pair) {
        val pairName = pair.name
        if (!negativePercentPairs.contains(pairName)) negativePercentPairs.add(pairName)
    }

    fun resetNegativePercentPairs() {
        this.negativePercentPairs = ArrayList()
    }

    fun setAvailablePairs(pairs: MutableList<String>?) {
        if (pairs != null) {
            availablePairs = pairs
            allPairsPrefs!!.items = HashSet(pairs)
        }
    }
}