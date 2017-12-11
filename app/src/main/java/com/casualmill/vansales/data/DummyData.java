package com.casualmill.vansales.data;

import com.casualmill.vansales.data.models.Item;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by faztp on 11-Dec-17.
 */

public class DummyData {

    // Deletes Existing Items and adds dummy
    public static void AddDummyItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.Instance.itemDao().DeleteAll();

                Random r  = new Random();
                for (int i = 1; i < 40; i++) {
                    Item t = new Item();
                    t.itemCode = "ITM00" + i;
                    t.itemName = "Item Name " + i;
                    t.barcode = String.valueOf(i * r.nextInt(1000) * 20);
                    t.price = (200 - i) % (r.nextInt(10) + 5) + i * 50;
                    AppDatabase.Instance.itemDao().Insert(t);
                }
            }
        }).start();
    }

}
