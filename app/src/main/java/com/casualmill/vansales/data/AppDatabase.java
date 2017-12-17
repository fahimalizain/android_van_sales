package com.casualmill.vansales.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.casualmill.vansales.data.dao.InvoiceDao;
import com.casualmill.vansales.data.dao.InvoiceItemDao;
import com.casualmill.vansales.data.dao.ItemDao;
import com.casualmill.vansales.data.dao.UOMDao;
import com.casualmill.vansales.data.models.Invoice;
import com.casualmill.vansales.data.models.InvoiceItem;
import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.data.models.UOM;

/**
 * Created by faztp on 11-Dec-17.
 */

@Database(entities = {Invoice.class, InvoiceItem.class,
                        Item.class, UOM.class,
            }, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase Instance;

    public abstract ItemDao itemDao();
    public abstract InvoiceDao invoiceDao();
    public abstract InvoiceItemDao invoiceItemDao();
    public abstract UOMDao uomDao();

    public static AppDatabase getAppDatabase(Context ctx) {
        if (Instance == null)
            Instance = Room.databaseBuilder(ctx, AppDatabase.class, "van_database").build();

        return Instance;
    }

    public static void destroyAppDatabase() {
        Instance = null;
    }
}
