
package com.crtb.tunnelmonitor.utils;

import java.util.Date;

import android.os.AsyncTask;
import android.util.Log;

public class AlertManager {

    private static final String TAG = "AlertManager";
    private int mAlertId;
    private int mDataStatus;
    private int mAlertStatus;
    private float mCorretion;
    private String mHandling;
    private Date mHandlingTime;

    public void handleAlert(int alertId, int dataStatus, float correction, int alertStatus,
            String handling, Date handlingTime) {
        Log.d(TAG, "handleAlert");
        mAlertId = alertId;
        mDataStatus = dataStatus;
        mAlertStatus = alertStatus;
        mCorretion = correction;
        mHandling = handling;
        mHandlingTime = handlingTime;

        new HandleAlertTask().execute();
    }

    class HandleAlertTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "HandleAlertTask doInBackground");
            AlertUtils.handleAlert(mAlertId, mDataStatus, mCorretion, mAlertStatus, mHandling, mHandlingTime);
            return null;
        }

    }
}
