package com.casualmill.vansales.data;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by faztp on 11-Dec-17.
 */

public class Converters {

    @TypeConverter
    public static Date dateFromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
