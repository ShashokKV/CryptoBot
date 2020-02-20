package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
class ProfitPair {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id = 0
    @ColumnInfo(name = "pairName", typeAffinity = ColumnInfo.TEXT)
    var pairName: String? = null
    @ColumnInfo(name = "percent", typeAffinity = ColumnInfo.REAL)
    var percent: Float? = null
    @ColumnInfo(name = "dateCreated")
    var dateCreated: LocalDateTime? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other == this) return true
        return when (other) {
            is ProfitPair -> {
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
        return pairName?.hashCode() ?: 0
    }
}