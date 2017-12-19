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

    public static String toString(float f, int places) {
        return String.format(Locale.ENGLISH, "%." + places +"f", f);
    }

    public static Float fromString(String s) {
        return s.isEmpty() ? 0 : Float.valueOf(s);
    }

    public static Float roundFloat(float f, int places) {
        // math.pow returns double
        return Math.round( f * (float)Math.pow(10, places)) / ((float) Math.pow(10, places));
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
