package com.casualmill.vansales.data;

import com.casualmill.vansales.data.models.Item;
import com.casualmill.vansales.data.models.UOM;

import java.util.List;

/**
 * Created by faztp on 17-Dec-17.
 */

public class DataHelper {

    public static void fillItem(Item item) {
        item.UnitDetails = AppDatabase.Instance.uomDao().get_item_units(item.itemCode);
    }
}
