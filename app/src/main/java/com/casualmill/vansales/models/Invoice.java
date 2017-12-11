package com.casualmill.vansales.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by faztp on 11-Dec-17.
 */

@Entity(tableName="invoices")
public class Invoice {

    @PrimaryKey(autoGenerate = true)
    public int invoice_no;

    public Date date;
}
