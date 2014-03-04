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
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.byd.player.BaseActivity;
import com.byd.player.bluetooth.BTChannelService.BTChannelBinder;
import com.byd.player.bluetooth.BtActionManager.BtCmdEnum;
import com.byd.player.R;
import com.byd.player.view.CheckableImageView;
import com.byd.player.bluetooth.BtService;

public class BTPlayerActivity extends BaseActivity {
	private static final String BTMUSIC = "BTPlayerActivity";
	BtService btService = com.byd.player.bluetooth.BaseActivity.btService;
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

    private CheckableImageView mBtnPlayPause;

    private ImageView mBtnNext;

    private ImageView mBtnPrevious;

    private LayoutInflater mInflater;

    private PlayerReceiver mPlayerReceiver;

    private Intent mAudioServiceIntent;
    
    static public BTChannelService BtChannelSrv;
    private boolean isBtChannelSrvBinded=false;
    
	private ServiceConnection mBTChannelSrv = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	    	BTChannelBinder binder = (BTChannelBinder) service;
	        BtChannelSrv = binder.getService();
	    }
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
        unregisterBroadcast();
        /*
        if (mBTChannelSrv != null && isBtChannelSrvBinded == true){
			unbindService(mBTChannelSrv);
			isBtChannelSrvBinded = false;
		}
        */
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.audio_player_view);

        mInflater = this.getLayoutInflater();
        initView();
        registerBroadcast();
    }
	protected void initBTmusic(){
		if(false == isServiceRunning(this, "com.byd.player.bluetooth.BTChannelService")){
			Intent service = new Intent(this, BTChannelService.class); 
			this.startService(service); 
			isBtChannelSrvBinded=bindService(service, mBTChannelSrv, Context.BIND_AUTO_CREATE);
		}
		 
        Log.v("BtService", "BtService¿ª»úÆô¶¯");  
    	if (!btService.doAction(BtCmdEnum.BT_CMD_CONNECT_A2DP)){
			//TODO deal with a2dp-connect failed, give a toast?
    		Log.e(BTMUSIC, "connect a2dp FAILED!");
		} else {
			Log.i(BTMUSIC, "connect a2dp SUCCESSFULLY!");
			AudioManager mAudioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
			mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
			mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
			boolean a = mAudioManager.isBluetoothA2dpOn();
			boolean b = mAudioManager.isBluetoothScoOn();
			boolean c = mAudioManager.isMusicActive();
			boolean d = mAudioManager.isSpeakerphoneOn();
			mAudioManager.setMode(AudioManager.MODE_IN_CALL);
			mAudioManager.setBluetoothScoOn(true);
			mAudioManager.startBluetoothSco();
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
    	initBTmusic();
    	
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
            /*
            mAlbumName.setText(mPlayingSong.getAlbum());
            mSingerName.setText(mPlayingSong.getSinger());
            mMusicName.setText(mPlayingSong.getFileTitle());
            */
        }

        if (null == mTotalTime) {
            mTotalTime = (TextView)findViewById(R.id.audio_total_time);
        }
        if (null == mPlayingTime) {
            mPlayingTime = (TextView)findViewById(R.id.audio_playing_time);
        }
        
        if (null == mBtnPlayPause) {
            mBtnPlayPause = (CheckableImageView)findViewById(R.id.btn_audio_play_pause);
            mBtnPlayPause.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mBtnPlayPause.isChecked()) {
                        BTcontinuePlay();
                    } else {
                        BTpause();
                    }
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

    private boolean hasLyrics() {
        // TODO: Add lyrics check
        return false;
    }

    private void BTpause() {
		if (!btService.doAction(BtCmdEnum.BT_CMD_AV_PAUSE)){
    		Log.e(BTMUSIC, "pause music failed!");
		} else {
			Log.i(BTMUSIC, "music should be paused right now!");
		}
		updatePlayPauseBtn(false);
    }

    private void BTcontinuePlay() {
		if (!btService.doAction(BtCmdEnum.BT_CMD_AV_PLAY)){
    		Log.e(BTMUSIC, "play music failed!");
		} else {
			Log.i(BTMUSIC, "music should be played right now!");
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
        // Check the button if the audio is not playing.
        mBtnPlayPause.setChecked(!isPlay);
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
}
