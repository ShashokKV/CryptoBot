package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class BalanceSyncTicker (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id: Int = 0,
    @ColumnInfo(name = "coinName")
    var coinName: String?,
    @ColumnInfo(name = "marketName")
    var marketName: String?,

    @ColumnInfo(name = "amount", typeAffinity = ColumnInfo.REAL)
    var amount: Double?,
    @ColumnInfo(name = "dateCreated")
    var dateCreated: LocalDateTime?
)