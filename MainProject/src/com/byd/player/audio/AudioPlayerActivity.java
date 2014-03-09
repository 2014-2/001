package com.byd.player.audio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.audio.AudioPlayerService.OnPlayPauseListener;
import com.byd.player.audio.AudioPlayerService.OnSongChangedListener;
import com.byd.player.audio.AudioPlayerService.OnUpdateListener;
import com.byd.player.audio.AudioPlayerService.PlayerBinder;
import com.byd.player.bluetooth.BTPlayerActivity.PlayerReceiver;
import com.byd.player.config.Constants;
import com.byd.player.lrc.LrcContent;
import com.byd.player.lrc.LrcUtils;
import com.byd.player.lrc.LrcView;
import com.byd.player.view.CheckableImageView;
import com.byd.player.view.VerticalSeekBar;

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

    private ImageView mBtnNext;

    private ImageView mBtnPrevious;

    private CheckableImageView mBtnRandomPlay;

    private CheckableImageView mBtnSingleLoop;

    private CheckableImageView mBtnAudioFx;

    private CheckableImageView mBtnVolume;

    private PopupWindow mPopupVolume;

    private PopupWindow mPopupAudioFx;

    private PopupWindow mPopupAudioList;

    private RadioGroup mAudioFxGroup;

    private VerticalSeekBar mVolumeSeekbar;

    private LayoutInflater mInflater;

    private PlayerReceiver mPlayerReceiver;

    private Intent mAudioServiceIntent;

    private AudioServiceConn mConn;

    private android.media.AudioManager mAudioMgr;

    private AudioPlayerService mService;

    private boolean mDisplayLyrics;

    private LrcView mLrcView;

    List<LrcContent> mLrcList;

    private Button mBtnBack;

    private ListView mAudioListView;

    private List<HashMap<String, String>> mAudioList;

    private Button mBtnAudioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.audio_player_view);

        mInflater = this.getLayoutInflater();

        mAudioServiceIntent = new Intent(this, AudioPlayerService.class);

        mAudioMgr = (android.media.AudioManager)getSystemService(Context.AUDIO_SERVICE);

        init(getIntent().getIntExtra(Constants.MUSIC_SONG_POSITION, -1));
        startPlay();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBtnRandomPlay.setChecked(Constants.getRandomPlayStatus(this));
        mBtnSingleLoop.setChecked(Constants.getSingleLoopStatus(this));
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
        if (mService != null && !mService.isPlaying()) {
            stopService(mAudioServiceIntent);
        }
    }

    private void init(int songPosition) {
        initSong(songPosition);
        initViews();
    }

    private void initSong(int songPosition) {
        mSongPosition = songPosition;
        mPlayingSong = AudioPlayerManager.getInstance().getSongAtPosition(mSongPosition);
    }

    private void initViews() {
        if (null == mSongInfoAndLyricsContainer) {
            mSongInfoAndLyricsContainer = (LinearLayout)findViewById(R.id.ll_song_info_and_lyrics);
        }
        mSongInfoAndLyricsContainer.removeAllViews();
        String songPath = "";
        try {
            songPath = mPlayingSong.getFilePath();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            mPlayingSong = AudioManager.getInstance().getPlaySongAtPosition(mSongPosition);
            if (null == mPlayingSong) {
                finish();
            }
        }
        String lrcPath = LrcUtils.replaceExtensionToLrc(songPath);
        mDisplayLyrics = LrcUtils.isLrcFileExist(lrcPath);
        if (mDisplayLyrics) {
            mLrcList = LrcUtils.readLRC(lrcPath);

            mSongInfoAndLyricsContainer.addView(mInflater.inflate(
                    R.layout.layout_lyrics, null));
            mLrcView = (LrcView)findViewById(R.id.lrc_view);
            mLrcView.setLrcList(mLrcList);
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

        if (null == mTotalTime) {
            mTotalTime = (TextView)findViewById(R.id.audio_total_time);
        }
        if (null == mPlayingTime) {
            mPlayingTime = (TextView)findViewById(R.id.audio_playing_time);
        }
        if (null == mProgressBar) {
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
        }

        if (null == mBtnPlayPause) {
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

        if (null == mBtnNext) {
            mBtnNext = (ImageView)findViewById(R.id.btn_audio_next);
            mBtnNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    playNext();
                }
            });
        }

        if (null == mBtnPrevious) {
            mBtnPrevious = (ImageView)findViewById(R.id.btn_audio_previous);
            mBtnPrevious.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPrevious();
                }
            });
        }

        if (null == mBtnRandomPlay) {
            mBtnRandomPlay = (CheckableImageView)findViewById(R.id.btn_audio_random_play);
            mBtnRandomPlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = mBtnRandomPlay.isChecked();
                    Constants.setRandomPlayStatus(getApplicationContext(), !isChecked);
                    mBtnRandomPlay.setChecked(!isChecked);
                }
            });
        }

        if (null == mBtnSingleLoop) {
            mBtnSingleLoop = (CheckableImageView)findViewById(R.id.btn_audio_single_loop);
            mBtnSingleLoop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = mBtnSingleLoop.isChecked();
                    Constants.setSingleLoopStatus(getApplicationContext(), !isChecked);
                    mBtnSingleLoop.setChecked(!isChecked);
                }
            });
        }

        if (null == mPopupVolume) {
            View volume_view = mInflater.inflate(R.layout.layout_audio_volume, null);
            mPopupVolume = new PopupWindow(volume_view, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            mVolumeSeekbar = (VerticalSeekBar)volume_view.findViewById(R.id.audio_volume_seekbar);
            mVolumeSeekbar
            .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                        boolean fromUser) {
                    mAudioMgr.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, progress,
                            android.media.AudioManager.FLAG_PLAY_SOUND);
                }
            });
        }

        if (null == mBtnVolume) {
            mBtnVolume = (CheckableImageView)findViewById(R.id.btn_audio_volume);
            mBtnVolume.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = mBtnVolume.isChecked();
                    if (!isChecked) {
                        mVolumeSeekbar.setProgress(mAudioMgr
                                .getStreamVolume(android.media.AudioManager.STREAM_MUSIC));
                        mVolumeSeekbar.setMax(mAudioMgr
                                .getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC));
                        mPopupVolume.showAtLocation(mSongInfoAndLyricsContainer, Gravity.RIGHT,
                                20, 30);
                        mPopupVolume.update();
                    } else if (mPopupVolume.isShowing()) {
                        mPopupVolume.dismiss();
                    }
                    mBtnVolume.setChecked(!isChecked);
                }
            });
        }

        if (null == mPopupAudioFx) {
            View audio_fx_view = mInflater.inflate(R.layout.audio_fx_popup, null);
            mPopupAudioFx = new PopupWindow(audio_fx_view, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            mAudioFxGroup = (RadioGroup)audio_fx_view.findViewById(R.id.audio_fx_group);
            mAudioFxGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int audioFxId = Constants.AudioFx.NONE;
                    switch (checkedId) {
                        case R.id.fx_none:
                            audioFxId = Constants.AudioFx.NONE;
                            break;
                        case R.id.fx_classical:
                            audioFxId = Constants.AudioFx.CLASSICAL;
                            break;
                        case R.id.fx_dance:
                            audioFxId = Constants.AudioFx.DANCE;
                            break;
                        case R.id.fx_flat:
                            audioFxId = Constants.AudioFx.FLAT;
                            break;
                        case R.id.fx_folk:
                            audioFxId = Constants.AudioFx.FOLK;
                            break;
                        case R.id.fx_heavymetal:
                            audioFxId = Constants.AudioFx.HEAVYMETAL;
                            break;
                        case R.id.fx_hiphop:
                            audioFxId = Constants.AudioFx.HIPHOP;
                            break;
                        case R.id.fx_jazz:
                            audioFxId = Constants.AudioFx.JAZZ;
                            break;
                        case R.id.fx_pop:
                            audioFxId = Constants.AudioFx.POP;
                            break;
                        case R.id.fx_rock:
                            audioFxId = Constants.AudioFx.ROCK;
                            break;
                    }
                    Constants.setAudioFx(getApplicationContext(), audioFxId);
                    setAudioFx(audioFxId);
                }
            });
        }

        if (null == mBtnAudioFx) {
            mBtnAudioFx = (CheckableImageView)findViewById(R.id.btn_audio_effect);
            mBtnAudioFx.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = mBtnAudioFx.isChecked();
                    if (!isChecked) {
                        int audioFx = Constants.getAudioFx(getApplicationContext());
                        switch(audioFx) {
                            case 0:
                                mAudioFxGroup.check(R.id.fx_none);
                                break;
                            case 1:
                                mAudioFxGroup.check(R.id.fx_classical);
                                break;
                            case 2:
                                mAudioFxGroup.check(R.id.fx_dance);
                                break;
                            case 3:
                                mAudioFxGroup.check(R.id.fx_flat);
                                break;
                            case 4:
                                mAudioFxGroup.check(R.id.fx_folk);
                                break;
                            case 5:
                                mAudioFxGroup.check(R.id.fx_heavymetal);
                                break;
                            case 6:
                                mAudioFxGroup.check(R.id.fx_hiphop);
                                break;
                            case 7:
                                mAudioFxGroup.check(R.id.fx_jazz);
                                break;
                            case 8:
                                mAudioFxGroup.check(R.id.fx_pop);
                                break;
                            case 9:
                                mAudioFxGroup.check(R.id.fx_rock);
                                break;
                            default:
                                mAudioFxGroup.check(R.id.fx_none);
                                break;
                        }
                        mPopupAudioFx.showAsDropDown(v, -40, -390);
                    } else if (mPopupAudioFx.isShowing()) {
                        mPopupAudioFx.dismiss();
                    }
                    mBtnAudioFx.setChecked(!isChecked);
                }
            });
        }

        if (null == mBtnBack) {
            mBtnBack = (Button)findViewById(R.id.button_audio_header_back);
            mBtnBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        if (null == mPopupAudioList) {
            mAudioListView = (ListView)mInflater.inflate(R.layout.list_audio, null);
            mPopupAudioList = new PopupWindow(mAudioListView, 350, LayoutParams.WRAP_CONTENT);
            List<Song> songList = AudioLoaderManager.getInstance().getViewSongs();
            mAudioList = new ArrayList<HashMap<String,String>>();
            for (Song song : songList) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("audio_name", song.getFileTitle());
                mAudioList.add(map);
            }
            String[] from = {"audio_name"};
            int[] to = {R.id.song_name};
            mAudioListView.setAdapter(new SimpleAdapter(this, mAudioList, R.layout.simple_audio_list_item, from, to));
            mAudioListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (arg2 != mSongPosition) {
                        playPosition(arg2);
                    }
                    if (mPopupAudioList.isShowing()) {
                        mPopupAudioList.dismiss();
                    }
                }
            });
        }

        if (null == mBtnAudioList) {
            mBtnAudioList = (Button)findViewById(R.id.button_header_list);
            mBtnAudioList.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAudioListView.setSelection(mSongPosition);
                    if (mPopupAudioList != null) {
                        if (mPopupAudioList.isShowing()) {
                            mPopupAudioList.dismiss();
                        } else {
                            mPopupAudioList.setFocusable(true);
                            mPopupAudioList.setBackgroundDrawable(new BitmapDrawable());
                            mPopupAudioList.showAsDropDown(mBtnAudioList, 0, 10);
                        }
                    }
                }
            });
        }
    }

    private void startPlay() {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.PLAY);
        mAudioServiceIntent.putExtra(Constants.MUSIC_SONG_POSITION, mSongPosition);
        startService(mAudioServiceIntent);
    }

    private void playPosition(int position) {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.PLAY_POSITION);
        mAudioServiceIntent.putExtra(Constants.MUSIC_SONG_POSITION, position);
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

    private void playNext() {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.NEXT);
        startService(mAudioServiceIntent);
    }

    private void playPrevious() {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.PREVIOUS);
        startService(mAudioServiceIntent);
    }

    private void setAudioFx(int audioFx) {
        mAudioServiceIntent.putExtra(Constants.PLAYER_MSG, Constants.PlayerCommand.AUDIO_FX);
        mAudioServiceIntent.putExtra(Constants.AUDIO_FX_ID, audioFx);
        startService(mAudioServiceIntent);
    }

    private void updateAudioDuration(int duration) {
        mProgressBar.setMax(duration);
        mTotalTime.setText(progresstime(duration));
    }

    private void updateAudioCurrent(int position) {
        mProgressBar.setProgress(position);
        mPlayingTime.setText(progresstime(position));
        if (mDisplayLyrics) {
            mLrcView.setIndex(lrcIndex(position));
        }
    }

    private void updatePlayPauseBtn(boolean isPlay) {
        // Check the button if the audio is not playing.
        mBtnPlayPause.setChecked(!isPlay);
    }

    private void initPlayTime(int position, int duration) {
        updateAudioDuration(duration);
        updateAudioCurrent(position);
    }

    /**
     * Find the current index of lyric in the mLrcList
     * @param currentTime
     * @return index
     */
    public int lrcIndex(int currentTime) {
        int index = 0;
        for (int i = 0; i < mLrcList.size(); i++) {
            if (i < mLrcList.size() - 1) {
                if (currentTime < mLrcList.get(i).getLrcTime() && i == 0) {
                    index = i;
                }
                if (currentTime > mLrcList.get(i).getLrcTime()
                        && currentTime < mLrcList.get(i + 1).getLrcTime()) {
                    index = i;
                }
            }
            if (i == mLrcList.size() - 1
                    && currentTime > mLrcList.get(i).getLrcTime()) {
                index = i;
            }
        }
        return index;
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
            mService.setOnSongChangedListener(new OnSongChangedListener() {
                @Override
                public void onSongChanged(int newPosition) {
                    init(newPosition);
                    initPlayTime(mService.getAudioCurrent(),mService.getAudioDuration());
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
