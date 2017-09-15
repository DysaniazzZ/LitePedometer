package com.dysania.litepedometer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.dysania.litepedometer.R;
import com.dysania.litepedometer.service.PedometerService;
import com.dysania.litepedometer.util.PedometerUtil;
import com.dysania.litepedometer.util.TimeUtil;

public class PedometerActivity extends AppCompatActivity {

    TextView mTvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        if (!PedometerUtil.isSupportStepCounter(this)) {
            Toast.makeText(this, R.string.device_not_support, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTvSteps = (TextView) findViewById(R.id.tv_steps);

        long currentTime = System.currentTimeMillis();
        long updateTime = PedometerUtil.getUpdateTime(this);
        if (!TimeUtil.isTheSameDay(currentTime, updateTime)) {
            //如果记录的数据不是当天的，就先清空
            PedometerUtil.resetStepCount(this);
        }
        PedometerUtil.startPedometerService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(PedometerService.ACTION_STEP_CHANGE);
        registerReceiver(mStepChangeReceiver, intentFilter);
    }

    BroadcastReceiver mStepChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PedometerService.ACTION_STEP_CHANGE.equals(intent.getAction())) {
                int steps = PedometerUtil.getStepCount(context);
                mTvSteps.setText(getString(R.string.today_steps, steps));
            }
        }
    };

    public void onPedometerStartClick(View view) {
        PedometerUtil.startPedometerService(this);
        mTvSteps.setVisibility(View.VISIBLE);
    }

    public void onPedometerStopClick(View view) {
        PedometerUtil.stopPedometerService(this);
        mTvSteps.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mStepChangeReceiver);
    }
}
