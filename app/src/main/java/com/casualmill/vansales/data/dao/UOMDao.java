package com.casualmill.vansales.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.casualmill.vansales.data.models.UOM;

import java.util.List;

@Dao
public interface UOMDao {

    @Query("SELECT * FROM uom")
    List<UOM> getAll();

    @Query("SELECT * FROM uom WHERE item_code = :item_code")
    List<UOM> get_item_units(String item_code);

    @Insert
    void Insert(UOM st);

    @Delete
    void Delete(UOM st);
}
