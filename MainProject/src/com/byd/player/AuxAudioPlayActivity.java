
package com.byd.player;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.IPlayerService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AuxAudioPlayActivity extends BaseActivity {

    private TextView mTvAuxStatus;

    private Intent mAudioChannelIntent;

    private AudioManager audioManager;
    
    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                //switchToBTChannel();
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_LOSS");
                stopPlay();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_GAIN");
            	startPlaybackService("3");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_audio);

        mTvAuxStatus = (TextView)findViewById(R.id.aux_status);

        // because of the fail of receiving aux device event, open the aux channel directly.
        Toast.makeText(this, R.string.aux_connected, Toast.LENGTH_LONG).show();

        //mAudioChannelIntent = new Intent(this, AudioChannelService.class);
        //startService(mAudioChannelIntent);
        audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onDestroy() {
        //stopService(mAudioChannelIntent);
        audioManager.abandonAudioFocus(afChangeListener);
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        play();
        //switchToAUXChannel();
        super.onResume();
    }

    public void onBackBtn(View v) {
        //switchToBTChannel();
        stopPlay();
        onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
        {
            //switchToBTChannel();
            stopPlay();
        }
        return super.onKeyDown(keyCode, event);
    }
    

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
	
	private boolean isChannelValid(String channel){
		if(channel.equals("0") || channel.equals("1") || channel.equals("2") || channel.equals("3"))
		{
			return true;
		}else{
			return false;
		}
	}

    private void play() {
        int focus = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN);
        startPlaybackService("3");
    }

    private void stopPlay() {
        audioManager.abandonAudioFocus(afChangeListener);
        stopPlaybackService("3");
    }
}
