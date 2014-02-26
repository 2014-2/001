package com.byd.player.audio;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;

import com.byd.player.config.Constants;

public class AudioPlayerService extends Service {
    private MediaPlayer mPlayer;

    private String mPath;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (mPlayer != null) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.PlayerAction.ACTION_UPDATE_CURRENT);
                    intent.putExtra(Constants.MUSIC_CURRENT, mPlayer.getCurrentPosition());
                    sendBroadcast(intent);
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
    };

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
        // TODO Auto-generated method stub
        return null;
    }

    private final class PreparedListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlayer.start();
            Intent intent = new Intent();
            intent.setAction(Constants.PlayerAction.ACTION_DURATION);
            int duration = mPlayer.getDuration();
            intent.putExtra(Constants.MUSIC_DURATION, duration);
            sendBroadcast(intent);
            handler.sendEmptyMessage(1);
        }
    }
}
