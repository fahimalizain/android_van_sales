package com.casualmill.vansales.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.casualmill.vansales.models.Invoice;
import com.casualmill.vansales.models.StockEntry;
import com.casualmill.vansales.models.StockTransfer;

import java.util.List;

@Dao
public interface StockTransferDao {

    @Query("SELECT * FROM stock_transfers")
    List<StockTransfer> getAll();

    @Query("SELECT * FROM stock_transfers WHERE transfer_no = :transfer_no LIMIT 1")
    Invoice get_stockTransfer(int transfer_no);

    @Insert
    void Insert(StockTransfer st);

    @Delete
    void Delete(StockTransfer st);
}
