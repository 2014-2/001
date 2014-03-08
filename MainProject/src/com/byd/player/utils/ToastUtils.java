package com.byd.player.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast sToast = null;

    public static void showToast(Context context, String text) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(text);
        }
        sToast.show();
    }

    public static void showToast(Context context, int textId) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), textId, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(textId);
        }
        sToast.show();
    }
}
