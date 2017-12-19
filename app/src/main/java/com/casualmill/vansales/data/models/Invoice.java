package com.casualmill.vansales.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by faztp on 11-Dec-17.
 */

@Entity(tableName="invoices")
public class Invoice implements Serializable {

    @PrimaryKey()
    @NonNull
    public String invoice_no;

    public Date date;

    public String customer_name;

    public float discount;
    public float grand_total;
}
