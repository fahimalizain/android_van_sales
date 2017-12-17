package com.casualmill.vansales.data.models;

import android.arch.persistence.room.Entity;

/**
 * Created by faztp on 17-Dec-17.
 */

@Entity(tableName = "uom")
public class UOM {
    public String Unit;
    public String item_code;
    public float conversion_factor;
    public float price;
}
