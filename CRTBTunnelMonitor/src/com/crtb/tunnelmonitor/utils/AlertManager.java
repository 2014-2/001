
package com.crtb.tunnelmonitor.utils;

import java.util.Date;

import android.os.AsyncTask;
import android.util.Log;

public class AlertManager {

    private static final String TAG = "AlertManager";
    private int mAlertId;
    private int mDataStatus;
    private int mAlertStatus;
    private boolean mIsRebury = false;
    private float mCorretion;
    private String mHandling;
    private Date mHandlingTime;
    private HandleFinishCallback mCallback;
    private int mCurAlertStatus;
    private String mRockGrage;

    public void handleAlert(int alertId, int dataStatus, boolean isRebury, float correction, int curAlertStatus, int alertStatus,
            String handling, Date handlingTime,String rockGrage, HandleFinishCallback callback) {
        Log.d(TAG, "handleAlert");
        mAlertId = alertId;
        mDataStatus = dataStatus;
        mCurAlertStatus = curAlertStatus;
        mAlertStatus = alertStatus;
        mIsRebury = isRebury;
        mCorretion = correction;
        mHandling = handling;
        mHandlingTime = handlingTime;
        mCallback = callback;
        mRockGrage = rockGrage;

        new HandleAlertTask().execute();
    }

    class HandleAlertTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "HandleAlertTask doInBackground");
            AlertUtils.handleAlert(mAlertId, mDataStatus, mIsRebury, mCorretion, mCurAlertStatus,
                    mAlertStatus, mHandling, mHandlingTime,mRockGrage);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mCallback != null) {
                mCallback.onFinish();
            }
        }

    }

    public interface HandleFinishCallback {
        public void onFinish();
    }
}
