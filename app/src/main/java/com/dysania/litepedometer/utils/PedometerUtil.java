package com.dysania.litepedometer.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import com.dysania.litepedometer.service.PedometerService;

/**
 * Created by DysaniazzZ on 12/05/2017.
 * 计步数据处理工具类
 */

public class PedometerUtil {

    /**
     * 判断设备是否支持计步
     */
    public static boolean isSupportStepCounter(Context context) {
        if (VERSION.SDK_INT <= VERSION_CODES.KITKAT) {
            return false;
        }

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            return false;
        }

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        return stepCounterSensor != null;
    }

    /**
     * 开启计步服务
     */
    public static void startPedometerService(Context context) {
        Intent intent = new Intent(context, PedometerService.class);
        context.startService(intent);
    }

    /**
     * 关闭计步服务
     */
    public static void stopPedometerService(Context context) {
        Intent intent = new Intent(context, PedometerService.class);
        context.stopService(intent);
    }

    /**
     * 处理计步器读取的步数，并记录
     */
    public static boolean handleStepCount(Context context, int currentSteps) {
        long updateTime = SPUtil.getUpdateTime(context);
        long currentTime = System.currentTimeMillis();

        if (updateTime == 0) {
            //说明是刚开启计步或者重新计步，取当前计步器步数为起点，开始计步
            int stepAnchor = currentSteps;
            int stepCount = 0;
            SPUtil.putSensorCount(context, currentSteps);
            SPUtil.putStepCount(context, stepCount);
            SPUtil.putStepAnchor(context, stepAnchor);
            SPUtil.putUpdateTime(context, currentTime);
            return true;
        }

        int sensorCount = SPUtil.getSensorCount(context);
        if (currentSteps == sensorCount) {
            return false;
        }

        int stepAnchor = SPUtil.getStepAnchor(context);
        int stepCount = SPUtil.getStepCount(context);
        //注意：这里只能保证重启后计步数据不会倒退，但是重启次数越多，计步会越不准
        if (TimeUtil.isTheSameDay(updateTime, currentTime)) {
            //如果是同一天就可以累加
            if (currentSteps < stepAnchor || currentSteps < stepCount) {
                //如果当前步数小于起点步数或者记录步数的任意一个，就可以认为是中间重启过
                //但是这时候会有个问题，比如记录走了1000步，重启完后一直没有打开应用，这期间走了>=1000步，就无法判断了
                //因为这时候可能有两种情况，一种是没有重启手机，中间只走了1步；一种是重启手机，一共走了1000+1001步
                if (currentSteps < stepAnchor) {
                    stepCount += currentSteps;
                    stepAnchor = currentSteps;
                } else {
                    stepCount += currentSteps - stepAnchor;
                    stepAnchor = currentSteps;
                }
            } else {
                int stepOffset = currentSteps - stepAnchor;
                if (stepOffset < stepCount) {
                    //说明是重启后数据处理的问题
                    stepCount += stepOffset;
                    stepAnchor = currentSteps;
                } else {
                    stepCount = stepOffset;
                }
            }
        } else {
            //如果是跨天，取第一次打开时的数据为起点
            stepAnchor = currentSteps;
            stepCount = 0;
        }
        SPUtil.putSensorCount(context, currentSteps);
        SPUtil.putStepCount(context, stepCount);
        SPUtil.putStepAnchor(context, stepAnchor);
        SPUtil.putUpdateTime(context, currentTime);
        return true;
    }

    /**
     * 获取记录的步数
     */
    public static int getStepCount(Context context) {
        return SPUtil.getStepCount(context);
    }

    public static long getUpdateTime(Context context) {
        return SPUtil.getUpdateTime(context);
    }

    /**
     * 重置计步数据
     */
    public static void resetStepCount(Context context) {
        SPUtil.putSensorCount(context, 0);
        SPUtil.putStepCount(context, 0);
        SPUtil.putStepAnchor(context, 0);
        SPUtil.putUpdateTime(context, 0);
    }
}
