package com.byd.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

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
    }
    
}
