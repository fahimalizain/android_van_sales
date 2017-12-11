package com.casualmill.vansales.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.casualmill.vansales.models.Invoice;

import java.util.List;

@Dao
public interface InvoiceDao {

    @Query("SELECT * FROM invoices")
    List<Invoice> getAll();

    @Query("SELECT * FROM invoices WHERE invoice_no = :invoice_no LIMIT 1")
    Invoice get_invoice(int invoice_no);

    @Insert
    void Insert(Invoice invoice);

    @Delete
    void Delete(Invoice invoice);
}
