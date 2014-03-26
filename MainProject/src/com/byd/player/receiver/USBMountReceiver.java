package com.byd.player.receiver;

import com.byd.player.audio.AudioListActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    private Context mContext = null;

    public USBMountReceiver() {

    }

    public USBMountReceiver(Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                + Environment.getExternalStorageDirectory()));
        context.sendBroadcast(intentScanner);
        intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                + "/extsd/"));
        context.sendBroadcast(intentScanner);
        intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                + "/udisk/"));
        context.sendBroadcast(intentScanner);

        if (mContext != null && mContext instanceof AudioListActivity) {
            // update audio list 1 min later.
            mHandler.sendEmptyMessageDelayed(RELOAD_AUDIO, 60 * 1000);
        }
    }

    private final static int RELOAD_AUDIO = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case RELOAD_AUDIO:
                if (mContext != null && mContext instanceof AudioListActivity) {
                    ((AudioListActivity) mContext).refreshDatas();
                }
                break;
            default:
                break;
            }
        }

    };

}
