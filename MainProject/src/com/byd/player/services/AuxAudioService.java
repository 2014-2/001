package com.byd.player.services;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.IBinder;
import android.util.Log;

import com.byd.player.AuxAudioPlayActivity;
import com.byd.player.receiver.DeviceConnReceiver;
import com.byd.player.receiver.DeviceConnReceiver.AuxConnectListener;

public class AuxAudioService extends Service {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "AuxService";

    private static final int MIN_BUFFER_SIZE = 4096;
    private static final int[] SAMPLE_RATES = new int[] { 8000, 11025, 22050, 44100 };
    private static final short[] AUDIO_FORMATS =  new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT };
    private static final short[] CHANNELS = new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO };

    private BlockingQueue<byte[]> mAudioDataQueue = new LinkedBlockingDeque<byte[]>();

    private int mRate;
    private int mBufSize;

    private AuxAudioRecoder mRecoder;
    private AuxAudioPlayer  mPlayer;

    private DeviceConnReceiver mDeviceConnReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mDeviceConnReceiver = new DeviceConnReceiver(new AuxConnectListener() {

            @Override
            public void onDisconnected() {
                stopRecoder();
                stopPlayer();
            }

            @Override
            public void onConnected() {
                startRecoder();
                // Start AuxAudioPlayActivity
                Intent intent = new Intent(AuxAudioService.this,
                        AuxAudioPlayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AuxAudioService.this.startActivity(intent);
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mDeviceConnReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDeviceConnReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startRecoder() {
        mRecoder = new AuxAudioRecoder();
        Thread t = new Thread(mRecoder);
        t.start();
    }

    private void stopRecoder() {
        if (mRecoder != null && mRecoder.isRecording()) {
            mRecoder.stop();
        }
    }

    private void startPlayer() {
        mPlayer = new AuxAudioPlayer();
        Thread th = new Thread(mPlayer);
        th.start();
    }

    private void stopPlayer() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    private AudioRecord getAudioRecord() {
        for (int rate : SAMPLE_RATES) {
            for (short audioFormat : AUDIO_FORMATS) {
                for (short channelConfig : CHANNELS) {
                    try {
                        if (DEBUG) {
                            Log.d(LOG_TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
                        }
                        int minBufferSize = AudioRecord.getMinBufferSize(rate,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT);
                        if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            final int bufferSize = (minBufferSize < MIN_BUFFER_SIZE) ? MIN_BUFFER_SIZE : minBufferSize;
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(
                                    AudioSource.MIC, rate, channelConfig,
                                    audioFormat, bufferSize);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                if (DEBUG) {
                                    Log.d(LOG_TAG, "Initalized successfully at rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
                                }
                                mRate = rate;
                                mBufSize = bufferSize;
                                return recorder;
                            } else {
                                recorder.release();
                                recorder = null;
                            }
                        }
                    } catch (Exception e) {
                        if (DEBUG) {
                            Log.e(LOG_TAG, rate + "Exception, keep trying.", e);
                        }
                    }
                }
            }
        }
        return null;
    }

    private AudioTrack getAudioTrack() {
        final int minBufferSize = AudioTrack.getMinBufferSize(mRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        final int bufferSize = (minBufferSize < MIN_BUFFER_SIZE) ? MIN_BUFFER_SIZE : minBufferSize;
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize, AudioTrack.MODE_STREAM);
        if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            return audioTrack;
        }
        return null;
    }

    private class AuxAudioRecoder implements Runnable {
        private final Object mLock = new Object();
        private volatile boolean mIsRecording = true;

        @Override
        public void run() {
            AudioRecord record = getAudioRecord();
            if (record != null) {
                record.startRecording();
                startPlayer();
                byte[] audioData = new byte[mBufSize];
                while (mIsRecording) {
                    record.read(audioData, 0, audioData.length);
                    mAudioDataQueue.add(audioData.clone());
                    Arrays.fill(audioData, (byte)0x00);
                }
                record.release();
                record = null;
            }
        }

        public void stop(){
            synchronized (mLock) {
                mIsRecording = false;
            }
        }

        public boolean isRecording() {
            synchronized (mLock) {
                return mIsRecording;
            }
        }
    }

    private class AuxAudioPlayer implements Runnable {
        private final Object mLock = new Object();
        private volatile boolean mIsPlaying = true;

        @Override
        public void run() {
            AudioTrack audioTrack = getAudioTrack();
            if (audioTrack != null) {
                audioTrack.play();
                while(mIsPlaying) {
                    try {
                        byte[] audioData = mAudioDataQueue.take();
                        audioTrack.write(audioData, 0, audioData.length);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                audioTrack.release();
                audioTrack = null;
            }
        }

        public void stop() {
            synchronized (mLock) {
                mIsPlaying = false;
            }
        }

        public boolean isPlaying() {
            synchronized (mLock) {
                return mIsPlaying;
            }
        }
    }
    //
    // private class DeviceConnReceiver extends BroadcastReceiver {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // final String action = intent.getAction();
    // if (ACTION_DEVICE_CONNECTED.equals(action)) {
    // startRecoder();
    // } else if (ACTION_DEVICE_UNCONNECTED.equals(action)) {
    // stopRecoder();
    // stopPlayer();
    // } else {
    // if (DEBUG) {
    // Log.e(LOG_TAG, "DeviceConnReceiver receives unknown action: " + action);
    // }
    // }
    // }
    // }

}
