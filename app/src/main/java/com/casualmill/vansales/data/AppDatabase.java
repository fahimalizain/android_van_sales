package com.casualmill.vansales.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.casualmill.vansales.models.Invoice;
import com.casualmill.vansales.models.InvoiceItem;
import com.casualmill.vansales.models.Item;

/**
 * Created by faztp on 11-Dec-17.
 */

@Database(entities = {Invoice.class, InvoiceItem.class, Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static AppDatabase Instance;

    public static AppDatabase getAppDatabase(Context ctx) {
        if (Instance == null)
            Instance = Room.databaseBuilder(ctx, AppDatabase.class, "van_database").build();

        return Instance;
    }

    public static void destroyAppDatabase() {
        Instance = null;
    }
}
