package com.byd.audioplayer.bluetooth;


import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.IPlayerService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.audioplayer.BaseActivity;
import com.byd.audioplayer.R;
import com.byd.audioplayer.view.VisualizeView;

public class BTPlayerActivity extends BaseActivity {
    private static final String BTMUSIC = "BTPlayerActivity";

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

    //broadcast for receiving.
    private final String BTSTATUS = "com.byd.player.receiver.action.BTSTATUS";
    
    //BT status.
    private final int BTSTATUS_DISCONNECT = 48;
    private final int BTSTATUS_CONNECTING = 49;
    private final int BTSTATUS_CONNECTED  = 50;
    private final int BTSTATUS_PLAYING    = 51;
    private int btstatus = BTSTATUS_DISCONNECT;
    private static volatile boolean isBTMusicOperation = false;
    
    //broadcast: bt music -> bt phone -> bt driver.
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
    
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {  
	    public void onAudioFocusChange(int focusChange) {  
	        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
	        	Log.d(BTMUSIC, "AUDIOFOCUS_LOSS_TRANSIENT");
	        	BTpause();
				stopPlaybackService("0");
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {  
	            if(false == isBTMusicOperation)
	            {
	                BTpause();
		            Log.d(BTMUSIC, "AUDIOFOCUS_LOSS");
	            }
	            stopPlay();
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
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        play();
        // when go back to bt music, request the play status.
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
        audioManager.abandonAudioFocus(afChangeListener);
        unregisterBroadcast();
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
        if (!sendBTMusicCmd(A2DPCONNECT)){
            //TODO deal with a2dp-connect failed, give a toast?
            Log.e(BTMUSIC, "connect a2dp FAILED!");
        } else {
            Log.i(BTMUSIC, "connect a2dp SUCCESSFULLY!");
            audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
//            audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
//            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
//            audioManager.setBluetoothScoOn(true);
//            audioManager.startBluetoothSco();
//            audioManager.setMode(AudioManager.STREAM_MUSIC);
//            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }

        if (!sendBTMusicCmd(AVRCPCONNECT)){
            Log.w(BTMUSIC, "This phone doesn't support AVRCP!");
        } else {
            Log.i(BTMUSIC, "support AVCRP!");
        }
    }

    protected void initView() {
    	//connect a2dp, if possible, connect avrcp.
        initBTmusic();
        
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
        
        //button event: play&pause.
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
        
        //button event: back.
        if(null == mBtnBack)
        {
        	mBtnBack = (ImageButton) findViewById(R.id.button_audio_header_back);
        	mBtnBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	BTpause();
                	stopPlay();
                    finish();
                }
            });
        }
        
        //button event: 1.click-next song. 2.long click-fast forward.
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

        //button event: 1.click-previous song. 2.long click-fast backward.
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

    //pause the bt music, refresh the play&pause button status.
    private void BTpause() {
        if (!sendBTMusicCmd(PAUSE)){
            Log.e(BTMUSIC, "pause music failed!");
        } else {
            Log.i(BTMUSIC, "music should be paused right now!");
            sendBTMusicCmd(REQUESTSTATUS);
        }
    }
    
    //stop the bt music, refresh the play&pause button status.
    private void BTstop() {
        if (!sendBTMusicCmd(STOP)){
            Log.e(BTMUSIC, "stop music failed!");
        } else {
            Log.i(BTMUSIC, "music should be stop right now!");
            sendBTMusicCmd(REQUESTSTATUS);
        }
    }

    //play the bt music, refresh the play&pause button status.
    private void BTcontinuePlay() {
        if (!sendBTMusicCmd(PLAY)){
            Log.e(BTMUSIC, "play music failed!");
        } else {
        	isBTMusicOperation = true;
        	play();
            Log.i(BTMUSIC, "music should be played right now!" + ": " + isBTMusicOperation);
            sendBTMusicCmd(REQUESTSTATUS);
        }
        
    }

    //play the next bt music, refresh the play&pause button status.
    private void BTplayNext() {
        if (!sendBTMusicCmd(FORWARD)){
            Log.e(BTMUSIC, "next play failed!");
        } else {
            Log.i(BTMUSIC, "next music!");
            sendBTMusicCmd(REQUESTSTATUS);
        }
    }

    //play the previous bt music, refresh the play&pause button status.
    private void BTplayPrevious() {
        if (!sendBTMusicCmd(BACKWARD)){
            Log.e(BTMUSIC, "previous play failed!");
        } else {
            Log.i(BTMUSIC, "previous music!");
            sendBTMusicCmd(REQUESTSTATUS);
        }
    }
    
    //api for sending command to bt phone.
    private boolean sendBTMusicCmd(String BTcmd)
    {
    	Intent intent_toBTphone= new Intent();
    	intent_toBTphone.setAction(BTcmd);
        sendBroadcast(intent_toBTphone);
    	return true;
    }

    //register the broadcast sending to bt phone.
    private void registerBroadcast() {
        mBTStatusReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BTSTATUS);
        registerReceiver(mBTStatusReceiver, filter);
    }

    //deregister the broadcast sending to bt phone.
    private void unregisterBroadcast() {
        unregisterReceiver(mBTStatusReceiver);
    }

    //broadcast receiving response from bt phone.
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
            			break;
            		}
                }
            }
        }
    }

    private void showToast(String info)
    {
    	Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
    
    //api for updating status of play&pause button.
    private void updatePlayPauseBtn(boolean isPlay) {
        mIsPlaying = isPlay;
        setBTPlayPauseIcon();
    }

    //judge if the specified service is running.
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
    
    //update UI of play&pause button.
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
			//when chick back button, stop bt music.
			BTpause();
			stopPlay();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//start playback stream for bt music, invoking system service "PlayerService".
	private void startPlaybackService(String channel){
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
	
	//stop playback stream for bt music, invoking system service "PlayerService".
	private void stopPlaybackService(String channel){
		if(false == isChannelValid(channel))
			return;
		IPlayerService playerService = IPlayerService.Stub.asInterface(ServiceManager.getService("PlayerService"));
		try {
			playerService.stopPlayer(getPackageName(), channel);
			//playing = false;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	//pause playback stream for bt music, invoking system service "PlayerService".
	private void pausePlaybackService(String channel){
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
	
	//judge whether audiochannel is valid: 0-bt music, 1-fm, 2-cmmb, 3-aux.
	private boolean isChannelValid(String channel){
		if(channel.equals("0") || channel.equals("1") || channel.equals("2") || channel.equals("3"))
		{
			return true;
		}else{
			return false;
		}
	}

	//api for bt play.
    private void play() {
        int focus = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN);
        startPlaybackService("0");
    }

    //api for bt stop.
    private void stopPlay() {
        audioManager.abandonAudioFocus(afChangeListener);
        stopPlaybackService("0");
    }
}
