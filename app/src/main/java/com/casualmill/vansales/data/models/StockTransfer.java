package com.casualmill.vansales.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by faztp on 11-Dec-17.
 */

@Entity(tableName = "stock_transfers")
public class StockTransfer {

    @PrimaryKey
    @ColumnInfo(name = "transfer_no")
    public int transferNo;

    public Date date;
}
