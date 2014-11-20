
package com.crtb.tunnelmonitor.utils;

import java.util.Date;

import com.crtb.tunnelmonitor.common.Constant;

import android.os.AsyncTask;
import android.util.Log;

public class AlertManager {

    private static final String TAG = "AlertManager:";
    private int mAlertId;
    private int mDataStatus;
    private int mAlertStatus;
    private boolean mIsRebury = false;
    private float mCorretion;
    private int mHandling;
    private String mInfo;
    private Date mHandlingTime;
    private HandleFinishCallback mCallback;
    private int mCurAlertStatus;
    private String mRockGrage;

    public void handleAlert(int alertId, int dataStatus, boolean isRebury, float correction, int curAlertStatus, int alertStatus,
            int handling, String info, Date handlingTime,String rockGrage, HandleFinishCallback callback) {
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
        mInfo = info;

        new HandleAlertTask().execute();
    }

    class HandleAlertTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
        	Log.d(Constant.LOG_TAG,TAG + "HandleAlertTask doInBackground");
            AlertUtils.handleAlert(mAlertId, mDataStatus, mIsRebury, mCorretion, mCurAlertStatus,
                    mAlertStatus, mHandling, mInfo, mHandlingTime,mRockGrage);
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
