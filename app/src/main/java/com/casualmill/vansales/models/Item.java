package com.casualmill.vansales.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "items")
public class Item {

    @PrimaryKey
    @ColumnInfo(name = "item_code")
    public String itemCode;

    @ColumnInfo(name = "item_name")
    public String itemName;

    public String barcode;
}
