package com.chess.cryptobot.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
class BtcBalance {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id = 0
    @ColumnInfo(name = "balance", typeAffinity = ColumnInfo.REAL)
    var balance: Float = 0.0F
    @ColumnInfo(name = "dateCreated")
    var dateCreated: LocalDateTime? = null

}