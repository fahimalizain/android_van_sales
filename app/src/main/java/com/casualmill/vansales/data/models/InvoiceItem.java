package com.casualmill.vansales.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

/**
 * Created by faztp on 11-Dec-17.
 */

@Entity(tableName = "invoice_items",
        indices = {@Index("invoice_no"), @Index("item_code")},
        foreignKeys = {
                        @ForeignKey(entity = Invoice.class,
                            parentColumns = "invoice_no", childColumns = "invoice_no",
                            onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
                        @ForeignKey(entity = Item.class,
                                parentColumns = "item_code", childColumns = "item_code",
                                onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
        }, inheritSuperIndices = false) // dont want item_code unique here
public class InvoiceItem extends Item {

    @ColumnInfo(name = "invoice_no")
    public String invoiceNo;

    public float qty;

    public float price;
}
