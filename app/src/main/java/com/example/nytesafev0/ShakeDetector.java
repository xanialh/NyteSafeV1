package com.example.nytesafev0;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.OptionalDouble;

public class ShakeDetector implements SensorEventListener {
    private static final int shakeResetTime = 3000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    private ArrayList<Float> thresholdValues = new ArrayList<Float>();
    private double thresholdSensitivity = 3;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        void onShake(int count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ); // Close to 1 when no movement

            thresholdValues.add(gForce);

            if (thresholdValues.size() > 100) {
                for (int i = 0; i < 49; i++) {
                    thresholdValues.remove(0);
                }
            }

            OptionalDouble threshold = thresholdValues.stream().mapToDouble(a -> a).average();

            if (gForce > threshold.getAsDouble() * thresholdSensitivity) {
                final long now = System.currentTimeMillis();

                if (mShakeTimestamp + shakeResetTime < now) { // Reset the shake count after 3 seconds of no shakes
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                mListener.onShake(mShakeCount);
            }
        }
    }
}
