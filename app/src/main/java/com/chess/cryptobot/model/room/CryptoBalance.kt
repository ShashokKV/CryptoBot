package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class CryptoBalance (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id:Int = 0,
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    var name: String,
    @ColumnInfo(name = "balance", typeAffinity = ColumnInfo.REAL)
    var balance: Float,
    @ColumnInfo(name = "dateCreated")
    var dateCreated: LocalDateTime
)