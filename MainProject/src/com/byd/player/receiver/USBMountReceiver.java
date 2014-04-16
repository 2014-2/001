package com.byd.player.receiver;

import com.byd.player.audio.AudioListActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * There is not special ACTION event for USB plugin, so instead of listening:
 *       Intent.ACTION_MEDIA_MOUNTED + 
 *       Intent.ACTION_MEDIA_CHECKING +
 *       Intent.ACTION_MEDIA_EJECT + 
 *       Intent.ACTION_MEDIA_REMOVED
 * 
 * @author Des
 *
 */
public class USBMountReceiver extends BroadcastReceiver {
    private final static Uri INTERNAL_URI = Uri.parse("file:///storage/emulated/");

    private final long ONE_MIN = 60 * 1000;
    private long mLastActionTime;
    private Context mContext;

    public USBMountReceiver(Context context) {
        mContext = context;
        mLastActionTime = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long curTime = System.currentTimeMillis();
        // make sure the application would not request scan file frequently.
        if (curTime - mLastActionTime > ONE_MIN) {
            // scan internal storage
            Intent intentScanner = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, INTERNAL_URI);
            mContext.sendBroadcast(intentScanner);

            // scan external storage 
            intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory()));
            mContext.sendBroadcast(intentScanner);

            intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + "/extsd/"));
            mContext.sendBroadcast(intentScanner);

            // scan USB
            intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + "/udisk/"));
            mContext.sendBroadcast(intentScanner);

            // send a message for reloading audio list.
            mHandler.sendEmptyMessageDelayed(RELOAD_AUDIO, ONE_MIN);
            mLastActionTime = curTime;
        }
    }

    private final static int RELOAD_AUDIO = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case RELOAD_AUDIO:
                if (mContext instanceof AudioListActivity) {
                    ((AudioListActivity) mContext).refreshDatas();
                }
                break;
            default:
                break;
            }
        }

    };

}
