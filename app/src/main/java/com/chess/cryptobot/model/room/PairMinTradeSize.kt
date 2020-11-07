package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class PairMinTradeSize {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id: Int = 0

    @ColumnInfo(name = "pairName", typeAffinity = ColumnInfo.TEXT)
    var pairName: String? = null

    @ColumnInfo(name = "minTradeSize")
    var minTradeSize: Double = 0.0

    @ColumnInfo(name = "stepSize")
    var stepSize: Double? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other == this) return true
        return when (other) {
            is PairMinTradeSize -> {
                pairName == other.pairName
            }
            is String -> {
                pairName == other
            }
            else -> {
                false
            }
        }
    }

    override fun hashCode(): Int {
        return pairName.hashCode() ?: 0
    }
}