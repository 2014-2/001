package com.byd.audioplayer.audio;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Canbus;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Equalizer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.byd.audioplayer.R;
import com.byd.audioplayer.config.Constants;

public class AudioPlayerService extends Service {
    private static final String LOG_TAG = AudioPlayerService.class.getSimpleName();

    private static final int HANDLER_MSG_UPDATE = 1;

    public static final String ACTION_SUSPEND_BROADCAST = "com.android.suspend.BroadcastReceiver";

    public static final String ACTION_CAR_SETTING = "com.canbus.action.CAR_SETTING";

    public static MediaPlayer mPlayer = null;

    private IBinder mPlayerBinder = new PlayerBinder();

    private OnUpdateListener mUpdateListener;

    private OnPlayPauseListener mPlayPauseListener;

    private onServiceStopListener mServiceStopListener;

    private static ArrayList<OnSongChangedListener> mSongChangedListenerList = new ArrayList<AudioPlayerService.OnSongChangedListener>();

    private Song mPlayingSong;

    public static int mSongPosition = -1;

    private Equalizer mEqualizer;

    private AudioManager am;

    private WheelKeyReceiver mWheelKeyReceiver;

    private SuspendReceiver mSuspendReceiver;

    private StorageMountReceiver mStorageMountReceiver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == HANDLER_MSG_UPDATE) {
                if (mPlayer != null) {
                    if (null != mUpdateListener) {
                        mUpdateListener.onUpdate(mPlayer.getCurrentPosition());
                    }
                    handler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE, 1000);
                }
            }
        };
    };

    public interface OnUpdateListener {
        /**
         * 更新音乐播放进度
         * 
         * @param current 当前播放时间
         */
        public void onUpdate(int current);
    }

    public interface OnPlayPauseListener {
        /**
         * 播放或暂停音乐
         * 
         * @param isPlay true代表音乐开始播放，false代表音乐被暂停
         */
        public void onPlayPause(boolean isPlay);
    }

    public interface OnSongChangedListener {
        /**
         * 歌曲切换
         * 
         * @param newPosition 切换到的歌曲在音乐列表中的索引位置
         */
        public void onSongChanged(int newPosition);
    }

    public interface onServiceStopListener {
        /**
         * AudioPlayerService终止时调用
         */
        public void onServiceStop();
    }

    public class PlayerBinder extends Binder {
        AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        mUpdateListener = listener;
    }

    public void setOnPlayPauseListener(OnPlayPauseListener listener) {
        mPlayPauseListener = listener;
    }

    public void setOnServiceStopListener(onServiceStopListener listener) {
        mServiceStopListener = listener;
    }

    public static void setOnSongChangedListener(OnSongChangedListener listener) {
        if (!isContainThisListener(listener)) {
            mSongChangedListenerList.add(listener);
        }
    }

    public static void removeOnSongChangeListener (OnSongChangedListener listener) {
        mSongChangedListenerList.remove(listener);
    }

    private static boolean isContainThisListener (OnSongChangedListener listener) {
        return mSongChangedListenerList.contains(listener);
    }

    /**
     * 获取歌曲总时间长度
     * 
     * @return 歌曲的时间长度
     */
    public int getAudioDuration() {
        return mPlayer.getDuration();
    }

    /**
     * 获取歌曲当前播放时间
     * 
     * @return 歌曲当前播放时间
     */
    public int getAudioCurrent(){
        return mPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    private void changeToNext() {
        boolean isPlayOrderChecked = Constants.isPlayOrderChecked(getApplicationContext());
        boolean isLoopModeChecked = Constants.isLoopModeChecked(getApplicationContext());
        int status = -1;
        if (isPlayOrderChecked && !isLoopModeChecked) {
            status = Constants.getPlayOrder(getApplicationContext());
        } else if (!isPlayOrderChecked && isLoopModeChecked) {
            status = Constants.getLoopMode(getApplicationContext());
        }
        mSongPosition = AudioPlayerManager.getInstance().getCurrentPlayingSongPosition();
        switch (status) {
            case Constants.PlayOrder.ORDER_PLAY:
                mSongPosition++;
                if (mSongPosition < AudioPlayerManager.getInstance().getCount()) {
                    changeSong(mSongPosition);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.last_song, Toast.LENGTH_SHORT)
                    .show();
                    mSongPosition--;
                }
                break;
            case Constants.PlayOrder.RANDOM_PLAY:
                mSongPosition = getRandomIndex(AudioPlayerManager.getInstance().getCount());
                changeSong(mSongPosition);
                break;
            case Constants.LoopMode.SINGLE_LOOP:
                changeSong(mSongPosition);
                break;
            case Constants.LoopMode.LIST_LOOP:
            default:
                mSongPosition++;
                changeSong(mSongPosition);
        }
    }

    private void forceChangeToNext() {
        boolean isPlayOrderChecked = Constants.isPlayOrderChecked(getApplicationContext());
        boolean isLoopModeChecked = Constants.isLoopModeChecked(getApplicationContext());
        int status = -1;
        if (isPlayOrderChecked && !isLoopModeChecked) {
            status = Constants.getPlayOrder(getApplicationContext());
        } else if (!isPlayOrderChecked && isLoopModeChecked) {
            status = Constants.getLoopMode(getApplicationContext());
        }
        switch (status) {
            case Constants.PlayOrder.RANDOM_PLAY:
                mSongPosition = getRandomIndex(AudioPlayerManager.getInstance().getCount());
                changeSong(mSongPosition);
                break;
            case Constants.PlayOrder.ORDER_PLAY:
            case Constants.LoopMode.SINGLE_LOOP:
            case Constants.LoopMode.LIST_LOOP:
            default:
                mSongPosition++;
                changeSong(mSongPosition);
        }
    }

    private void forceChangeToPrevious() {
        boolean isPlayOrderChecked = Constants.isPlayOrderChecked(getApplicationContext());
        boolean isLoopModeChecked = Constants.isLoopModeChecked(getApplicationContext());
        int status = -1;
        if (isPlayOrderChecked && !isLoopModeChecked) {
            status = Constants.getPlayOrder(getApplicationContext());
        } else if (!isPlayOrderChecked && isLoopModeChecked) {
            status = Constants.getLoopMode(getApplicationContext());
        }
        switch (status) {
            case Constants.PlayOrder.RANDOM_PLAY:
                mSongPosition = getRandomIndex(AudioPlayerManager.getInstance().getCount());
                changeSong(mSongPosition);
                break;
            case Constants.LoopMode.SINGLE_LOOP:
            case Constants.PlayOrder.ORDER_PLAY:
            case Constants.LoopMode.LIST_LOOP:
            default:
                mSongPosition--;
                changeSong(mSongPosition);
        }
    }

    private void changeSong(int position) {
        int size = AudioPlayerManager.getInstance().getCount();
        mSongPosition = (mSongPosition + size) % size;
        mPlayer.reset();
        handler.removeMessages(HANDLER_MSG_UPDATE);
        AudioPlayerManager.getInstance().setPlaySong(mSongPosition);
        mPlayingSong = AudioPlayerManager.getInstance().getSongAtPosition(mSongPosition);
        try {
            mPlayer.setDataSource(mPlayingSong.getFilePath());
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (null != mSongChangedListenerList && !mSongChangedListenerList.isEmpty()) {
            for (OnSongChangedListener listener : mSongChangedListenerList) {
                listener.onSongChanged(mSongPosition);
            }
        }
    }

    private void setEqualizer(int audioFx) {
        short bands = mEqualizer.getNumberOfBands();

        for (short i = 0; i < bands; i++) {
            mEqualizer.setBandLevel(i, Constants.SOUND_EFFECT_LEVEL[audioFx][i]);
        }
    }

    private int getRandomIndex(int range) {
        int newPosition = (int)(Math.random() * range);
        if (range > 1 && newPosition == mSongPosition) {
            newPosition = getRandomIndex(range);
        }
        return newPosition;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new PreparedListener());
        mPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                changeToNext();
            }
        });

        mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);
        int audioFx = Constants.getAudioFx(getApplicationContext());
        setEqualizer(audioFx);

        registerReceivers();
    }

    private void registerReceivers() {
        if (mWheelKeyReceiver == null) {
            mWheelKeyReceiver = new WheelKeyReceiver();
            IntentFilter filter = new IntentFilter(ACTION_CAR_SETTING);
            filter.addCategory("com.canbus.action.CAR_SETTING.WHEEL_KEY");
            registerReceiver(mWheelKeyReceiver, filter);
        }

        if (mSuspendReceiver == null) {
            mSuspendReceiver = new SuspendReceiver();
            IntentFilter filter2 = new IntentFilter(ACTION_SUSPEND_BROADCAST);
            registerReceiver(mSuspendReceiver, filter2);
        }
        if (mStorageMountReceiver == null) {
            mStorageMountReceiver = new StorageMountReceiver();
            IntentFilter filter3 = new IntentFilter();
            filter3.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter3.addAction(Intent.ACTION_MEDIA_EJECT);
            filter3.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter3.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter3.addDataScheme("file");
            registerReceiver(mStorageMountReceiver, filter3);
        }
    }

    private void unregisterReceivers() {
        unregisterReceiver(mWheelKeyReceiver);
        unregisterReceiver(mSuspendReceiver);
        unregisterReceiver(mStorageMountReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        int action = intent.getIntExtra(Constants.PLAYER_MSG, -1);
        switch (action) {
            case Constants.PlayerCommand.PLAY:
                mSongPosition = intent.getIntExtra(Constants.MUSIC_SONG_POSITION, -1);
                Song song = AudioPlayerManager.getInstance().getSongAtPosition(mSongPosition);
                if (song != null && !song.equals(mPlayingSong)) {
                    if (isPlaying()) {
                        mPlayer.stop();
                    }
                    mPlayer.reset();
                    try {
                        mPlayer.setDataSource(song.getFilePath());
                        mPlayingSong = song;
                        mPlayer.prepare();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.PlayerCommand.STOP:
                mPlayer.stop();
                break;
            case Constants.PlayerCommand.SEEK:
                int progress = intent.getIntExtra(Constants.MUSIC_SEEK_TO,
                        mPlayer.getCurrentPosition());
                mPlayer.seekTo(progress);
                break;
            case Constants.PlayerCommand.PAUSE:
                mPlayer.pause();
                if (null != mPlayPauseListener) {
                    mPlayPauseListener.onPlayPause(false);
                }
                break;
            case Constants.PlayerCommand.CONTINUE_PLAY:
                mPlayer.start();
                if (null != mPlayPauseListener) {
                    mPlayPauseListener.onPlayPause(true);
                }
                break;
            case Constants.PlayerCommand.NEXT:
                forceChangeToNext();
                break;
            case Constants.PlayerCommand.PREVIOUS:
                forceChangeToPrevious();
                break;
            case Constants.PlayerCommand.AUDIO_FX:
                int audioFx = intent.getIntExtra(Constants.AUDIO_FX_ID, Constants.AudioFx.NONE);
                setEqualizer(audioFx);
                break;
            case Constants.PlayerCommand.PLAY_POSITION:
                mSongPosition = intent.getIntExtra(Constants.MUSIC_SONG_POSITION, -1);
                changeSong(mSongPosition);
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mPlayerBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        am.abandonAudioFocus(afChangeListener);
        handler.removeMessages(HANDLER_MSG_UPDATE);
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        mSongPosition = -1;
        mSongChangedListenerList.clear();
        if (mServiceStopListener != null) {
            mServiceStopListener.onServiceStop();
        }
        unregisterReceivers();
    }

    private final class PreparedListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlayer.start();
            handler.sendEmptyMessage(HANDLER_MSG_UPDATE);
        }
    }

    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(LOG_TAG, "AudioFocus focusChange:" + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    if (mPlayer != null && !mPlayer.isPlaying()) {
                        mPlayer.start();
                        if (null != mPlayPauseListener) {
                            mPlayPauseListener.onPlayPause(true);
                        }
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mPlayer.pause();
                    if (null != mPlayPauseListener) {
                        mPlayPauseListener.onPlayPause(false);
                    }
                    break;
            }
        }
    };

    class WheelKeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.canbus.action.CAR_SETTING".equals(action)) {
                int value = intent.getIntExtra("value", -1);
                if (-1 == value) {
                    // if the value is not int type
                    String valueStr = intent.getStringExtra("value");
                    value = Integer.parseInt(valueStr);
                }
                switch (value) {
                    case Canbus.WHEEL_KEY_SEEK_UP_SHORT:
                        forceChangeToPrevious();
                        break;
                    case Canbus.WHEEL_KEY_SEEK_DOWN_SHORT:
                        forceChangeToNext();
                        break;
                }
            }
        }
    }

    class SuspendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(LOG_TAG, "suspend action = " + action);
            String valueStr = intent.getStringExtra("command");
            Log.d(LOG_TAG, "suspend action command = " + valueStr);
            if (ACTION_SUSPEND_BROADCAST.equals(action)) {
                if("stop".equals(valueStr)) {
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        mPlayer.pause();
                        if (null != mPlayPauseListener) {
                            mPlayPauseListener.onPlayPause(false);
                        }
                    }
                } else if ("recover".equals(valueStr)) {
                    if (mPlayer != null && !mPlayer.isPlaying()) {
                        mPlayer.start();
                        if (null != mPlayPauseListener) {
                            mPlayPauseListener.onPlayPause(true);
                        }
                    }
                }
            }
        }
    }

    class StorageMountReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(LOG_TAG, "Storage Mount Action is: " + action);
            String path = intent.getData().getPath();
            // 重新刷新播放歌曲列表
            AudioPlayerManager.getInstance().storageStatusChange(mPlayingSong);
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                Log.d(LOG_TAG, "external storage insert");
                Log.d(LOG_TAG, "Storage path is: " + path);
            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)
                    || Intent.ACTION_MEDIA_REMOVED.equals(action)
                    || Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
                Log.d(LOG_TAG, "external storage remove");
                Log.d(LOG_TAG, "Storage path is: " + path);
                if (mPlayingSong.getFilePath().contains(path)) {
                    Log.d(LOG_TAG, "Playing song has been removed");
                    stopSelf();
                    Toast.makeText(getApplicationContext(), R.string.external_storage_removed,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
