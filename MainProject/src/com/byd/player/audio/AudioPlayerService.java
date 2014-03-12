package com.byd.player.audio;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

import com.byd.player.config.Constants;

public class AudioPlayerService extends Service {
    private static final int HANDLER_MSG_UPDATE = 1;

    public static MediaPlayer mPlayer;

    private IBinder mPlayerBinder = new PlayerBinder();

    private OnUpdateListener mUpdateListener;

    private OnPlayPauseListener mPlayPauseListener;

    private OnSongChangedListener mSongChangedListener;

    private Song mPlayingSong;

    public static int mSongPosition = -1;

    private Equalizer mEqualizer;

    private AudioManager am;

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
        public void onUpdate(int current);
    }

    public interface OnPlayPauseListener {
        public void onPlayPause(boolean isPlay);
    }

    public interface OnSongChangedListener {
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

    public void setOnSongChangedListener(OnSongChangedListener listener) {
        mSongChangedListener = listener;
    }

    public int getAudioDuration(){
        return mPlayer.getDuration();
    }

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
                    Toast.makeText(getApplicationContext(), "已经是最后一首歌曲", Toast.LENGTH_SHORT).show();
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

    private void changeToPrevious() {
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
                changeSong(mSongPosition);
                break;
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
        if (null != mSongChangedListener) {
            mSongChangedListener.onSongChanged(mSongPosition);
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
        mPlayer.reset();
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
                changeToNext();
                break;
            case Constants.PlayerCommand.PREVIOUS:
                changeToPrevious();
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
        }
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
}
