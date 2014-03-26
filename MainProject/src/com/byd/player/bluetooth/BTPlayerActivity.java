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
import android.os.IPlayerService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.services.AudioChannelService;
//import com.byd.player.services.AudioChannelService.AudioChannelBinder;
import com.byd.player.view.VisualizeView;

public class BTPlayerActivity extends BaseActivity {
    private static final String BTMUSIC = "BTPlayerActivity";

    private LayoutInflater mInflater;
    
    private PlayerReceiver mBTStatusReceiver;
    
    private ImageButton mBtnBack;
    
    private ImageButton mAudioList;
    
    private ImageView mBtStatus;

    private LinearLayout mBtnPlayPause;
    
    private ImageView mIconPlay;
    
    private TextView mHeaderTitle;

    private VisualizeView mIconPause;
    
    private static boolean mIsPlaying = false;

    private ImageView mBtnNext;

    private ImageView mBtnPrevious;

    private AudioManager audioManager;

    static public AudioChannelService BtChannelSrv;

    private static final String SERVICE_TAG = "audiochannel-bt";
    
    //broadcast for receiving.
    private final String BTSTATUS = "com.byd.player.receiver.action.BTSTATUS";
    
    //BT status.
    private final int BTSTATUS_DISCONNECT = 48;
    private final int BTSTATUS_CONNECTING = 49;
    private final int BTSTATUS_CONNECTED  = 50;
    private final int BTSTATUS_PLAYING    = 51;
    private int btstatus = 48;
    private static volatile boolean isBTMusicOperation = false;
    
    //broadcast for sending.
    private final String REQUESTSTATUS= "com.byd.player.bluetooth.action.REQUIRESTATUS";
    private final String A2DPCONNECT= "com.byd.player.bluetooth.action.A2DPCONNECT";
    private final String AVRCPCONNECT= "com.byd.player.bluetooth.action.AVRCPCONNECT";
    private final String PLAY= "com.byd.player.bluetooth.action.PLAY";
    private final String PAUSE= "com.byd.player.bluetooth.action.PAUSE";
    private final String STOP= "com.byd.player.bluetooth.action.STOP";
    private final String FORWARD= "com.byd.player.bluetooth.action.FORWARD";
    private final String BACKWARD= "com.byd.player.bluetooth.action.BACKWARD";
    private final String FASTFORWARD= "com.byd.player.bluetooth.action.FASTFORWARD";
    private final String FASTFORWARDSTOP= "com.byd.player.bluetooth.action.FASTFORWARDSTOP";
    private final String FASTBACKWARD= "com.byd.player.bluetooth.action.FASTBACKWARD";
    private final String FASTBACKWARDSTOP= "com.byd.player.bluetooth.action.FASTBACKWARDSTOP";
    
