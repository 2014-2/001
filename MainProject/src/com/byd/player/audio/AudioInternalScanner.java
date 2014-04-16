package com.byd.player.audio;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.byd.player.audio.AudioLoaderManager;

public class AudioInternalScanner implements MediaScannerConnection.OnScanCompletedListener {
    public final static int MEDIA_SCAN_PERIOD = 60 * 1000;

    private Context mContext;
    private Timer mScanTimer;

    public AudioInternalScanner(Context context) {
        mContext = context;
    }

    public void startScan() {
        if (mScanTimer == null) {
            mScanTimer = new Timer();
        }
        mScanTimer.schedule(new ScanRunnable(), MEDIA_SCAN_PERIOD >> 1, MEDIA_SCAN_PERIOD);
    }

    public void stopScan() {
        if (mScanTimer != null) {
            mScanTimer.cancel();
            mScanTimer = null;
        }
    }

    private class ScanRunnable extends TimerTask {
        @Override
        public void run() {
            MediaScannerConnection.scanFile(mContext, new String[] { "/storage/emulated/" }, null,
                    AudioInternalScanner.this);
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        AudioLoaderManager.getInstance().loadData(AudioLoaderManager.INTERNAL_TYPE);
    }
}
