package com.casualmill.vansales.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.casualmill.vansales.data.models.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT *, (SELECT SUM(qty) FROM stock_entries WHERE item_code = i.item_code) as stock_balance FROM items i")
    List<Item> getAll();

    @Query("DELETE FROM items")
    void DeleteAll();

    @Insert
    void Insert(Item item);

    @Delete
    void Delete(Item item);
}
