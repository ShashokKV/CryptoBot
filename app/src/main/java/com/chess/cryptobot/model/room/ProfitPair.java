package com.chess.cryptobot.model.room;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity
public class ProfitPair implements Comparable<ProfitPair>{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = 3)
    private int id;
    @ColumnInfo(name = "pairName", typeAffinity = 2)
    private String pairName;
    @ColumnInfo(name = "percent", typeAffinity = 4)
    private Float percent;
    @ColumnInfo(name = "dateCreated")
    private LocalDateTime dateCreated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public Float getPercent() {
        return percent;
    }

    public void setPercent(Float percent) {
        this.percent = percent;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public int compareTo(ProfitPair pair) {
        return Math.round((pair.getPercent() - this.getPercent())*100);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null) return false;
        if (obj.equals(this)) return true;

        if (obj instanceof ProfitPair) {
            return this.getPairName().equals(((ProfitPair) obj).getPairName());
        }else if(obj instanceof String) {
            return this.getPairName().equals(obj);
        }else {
            return false;
        }
    }
}