    private AudioChannelService audioChannelService = new AudioChannelService();
    
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {  
	    public void onAudioFocusChange(int focusChange) {  
	        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
	            // Pause bt playback
	        	Log.d(BTMUSIC, "AUDIOFOCUS_LOSS_TRANSIENT");
	        	BTpause();
				stopPlaybackService("0");
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {  
	            if(false == isBTMusicOperation)
	            {
	            	audioManager.abandonAudioFocus(afChangeListener); 
	            	// Pause bt playback  
		            Log.d(BTMUSIC, "AUDIOFOCUS_LOSS");
		            BTpause();
					stopPlaybackService("0");
	            }
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {  
	            // Lower the volume  
	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {  
	            // Resume bt playback
	        	Log.d(BTMUSIC, "AUDIOFOCUS_GAIN");
	        	BTcontinuePlay();
				startPlaybackService("0");
	        }
	    }  
	}; 
	
//    private ServiceConnection mBTChannelSrv = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            AudioChannelBinder binder = (AudioChannelBinder) service;
//            BtChannelSrv = binder.getService();
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            BtChannelSrv = null;
//        }
//    };
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        sendBTMusicCmd(REQUESTSTATUS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBTMusicOperation = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        //BTpause();
        
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

        //mInflater = this.getLayoutInflater();
        initView();
        registerBroadcast();
    }
    protected void initBTmusic(){
//    	Intent intent = new Intent("com.byd.player.receiver.action.BTCHANNEL");
//    	sendBroadcast(intent);
    	startPlaybackService("0");

        if (!sendBTMusicCmd(A2DPCONNECT)){
            //TODO deal with a2dp-connect failed, give a toast?
            Log.e(BTMUSIC, "connect a2dp FAILED!");
        } else {
            Log.i(BTMUSIC, "connect a2dp SUCCESSFULLY!");
            audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            audioManager.setBluetoothScoOn(true);
            audioManager.startBluetoothSco();
            audioManager.setMode(AudioManager.STREAM_MUSIC);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

        }

        if (!sendBTMusicCmd(AVRCPCONNECT)){
            Log.w(BTMUSIC, "This phone doesn't support AVRCP!");
        } else {
            Log.i(BTMUSIC, "support AVCRP!");
        }

        //BTcontinuePlay();

    }

    protected void initView() {
//    	mBtStatus = (ImageView)findViewById(R.id.icon_bt);
//    	mBtStatus.setVisibility(View.VISIBLE);
    	
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
          
        
        if(null == mAudioList)
        {
        	mAudioList = (ImageButton)findViewById(R.id.button_header_list);
        	mAudioList.setVisibility(View.INVISIBLE);;
        }
        
        if(null == mHeaderTitle)
        {
        	mHeaderTitle = (TextView)findViewById(R.id.tv_header_title);
        	mHeaderTitle.setText(R.string.bt_title);
        }
        
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
        	mBtnBack = (ImageButton) findViewById(R.id.button_audio_header_back);
        	mBtnBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	BTpause();
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
                    if (!sendBTMusicCmd(FASTFORWARD)){
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
                        if (!sendBTMusicCmd(FASTFORWARDSTOP)){
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
                    if (!sendBTMusicCmd(FASTBACKWARD)){
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
                        if (!sendBTMusicCmd(FASTBACKWARDSTOP)){
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
        if (!sendBTMusicCmd(PAUSE)){
            Log.e(BTMUSIC, "pause music failed!");
        } else {
            Log.i(BTMUSIC, "music should be paused right now!");
            updatePlayPauseBtn(false);
        }
    }
    
    private void BTstop() {
        if (!sendBTMusicCmd(STOP)){
            Log.e(BTMUSIC, "stop music failed!");
        } else {
            Log.i(BTMUSIC, "music should be stop right now!");
            updatePlayPauseBtn(false);
        }
    }

    private void BTcontinuePlay() {
        if (!sendBTMusicCmd(PLAY)){
            Log.e(BTMUSIC, "play music failed!");
        } else {
        	isBTMusicOperation = true;
        	audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN);  
            Log.i(BTMUSIC, "music should be played right now!" + ": " + isBTMusicOperation);
            updatePlayPauseBtn(true);
        }
        
    }

    private void BTplayNext() {
        if (!sendBTMusicCmd(FORWARD)){
            Log.e(BTMUSIC, "next play failed!");
        } else {
            Log.i(BTMUSIC, "next music!");
        }
    }

    private void BTplayPrevious() {
        if (!sendBTMusicCmd(BACKWARD)){
            Log.e(BTMUSIC, "previous play failed!");
        } else {
            Log.i(BTMUSIC, "previous music!");
        }
    }
    
    private boolean sendBTMusicCmd(String BTcmd)
    {
    	Intent intent_toBTphone= new Intent();
    	intent_toBTphone.setAction(BTcmd);
        sendBroadcast(intent_toBTphone);
    	return true;
    }

    private void registerBroadcast() {
        mBTStatusReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BTSTATUS);
        registerReceiver(mBTStatusReceiver, filter);
    }

    private void unregisterBroadcast() {
        unregisterReceiver(mBTStatusReceiver);
    }

    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BTSTATUS.equals(action))
            {
            	if (intent.hasExtra("a2dp")) {
            		switch(intent.getIntExtra("a2dp", 0)){
            		case BTSTATUS_DISCONNECT:
            			btstatus = BTSTATUS_DISCONNECT;
            			updatePlayPauseBtn(false);
            			showToast(getString(R.string.bt_plsconnect));
            			break;
            		case BTSTATUS_CONNECTING:
            			btstatus = BTSTATUS_CONNECTING;
            			updatePlayPauseBtn(false);
            			showToast(getString(R.string.bt_plsconnect));
            			break;
            		case BTSTATUS_CONNECTED:
            			btstatus = BTSTATUS_CONNECTED;
            			updatePlayPauseBtn(false);
            			showToast(getString(R.string.bt_connected));
            			break;
            		case BTSTATUS_PLAYING:
            			btstatus = BTSTATUS_PLAYING;
            			updatePlayPauseBtn(true);
            			showToast(getString(R.string.bt_connected));
            			break;
            		default:
            			btstatus = BTSTATUS_DISCONNECT;
            			showToast(getString(R.string.bt_plsconnect));
            		}
                }
            }
        }
    }

    private void showToast(String info)
    {
    	Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
    
    private void updatePlayPauseBtn(boolean isPlay) {
        mIsPlaying = isPlay;
        setBTPlayPauseIcon();
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
    
    private void setBTPlayPauseIcon() {
        if (mIsPlaying) {
            mIconPause.setVisibility(View.VISIBLE);
            mIconPlay.setVisibility(View.GONE);
        } else {
            mIconPlay.setVisibility(View.VISIBLE);
            mIconPause.setVisibility(View.GONE);
        }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode)
		{
			BTpause();
			stopPlaybackService("0");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void startPlaybackService(String channel){
		if(false == isChannelValid(channel))
			return;
		IPlayerService playerService = IPlayerService.Stub.asInterface(ServiceManager.getService("PlayerService"));
		try {
			playerService.startPlayer(getPackageName(), channel);
			//playing = true;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void stopPlaybackService(String channel){
		if(false == isChannelValid(channel))
			return;
		IPlayerService playerService = IPlayerService.Stub.asInterface(ServiceManager.getService("PlayerService"));
		try {
			playerService.stopPlayer(getPackageName(), "1");
			//playing = false;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void pausePlaybackService(String channel){
		if(false == isChannelValid(channel))
			return;
		IPlayerService playerService = IPlayerService.Stub.asInterface(ServiceManager.getService("PlayerService"));
		try {
			playerService.pausePlayer(getPackageName(), channel);
			//playing = false;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isChannelValid(String channel){
		if(channel.equals("0") || channel.equals("1") || channel.equals("2") || channel.equals("3"))
		{
			return true;
		}else{
			return false;
		}
	}
	
}
