package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CoinInfo (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id: Int = 0,
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    var name: String?,
    @ColumnInfo(name = "marketName", typeAffinity = ColumnInfo.TEXT)
    var marketName: String,
    @ColumnInfo(name = "status", typeAffinity = ColumnInfo.INTEGER)
    var status: Boolean,
    @ColumnInfo(name = "fee", typeAffinity = ColumnInfo.REAL)
    var fee: Double
) {
    constructor(name: String? ,marketName: String) : this(0, name, marketName, true, 0.0)
}