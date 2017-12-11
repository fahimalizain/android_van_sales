package com.casualmill.vansales.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "stock_entries")
public class StockEntry {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "item_code")
    public String itemCode;

    public float qty;
}
