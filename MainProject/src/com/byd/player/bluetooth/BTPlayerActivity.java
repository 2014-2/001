package com.byd.player.bluetooth;


import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.bluetooth.BtActionManager.BtCmdEnum;
import com.byd.player.services.AudioChannelService;
import com.byd.player.services.AudioChannelService.AudioChannelBinder;
import com.byd.player.services.BtService;
import com.byd.player.video.VideoView;
import com.byd.player.view.CheckableImageView;
import com.byd.player.view.VisualizeView;

public class BTPlayerActivity extends BaseActivity {
    private static final String BTMUSIC = "BTPlayerActivity";
    BtService btService = com.byd.player.bluetooth.BTBaseActivity.btService;
    /* if we can ge ID3 info, this is useful.
    private Song mPlayingSong;
     */

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
    
    private ImageButton mBtnBack;
    
    private ImageView mBtStatus;

    private LinearLayout mBtnPlayPause;
    
    private ImageView mIconPlay;

    private VisualizeView mIconPause;
    
    private boolean mIsPlaying = false;

    private ImageView mBtnNext;

    private ImageView mBtnPrevious;

    private LayoutInflater mInflater;

    private PlayerReceiver mPlayerReceiver;

    private Intent mAudioServiceIntent;
    
    private AudioManager audioManager;

    static public AudioChannelService BtChannelSrv;

    private static final String SERVICE_TAG = "audiochannel-bt";

    private boolean isChannelSrvBinded=false;
    
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {  
	    public void onAudioFocusChange(int focusChange) {  
	        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
	            // Pause bt playback 
	            BTpause();
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {  
	            audioManager.abandonAudioFocus(afChangeListener);  
	            // Pause bt playback  
	            BTpause();
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {  
	            // Lower the volume  
	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {  
	            // Resume bt playback  
	        	BTcontinuePlay();
	        }
	    }  
	}; 
	
    private ServiceConnection mBTChannelSrv = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AudioChannelBinder binder = (AudioChannelBinder) service;
            BtChannelSrv = binder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            BtChannelSrv = null;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //BTpause();
        unregisterBroadcast();
        /*
        if (mBTChannelSrv != null){
			unbindService(mBTChannelSrv);
			isChannelSrvBinded = false;
		}
         */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.bt_music_play);

        mInflater = this.getLayoutInflater();
        initView();
        registerBroadcast();
    }
    protected void initBTmusic(){
        Intent service = new Intent(this, AudioChannelService.class);
        service.putExtra("service_tag", SERVICE_TAG);
        //this.startService(service);
        //isChannelSrvBinded = bindService(service, mBTChannelSrv, Context.BIND_AUTO_CREATE);

        if (!btService.doAction(BtCmdEnum.BT_CMD_CONNECT_A2DP)){
            //TODO deal with a2dp-connect failed, give a toast?
            Log.e(BTMUSIC, "connect a2dp FAILED!");
        } else {
            Log.i(BTMUSIC, "connect a2dp SUCCESSFULLY!");
            audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setBluetoothScoOn(true);
            audioManager.startBluetoothSco();
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

        }

        if (!btService.doAction(BtCmdEnum.BT_CMD_AVRCP_CONECT)){
            Log.w(BTMUSIC, "This phone doesn't support AVRCP!");
        } else {
            Log.i(BTMUSIC, "support AVCRP!");
        }
        if (!btService.doAction(BtCmdEnum.BT_CMD_ID3_SUPPORT_INDICATION)){
            Log.w(BTMUSIC, "The device doesn't support ID3 info!");
        } else {
            Log.i(BTMUSIC, "support ID3 info!");
        }
        //BTcontinuePlay();

    }

