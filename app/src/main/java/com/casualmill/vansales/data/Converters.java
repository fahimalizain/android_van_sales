package com.casualmill.vansales.data;

import android.arch.persistence.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by faztp on 11-Dec-17.
 */

public class Converters {

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    }

    public static String toCurrency(float f) {
        return String.format(Locale.ENGLISH, "$%.2f", f);
    }

    @TypeConverter
    public static Date dateFromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
