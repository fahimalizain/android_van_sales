package com.casualmill.vansales.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.casualmill.vansales.data.AppDatabase;
import com.casualmill.vansales.data.models.Invoice;
import com.casualmill.vansales.data.models.InvoiceItem;

import java.util.List;

@Dao
public abstract class InvoiceDao {

    @Query("SELECT * FROM invoices")
    public abstract List<Invoice> getAll();

    @Query("SELECT * FROM invoices WHERE invoice_no = :invoice_no LIMIT 1")
    public abstract Invoice get_invoice(int invoice_no);

    @Insert
    public abstract void Insert(Invoice invoice);

    @Update
    public abstract void Update(Invoice invoice);

    @Delete
    public abstract void Delete(Invoice invoice);

    @Transaction
    public void Insert(Invoice invoice, InvoiceItem[] invoices, Boolean update) {
        if (update) {
            AppDatabase.Instance.invoiceItemDao().Delete(invoice.invoice_no);
            Update(invoice);
        } else
            Insert(invoice);
        AppDatabase.Instance.invoiceItemDao().InsertAll(invoices);
    }
}
