package com.byd.player.audio;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.audio.AudioPlayerService.OnPlayPauseListener;
import com.byd.player.audio.AudioPlayerService.OnUpdateListener;
import com.byd.player.audio.AudioPlayerService.PlayerBinder;
import com.byd.player.config.Constants;
import com.byd.player.view.CheckableImageView;

public class AudioPlayerActivity extends BaseActivity {
    private Song mPlayingSong;

    private int mSongPosition;

    /**
     * The container of song info or lyrics
     */
    private LinearLayout mSongInfoAndLyricsContainer;

    private TextView mTotalTime;

    private TextView mPlayingTime;

    private SeekBar mProgressBar;

    private TextView mAlbumName;

    private TextView mSingerName;

    private TextView mMusicName;

    private CheckableImageView mBtnPlayPause;

    private LayoutInflater mInflater;

    private PlayerReceiver mPlayerReceiver;

    private Intent mAudioServiceIntent;

    private AudioServiceConn mConn;

    private AudioPlayerService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.audio_player_view);

        mInflater = this.getLayoutInflater();

        mAudioServiceIntent = new Intent(this, AudioPlayerService.class);

        init(getIntent().getIntExtra(Constants.MUSIC_SONG_POSITION, -1));
        startPlay();

        registerBroadcast();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConn = new AudioServiceConn();
        bindService(mAudioServiceIntent, mConn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        if (!mService.isPlaying()) {
            stopService(mAudioServiceIntent);
        }
    }

    private void init(int songPosition) {
        initSong(songPosition);
        initViews();
    }

    private void initSong(int songPosition) {
        mSongPosition = songPosition;
        mPlayingSong = AudioManager.getInstance().getSongAtPosition(mSongPosition);
    }

    private void initViews() {
        mSongInfoAndLyricsContainer = (LinearLayout)findViewById(R.id.ll_song_info_and_lyrics);
        mSongInfoAndLyricsContainer.removeAllViews();
        if (hasLyrics()) {
            // TODO: Add lyrics display
        } else {
            mSongInfoAndLyricsContainer.addView(mInflater.inflate(
                    R.layout.layout_song_info, null));
            mAlbumName = (TextView)findViewById(R.id.album_name);
            mSingerName = (TextView)findViewById(R.id.singer_name);
            mMusicName = (TextView)findViewById(R.id.music_name);
            mAlbumName.setText(mPlayingSong.getAlbum());
            mSingerName.setText(mPlayingSong.getSinger());
            mMusicName.setText(mPlayingSong.getFileTitle());
        }

        mTotalTime = (TextView)findViewById(R.id.audio_total_time);
        mPlayingTime = (TextView)findViewById(R.id.audio_playing_time);
        mProgressBar = (SeekBar)findViewById(R.id.audio_seekbar);
        mProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });

        mBtnPlayPause = (CheckableImageView)findViewById(R.id.btn_audio_play_pause);
        mBtnPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBtnPlayPause.isChecked()) {
                    continuePlay();
                } else {
                    pause();
                }
            }
        });
    }

    private boolean hasLyrics() {
        // TODO: Add lyrics check
        return false;
    }

    private void startPlay() {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.PLAY);
        mAudioServiceIntent.putExtra(Constants.MUSIC_SONG_POSITION, mSongPosition);
        startService(mAudioServiceIntent);
    }

    private void stopPlay() {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.STOP);
        startService(mAudioServiceIntent);
    }

    private void seekTo(int progress) {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.SEEK);
        mAudioServiceIntent.putExtra(Constants.MUSIC_SEEK_TO, progress);
        startService(mAudioServiceIntent);
    }

    private void pause() {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.PAUSE);
        startService(mAudioServiceIntent);
    }

    private void continuePlay() {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.CONTINUE_PLAY);
        startService(mAudioServiceIntent);
    }

    private void registerBroadcast() {
        mPlayerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        //        registerReceiver(mPlayerReceiver, filter);
    }

    private void unregisterBroadcast() {
        //        unregisterReceiver(mPlayerReceiver);
    }

    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // if (action.equals(MUSIC_CURRENT)) {
            // currentTime = intent.getIntExtra("currentTime", -1);
            // currentProgress.setText(MediaUtil.formatTime(currentTime));
            // music_progressBar.setProgress(currentTime);
            // } else if (action.equals(MUSIC_DURATION)) {
            // int duration = intent.getIntExtra("duration", -1);
            // music_progressBar.setMax(duration);
            // finalProgress.setText(MediaUtil.formatTime(duration));
            // } else if (action.equals(UPDATE_ACTION)) {
            // // 获取Intent中的current消息，current代表当前正在播放的歌曲
            // listPosition = intent.getIntExtra("current", -1);
            // url = mp3Infos.get(listPosition).getUrl();
            // if (listPosition >= 0) {
            // musicTitle.setText(mp3Infos.get(listPosition).getTitle());
            // musicArtist.setText(mp3Infos.get(listPosition).getArtist());
            // }
            // if (listPosition == 0) {
            // finalProgress.setText(MediaUtil.formatTime(mp3Infos.get(
            // listPosition).getDuration()));
            // playBtn.setBackgroundResource(R.drawable.pause_selector);
            // isPause = true;
            // }
            // }
        }
    }

    private void updateAudioDuration(int duration) {
        mProgressBar.setMax(duration);
        mTotalTime.setText(progresstime(duration));
    }

    private void updateAudioCurrent(int position) {
        mProgressBar.setProgress(position);
        mPlayingTime.setText(progresstime(position));
    }

    private void updatePlayPauseBtn(boolean isPlay) {
        // Check the button if the audio is not playing.
        mBtnPlayPause.setChecked(!isPlay);
    }

    private void initPlayTime(int position, int duration) {
        updateAudioDuration(duration);
        updateAudioCurrent(position);
    }

    private class AudioServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerBinder binder = (PlayerBinder)service;
            mService = binder.getService();
            initPlayTime(mService.getAudioCurrent(),mService.getAudioDuration());
            updatePlayPauseBtn(mService.isPlaying());
            mService.setOnUpdateListener(new OnUpdateListener() {
                @Override
                public void onUpdate(int current) {
                    updateAudioCurrent(current);
                    // Make sure the status of PlayPause button is right
                    if (mService.isPlaying() == mBtnPlayPause.isChecked()) {
                        mBtnPlayPause.setChecked(!mService.isPlaying());
                    }
                }
            });
            mService.setOnPlayPauseListener(new OnPlayPauseListener() {
                @Override
                public void onPlayPause(boolean isPlay) {
                    updatePlayPauseBtn(isPlay);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }
    }

    private String progresstime(int progress) {
        Date date = new Date(progress);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }
}
