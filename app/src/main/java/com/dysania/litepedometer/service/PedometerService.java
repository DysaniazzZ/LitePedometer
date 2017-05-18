package com.dysania.litepedometer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.dysania.litepedometer.R;
import com.dysania.litepedometer.utils.PedometerUtil;

/**
 * Created by DysaniazzZ on 12/05/2017.
 */

public class PedometerService extends Service implements SensorEventListener {

    private int mSteps;
    private Sensor mStepCounterSensor;
    private SensorManager mSensorManager;
    private NotificationManager mNotificationManager;

    private static final int NOTIFICATION_ID = R.string.app_name;
    public static final String ACTION_STEP_CHANGE = "com.dysania.litepedometer.ACTION_STEP_CHANGE";

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startStepCounter();
        startForegroundNotification();
        return START_STICKY;
    }

    private void startStepCounter() {
        if (PedometerUtil.isSupportStepCounter(this)) {
            mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void startForegroundNotification() {
        String contentText = getString(R.string.today_steps, PedometerUtil.getStepCount(this));
        startForeground(NOTIFICATION_ID, getNotification(contentText));
    }

    private void updateNotification() {
        String contentText = getString(R.string.today_steps, PedometerUtil.getStepCount(this));
        mNotificationManager.notify(NOTIFICATION_ID, getNotification(contentText));
    }

    private Notification getNotification(String contentText) {
        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(contentText)
                .setOngoing(true)
                .build();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mSteps = (int) event.values[0];      //获取的是从上次设备启动计步传感器被激活后的所有步数，设备重启后数据会被清空

        if (PedometerUtil.handleStepCount(this, mSteps)) {
            updateNotification();
        }

        sendBroadcast(new Intent(ACTION_STEP_CHANGE));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        stopForeground(true);
    }
}