    protected void initView() {
    	mBtStatus = (ImageView)findViewById(R.id.icon_bt);
    	mBtStatus.setVisibility(View.VISIBLE);
    	
        initBTmusic();
        /*
        if (null == mSongInfoAndLyricsContainer) {
            mSongInfoAndLyricsContainer = (LinearLayout)findViewById(R.id.ll_song_info_and_lyrics);
        }
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

        if (null == mTotalTime) {
            mTotalTime = (TextView)findViewById(R.id.audio_total_time);
        }
        if (null == mPlayingTime) {
            mPlayingTime = (TextView)findViewById(R.id.audio_playing_time);
        }
        */
        
        if (null == mIconPlay) {
            mIconPlay = (ImageView)findViewById(R.id.audio_play);
        }

        if (null == mIconPause) {
            mIconPause = (VisualizeView)findViewById(R.id.audio_pause);
        }
        
        if (null == mBtnPlayPause) {
            mBtnPlayPause = (LinearLayout)findViewById(R.id.btn_audio_play_pause);
            mBtnPlayPause.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mIsPlaying) {
                        BTcontinuePlay();
                    } else {
                        BTpause();
                    }
                }
            });
        }
        
        if(null == mBtnBack)
        {
        	mBtnBack = (ImageButton) findViewById(R.id.btn_reback);
        	mBtnBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        
        if (null == mBtnNext) {
            mBtnNext = (ImageView)findViewById(R.id.btn_audio_next);
            mBtnNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    BTplayNext();
                }
            });
            mBtnNext.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    if (!btService.doAction(BtCmdEnum.BT_CMD_FAST_FORWARD)){
                        Log.w(BTMUSIC, "fast forward failed!");
                    } else {
                        Log.i(BTMUSIC, "fast forward successfully!");
                    }
                    return true;
                }
            });

            mBtnNext.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction()== MotionEvent.ACTION_UP){
                        if (!btService.doAction(BtCmdEnum.BT_CMD_FAST_FORWARD_STOP)){
                            Log.w(BTMUSIC, "fast forward stop failed!");
                        } else {
                            Log.i(BTMUSIC, "fast forward stop successfully!");
                        }
                    }
                    return false;
                }
            });
        }

        if (null == mBtnPrevious) {
            mBtnPrevious = (ImageView)findViewById(R.id.btn_audio_previous);
            mBtnPrevious.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    BTplayPrevious();
                }
            });
            mBtnPrevious.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    if (!btService.doAction(BtCmdEnum.BT_CMD_FAST_BACKWARD)){
                        Log.w(BTMUSIC, "fast backward failed!");
                    } else {
                        Log.i(BTMUSIC, "fast backward successfully!");
                    }
                    return true;
                }
            });

            mBtnPrevious.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction()== MotionEvent.ACTION_UP){
                        if (!btService.doAction(BtCmdEnum.BT_CMD_FAST_BACKWARD_STOP)){
                            Log.w(BTMUSIC, "fast backward stop failed!");
                        } else {
                            Log.i(BTMUSIC, "fast backward stop successfully!");
                        }
                    }
                    return false;
                }
            });
        }
    }

    private void BTpause() {
        if (!btService.doAction(BtCmdEnum.BT_CMD_AV_PAUSE)){
            Log.e(BTMUSIC, "pause music failed!");
        } else {
            Log.i(BTMUSIC, "music should be paused right now!");
            audioManager.abandonAudioFocus(afChangeListener);
        }
        updatePlayPauseBtn(false);
    }

    private void BTcontinuePlay() {
        if (!btService.doAction(BtCmdEnum.BT_CMD_AV_PLAY)){
            Log.e(BTMUSIC, "play music failed!");
        } else {
            Log.i(BTMUSIC, "music should be played right now!");
            audioManager.requestAudioFocus(afChangeListener, 
		            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        }
        updatePlayPauseBtn(true);
    }

    private void BTplayNext() {
        if (!btService.doAction(BtCmdEnum.BT_CMD_AV_FORWARD)){
            Log.e(BTMUSIC, "next play failed!");
        } else {
            Log.i(BTMUSIC, "next music!");
        }
    }

    private void BTplayPrevious() {
        if (!btService.doAction(BtCmdEnum.BT_CMD_AV_BACKWARD)){
            Log.e(BTMUSIC, "previous play failed!");
        } else {
            Log.i(BTMUSIC, "previous music!");
        }
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
        }
    }

    private void updatePlayPauseBtn(boolean isPlay) {
        mIsPlaying = isPlay;
        setPlayPauseIcon(isPlay);
    }

    public boolean isServiceRunning(Context mContext,String className){
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
        = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    
    private void setPlayPauseIcon(boolean isPlaying) {
        if (isPlaying) {
            mIconPause.setVisibility(View.VISIBLE);
            mIconPlay.setVisibility(View.GONE);
        } else {
            mIconPlay.setVisibility(View.VISIBLE);
            mIconPause.setVisibility(View.GONE);
        }
    }
}
