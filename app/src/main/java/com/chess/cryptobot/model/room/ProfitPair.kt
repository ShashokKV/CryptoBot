package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class ProfitPair (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id: Int = 0,
    @ColumnInfo(name = "pairName", typeAffinity = ColumnInfo.TEXT)
    var pairName: String,
    @ColumnInfo(name = "percent", typeAffinity = ColumnInfo.REAL)
    var percent: Float,
    @ColumnInfo(name = "dateCreated")
    var dateCreated: LocalDateTime
) {

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
        return pairName.hashCode()
    }
}