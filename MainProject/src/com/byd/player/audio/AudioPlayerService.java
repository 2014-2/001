package com.byd.player.audio;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.byd.player.config.Constants;

public class AudioPlayerService extends Service {
    private MediaPlayer mPlayer;

    private IBinder mPlayerBinder = new PlayerBinder();

    private OnUpdateListener mUpdateListener;

    private String mPath;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (mPlayer != null) {
                    if (null != mUpdateListener) {
                        mUpdateListener.onUpdate(mPlayer.getCurrentPosition());
                    }
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
    };

    public interface OnUpdateListener {
        public void onUpdate(int current);
    }

    public class PlayerBinder extends Binder {
        AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        mUpdateListener = listener;
    }

    public int getAudioDuration(){
        return mPlayer.getDuration();
    }

    public int getAudioCurrent(){
        return mPlayer.getCurrentPosition();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.reset();
        mPlayer.setOnPreparedListener(new PreparedListener());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        int action = intent.getIntExtra(Constants.PLAYER_MSG, -1);
        switch (action) {
            case Constants.PlayerCommand.PLAY:
                String path = intent.getStringExtra(Constants.MUSIC_URL);
                if (!path.equals(mPath)) {
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                    }
                    mPlayer.reset();
                    try {
                        mPlayer.setDataSource(path);
                        mPath = path;
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
            default:
                break;
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mPlayerBinder;
    }

    private final class PreparedListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlayer.start();
            handler.sendEmptyMessage(1);
        }
    }
}
