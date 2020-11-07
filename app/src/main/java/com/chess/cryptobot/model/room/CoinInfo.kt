package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class CoinInfo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id = 0

    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    var name: String? = null

    @ColumnInfo(name = "marketName", typeAffinity = ColumnInfo.TEXT)
    var marketName: String? = null

    @ColumnInfo(name = "status", typeAffinity = ColumnInfo.INTEGER)
    var status: Boolean = true

    @ColumnInfo(name = "fee", typeAffinity = ColumnInfo.REAL)
    var fee: Double = 0.0
}