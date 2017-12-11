package com.casualmill.vansales.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "items")
public class Item {

    @PrimaryKey
    @ColumnInfo(name = "item_code")
    @NonNull
    public String itemCode;

    @ColumnInfo(name = "item_name")
    public String itemName;

    public String barcode;

    public float price;

    @Ignore
    @ColumnInfo(name = "stock_balance")
    public float stock_balance;
}
