package com.casualmill.vansales.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.casualmill.vansales.data.models.StockEntry;

/**
 * Created by faztp on 11-Dec-17.
 */

@Dao
public interface StockEntryDao {

    @Query("SELECT SUM(qty) FROM stock_entries WHERE item_code = :item_code")
    float getItemQty(String item_code);

    @Insert
    void InsertAll(StockEntry... entries);

    @Delete
    void DeleteAll(StockEntry... entries);
}
