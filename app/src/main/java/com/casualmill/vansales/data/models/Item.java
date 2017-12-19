package com.casualmill.vansales.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;


@Entity(tableName = "items",
        indices = {@Index(value = "item_code", unique = true)})
public class Item implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int Uid;

    @ColumnInfo(name = "item_code")
    @NonNull
    public String itemCode;

    @ColumnInfo(name = "item_name")
    public String itemName;

    public String barcode;

    public String default_uom;

    @Ignore
    public List<UOM> UnitDetails;

    @ColumnInfo(name = "stock_balance")
    public float stock_balance;
}
