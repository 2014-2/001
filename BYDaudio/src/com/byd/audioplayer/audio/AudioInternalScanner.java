package com.byd.audioplayer.audio;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.byd.audioplayer.audio.AudioLoaderManager;

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
        
        mHandler.sendEmptyMessage(RELOAD_AUDIO_INTERNAL);
    }
    
    private final static int RELOAD_AUDIO_INTERNAL = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case RELOAD_AUDIO_INTERNAL:
                AudioLoaderManager.getInstance().loadData(AudioLoaderManager.INTERNAL_TYPE);
                break;
            default:
                break;
            }
        }

    };
}
