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
import android.widget.Toast;

import com.byd.audioplayer.R;
import com.byd.audioplayer.config.Constants;

public class AudioPlayerService extends Service {
    private static final int HANDLER_MSG_UPDATE = 1;

    public static MediaPlayer mPlayer = null;

    private IBinder mPlayerBinder = new PlayerBinder();

    private OnUpdateListener mUpdateListener;

    private OnPlayPauseListener mPlayPauseListener;

    private static ArrayList<OnSongChangedListener> mSongChangedListenerList = new ArrayList<AudioPlayerService.OnSongChangedListener>();

    private Song mPlayingSong;

    public static int mSongPosition = -1;

    private Equalizer mEqualizer;

    private AudioManager am;

    private WheelKeyReceiver mWheelKeyReceiver;

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
         * 閺囧瓨鏌婇棅鍏呯閹绢厽鏂佹潻娑樺
         * 
         * @param current 瑜版挸澧犻幘顓熸杹閺冨爼妫�
         */
        public void onUpdate(int current);
    }

    public interface OnPlayPauseListener {
        /**
         * 閹绢厽鏂侀幋鏍ㄦ畯閸嬫粓鐓舵稊锟�         * 
         * @param isPlay true娴狅綀銆冮棅鍏呯瀵拷顫愰幘顓熸杹閿涘畺alse娴狅綀銆冮棅鍏呯鐞氼偅娈忛崑锟�         */
        public void onPlayPause(boolean isPlay);
    }

    public interface OnSongChangedListener {
        /**
         * 濮濆本娲搁崚鍥ㄥ床
         * 
         * @param newPosition 閸掑洦宕查崚鎵畱濮濆本娲搁崷銊╃叾娑旀劕鍨悰銊よ厬閻ㄥ嫮鍌ㄥ鏇氱秴缂冿拷
         */
        public void onSongChanged(int newPosition);
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
     * 閼惧嘲褰囧灞炬锤閹粯妞傞梻鎾毐鎼达拷
     * 
     * @return 濮濆本娲搁惃鍕闂傛挳鏆辨惔锟�     */
    public int getAudioDuration() {
        return mPlayer.getDuration();
    }

    /**
     * 閼惧嘲褰囧灞炬锤瑜版挸澧犻幘顓熸杹閺冨爼妫�
     * 
     * @return 濮濆本娲歌ぐ鎾冲閹绢厽鏂侀弮鍫曟？
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

        mWheelKeyReceiver = new WheelKeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.canbus.action.CAR_SETTING");
        filter.addCategory("com.canbus.action.CAR_SETTING.WHEEL_KEY");
        registerReceiver(mWheelKeyReceiver, filter);
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
                if (!song.equals(mPlayingSong)) {
                    if (isPlaying()) {
                        mPlayer.stop();
                    }
                    mPlayer.reset();
                    try {
                        mPlayer.setDataSource(song.getFilePath());
                        mPlayingSong = song;
                        mPlayer.prepareAsync();
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
        unregisterReceiver(mWheelKeyReceiver);
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
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPlayer != null && !mPlayer.isPlaying()) {
                        mPlayer.start();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mPlayer.stop();
                    am.abandonAudioFocus(afChangeListener);
                    stopSelf();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mPlayer.pause();
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
}
