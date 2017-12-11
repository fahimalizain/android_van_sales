package com.casualmill.vansales.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.casualmill.vansales.models.InvoiceItem;
import com.casualmill.vansales.models.StockTransferItem;

import java.util.List;

@Dao
public interface StockTransferItemDao {

    @Query("SELECT * FROM stock_transfer_items WHERE transfer_no = :transfer_no")
    List<StockTransferItem> get_all_for_transfer_no(int transfer_no);

    @Insert
    void InsertAll(InvoiceItem... items);

}
