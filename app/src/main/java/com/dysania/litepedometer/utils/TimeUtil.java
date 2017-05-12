package com.dysania.litepedometer.utils;

import java.util.Calendar;

/**
 * Created by DysaniazzZ on 12/05/2017.
 * 时间工具类
 */

public class TimeUtil {

    /**
     * 判断两个毫秒值是否是同一天
     */
    public static boolean isTheSameDay(long milliSeconds1, long milliSeconds2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTimeInMillis(milliSeconds1);
        calendar2.setTimeInMillis(milliSeconds2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2
                .get(Calendar.DAY_OF_YEAR);
    }
}
