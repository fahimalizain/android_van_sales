package com.casualmill.vansales.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by faztp on 11-Dec-17.
 */

@Entity(tableName = "invoice_items",
        foreignKeys = @ForeignKey(entity = Invoice.class,
                parentColumns = "invoice_no", childColumns = "invoice_no",
                onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class InvoiceItem {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "invoice_no")
    public int invoiceNo;

    @ColumnInfo(name = "item_code")
    public String itemCode;

    public float qty;

    public float price;
}
