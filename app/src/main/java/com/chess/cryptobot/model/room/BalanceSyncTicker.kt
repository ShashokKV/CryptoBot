package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
class BalanceSyncTicker {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id = 0

    @ColumnInfo(name = "coinName")
    var coinName: String? = null

    @ColumnInfo(name = "marketName")
    var marketName: String? = null

    @ColumnInfo(name = "amount", typeAffinity = ColumnInfo.REAL)
    var amount: Double? = null

    @ColumnInfo(name = "dateCreated")
    var dateCreated: LocalDateTime? = null
}