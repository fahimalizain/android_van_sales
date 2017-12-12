package com.casualmill.vansales.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;


@Entity(tableName = "items")
public class Item implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "item_code")
    @NonNull
    public String itemCode;

    @ColumnInfo(name = "item_name")
    public String itemName;

    public String barcode;

    public float price;

    @ColumnInfo(name = "stock_balance")
    public float stock_balance;
}
