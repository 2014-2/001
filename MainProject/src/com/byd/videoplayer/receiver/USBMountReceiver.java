package com.byd.videoplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

    // don't response usb events between TIME_INTERVAL.
    private final long TIME_INTERVAL = 10 * 1000;  
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
        if (curTime - mLastActionTime > TIME_INTERVAL) {
        	
            // scan external storage 
            Intent intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory()));
            mContext.sendBroadcast(intentScanner);

            // scan sdcard
            intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + "/extsd/"));
            mContext.sendBroadcast(intentScanner);
            Intent scanSdcard = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR");
            scanSdcard.setData(Uri.parse("file://" + "/extsd/"));
            mContext.sendBroadcast(scanSdcard);
            
            
            // scan USB
            intentScanner = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + "/udisk/"));
            mContext.sendBroadcast(intentScanner);
            Intent scanUSB = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR");
            scanUSB.setData(Uri.parse("file://" + "/udisk/"));
            mContext.sendBroadcast(scanUSB);

            mLastActionTime = curTime;
        }
    }

}
