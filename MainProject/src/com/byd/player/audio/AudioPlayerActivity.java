package com.byd.player.audio;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.config.Constants;

public class AudioPlayerActivity extends BaseActivity {
    private Song mPlayingSong;

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

    private LayoutInflater mInflater;

    private PlayerReceiver mPlayerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.audio_player_view);

        mPlayingSong = (Song)getIntent().getExtras().getSerializable(Constants.EXTENDED_DATA_SONG);
        mInflater = this.getLayoutInflater();

        initViews();
        startPlay();

        registerBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
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
            mAlbumName.setSelected(true);
            mSingerName.setText(mPlayingSong.getSinger());
            mSingerName.setSelected(true);
            mMusicName.setText(mPlayingSong.getFileTitle());
            mMusicName.setSelected(true);
        }

        mTotalTime = (TextView)findViewById(R.id.audio_total_time);
        mPlayingTime = (TextView)findViewById(R.id.audio_playing_time);
        mProgressBar = (SeekBar)findViewById(R.id.audio_seekbar);
    }

    private boolean hasLyrics() {
        // TODO: Add lyrics check
        return false;
    }

    private void startPlay() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.PLAY);
        intent.putExtra(Constants.MUSIC_URL, mPlayingSong.getFilePath());
        startService(intent);
    }

    private void stopPlay() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.STOP);
        startService(intent);
    }

    private void registerBroadcast() {
        mPlayerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.PlayerAction.ACTION_DURATION);
        filter.addAction(Constants.PlayerAction.ACTION_UPDATE_CURRENT);
        registerReceiver(mPlayerReceiver, filter);
    }

    private void unregisterBroadcast() {
        unregisterReceiver(mPlayerReceiver);
    }

    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.PlayerAction.ACTION_UPDATE_CURRENT.equals(action)) {
                int current = intent.getIntExtra(Constants.MUSIC_CURRENT, 0);
                mProgressBar.setProgress(current);
                mPlayingTime.setText(progresstime(current));
            } else if (Constants.PlayerAction.ACTION_DURATION.equals(action)) {
                int duration = intent.getIntExtra(Constants.MUSIC_DURATION, 0);
                mProgressBar.setMax(duration);
                mTotalTime.setText(progresstime(duration));
            }
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

    private String progresstime(int progress) {
        Date date = new Date(progress);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }
}
