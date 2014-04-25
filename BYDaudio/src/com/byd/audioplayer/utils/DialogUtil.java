package com.byd.audioplayer.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.byd.audioplayer.R;

public class DialogUtil {

    private static final String TAG = "DialogUtil";

    static Dialog mDialog;

    public static void showProgressDialog(Context context) {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new ProgressDialog(context);
        ((ProgressDialog) mDialog).setMessage(context
                .getString(R.string.waiting));
        mDialog.setCancelable(true);
        try {
            mDialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            mDialog.cancel();
        }
    }
    
    public static void closeProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
