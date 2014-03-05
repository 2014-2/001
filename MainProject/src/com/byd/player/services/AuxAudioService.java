package com.byd.player.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import com.byd.player.config.Constants;
import com.byd.player.receiver.DeviceConnReceiver;
import com.byd.player.receiver.DeviceConnReceiver.AuxConnectListener;

public class AuxAudioService extends Service {
	private static final boolean DEBUG = true;
	private static final String LOG_TAG = "AuxService";

	private static final int MIN_BUFFER_SIZE = 4096;
	private static final int SAMPLE_RATE = 44100;
	private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	// FIXME: not sure
	private static final int IN_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
	private static final int OUT_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;

	private BlockingQueue<byte[]> mAudioDataQueue = new LinkedBlockingDeque<byte[]>();

	private volatile int mBufSize;

	private AuxAudioRecoder mRecoder;
	private AuxAudioPlayer mPlayer;

	private DeviceConnReceiver mDeviceConnReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		if (!DEBUG) {
			mDeviceConnReceiver = new DeviceConnReceiver(
					new AuxConnectListener() {

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
							intent.putExtra(Constants.IS_AUX_CONNECTED, true);
							AuxAudioService.this.startActivity(intent);
						}
					});
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
			registerReceiver(mDeviceConnReceiver, intentFilter);
		} else {
			startRecoder();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mDeviceConnReceiver);
		stopPlayer();
		stopRecoder();
		// Make sure the service is always running
		startService(new Intent(this, AuxAudioService.class));
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
		try {
			if (DEBUG) {
				Log.d(LOG_TAG, "Attempting rate " + SAMPLE_RATE + "Hz, bits: "
						+ AUDIO_FORMAT + ", channel: " + IN_CHANNEL_CONFIG);
			}
			int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
					IN_CHANNEL_CONFIG, AUDIO_FORMAT);
			if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {
				final int bufferSize = (minBufferSize < MIN_BUFFER_SIZE) ? MIN_BUFFER_SIZE
						: minBufferSize;
				// check if we can instantiate and have a success
				AudioRecord recorder = new AudioRecord(AudioSource.MIC,
						SAMPLE_RATE, IN_CHANNEL_CONFIG, AUDIO_FORMAT,
						bufferSize);
				if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
					if (DEBUG) {
						Log.d(LOG_TAG, "Initalized successfully at rate "
								+ SAMPLE_RATE + "Hz, bits: " + AUDIO_FORMAT
								+ ", channel: " + IN_CHANNEL_CONFIG);
					}
					mBufSize = bufferSize;
					return recorder;
				} else {
					recorder.release();
					recorder = null;
				}
			}
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(LOG_TAG, SAMPLE_RATE + "Exception, keep trying.", e);
			}
		}
		return null;
	}

	private AudioTrack getAudioTrack() {
		final int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
				OUT_CHANNEL_CONFIG, AUDIO_FORMAT);
		final int bufferSize = (minBufferSize < MIN_BUFFER_SIZE) ? MIN_BUFFER_SIZE
				: minBufferSize;
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				SAMPLE_RATE, OUT_CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize,
				AudioTrack.MODE_STREAM);
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
				selectAuxChannel();
				record.startRecording();
				startPlayer();
				byte[] audioData = new byte[mBufSize];
				while (mIsRecording) {
					record.read(audioData, 0, audioData.length);
					mAudioDataQueue.add(audioData.clone());
					Arrays.fill(audioData, (byte) 0x00);
				}
				record.release();
				record = null;
			}
		}

		public void stop() {
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
				while (mIsPlaying) {
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

	private static void selectAuxChannel() {
		FileWriter fr = null;
		try {
			fr = new FileWriter(new File("sys/kernel/debug/esai/esai_reg"));
			String cmd = "";
			cmd += "ff ";
			cmd += "3";
			fr.write(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
