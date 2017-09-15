package com.dysania.litepedometer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DysaniazzZ on 12/05/2017.
 * 计步数据存储工具类
 */

public class SPUtil {

    private static SharedPreferences sp;

    private static final String PEDOMETER_SENSOR_COUNT = "pedometer_sensor_count";  //计步器读取的步数
    private static final String PEDOMETER_STEP_COUNT = "pedometer_step_count";      //步数
    private static final String PEDOMETER_STEP_ANCHOR = "pedometer_step_anchor";    //起点
    private static final String PEDOMETER_UPDATE_TIME = "pedometer_update_time";    //最后一次记录的时间

    public static SharedPreferences getSharedPreferences(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences("Pedometer", Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static void putLong(Context context, String key, long value) {
        sp = getSharedPreferences(context);
        sp.edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key, long defValue) {
        sp = getSharedPreferences(context);
        return sp.getLong(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        sp = getSharedPreferences(context);
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int defValue) {
        sp = getSharedPreferences(context);
        return sp.getInt(key, defValue);
    }

    public static void putSensorCount(Context context, int sensorCount) {
        putInt(context, PEDOMETER_SENSOR_COUNT, sensorCount);
    }

    public static int getSensorCount(Context context) {
        return getInt(context, PEDOMETER_SENSOR_COUNT, 0);
    }

    public static void putStepCount(Context context, int stepCount) {
        putInt(context, PEDOMETER_STEP_COUNT, stepCount);
    }

    public static int getStepCount(Context context) {
        return getInt(context, PEDOMETER_STEP_COUNT, 0);
    }

    public static void putStepAnchor(Context context, int stepAnchor) {
        putInt(context, PEDOMETER_STEP_ANCHOR, stepAnchor);
    }

    public static int getStepAnchor(Context context) {
        return getInt(context, PEDOMETER_STEP_ANCHOR, 0);
    }

    public static void putUpdateTime(Context context, long updateTime) {
        putLong(context, PEDOMETER_UPDATE_TIME, updateTime);
    }

    public static long getUpdateTime(Context context) {
        return getLong(context, PEDOMETER_UPDATE_TIME, 0);
    }
}
