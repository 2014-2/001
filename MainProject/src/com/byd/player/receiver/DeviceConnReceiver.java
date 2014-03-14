package com.byd.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceConnReceiver extends BroadcastReceiver {
    private static final boolean DEBUG = true;

    private static final String LOG_TAG = "DeviceConnReceiver";

    private static final String ACTION_DEVICE_CONNECTED = "aux.device.connected";

    private static final String ACTION_DEVICE_UNCONNECTED = "aux.device.unconnected";

    private AuxConnectListener mListener;

    public DeviceConnReceiver(AuxConnectListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Use headset for test
        if (intent.hasExtra("state")) {
            if (intent.getIntExtra("state", 0) == 0) {
                mListener.onDisconnected();
            } else if (intent.getIntExtra("state", 0) >= 1) {
                mListener.onConnected();
            }
        }
        final String action = intent.getAction();
        if (ACTION_DEVICE_CONNECTED.equals(action)) {
            mListener.onConnected();
        } else if (ACTION_DEVICE_UNCONNECTED.equals(action)) {
            mListener.onDisconnected();
        } else {
            if (DEBUG) {
                Log.e(LOG_TAG, "DeviceConnReceiver receives unknown action: " + action);
            }
        }
    }

    public interface AuxConnectListener {
        void onConnected();

        void onDisconnected();
    }
}
