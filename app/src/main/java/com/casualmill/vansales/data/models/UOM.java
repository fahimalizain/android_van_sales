package com.casualmill.vansales.data.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by faztp on 17-Dec-17.
 */

@Entity(tableName = "uom")
public class UOM implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String unit_code;
    public String unit_name;
    public String item_code;
    public float conversion_factor;
    public float price;

    @Override
    public String toString() {
        return unit_name;
    }
}
