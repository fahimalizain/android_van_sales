package com.casualmill.vansales.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by faztp on 11-Dec-17.
 */

@Entity(tableName = "stock_transfer_items",
        indices = {@Index("transfer_no")},
        foreignKeys = @ForeignKey(entity = StockTransfer.class,
        parentColumns = "transfer_no", childColumns = "transfer_no",
        onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class StockTransferItem {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "transfer_no")
    public int transferNo;

    @ColumnInfo(name = "item_code")
    public String itemCode;

    public float qty;

}
