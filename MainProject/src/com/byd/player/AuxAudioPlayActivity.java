
package com.byd.player;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.player.bluetooth.BTPlayerActivity;
import com.byd.player.config.Constants;
import com.byd.player.receiver.AudioChannelBroadcastReceiver;
import com.byd.player.receiver.DeviceConnReceiver;
import com.byd.player.receiver.DeviceConnReceiver.AuxConnectListener;
import com.byd.player.services.AudioChannelService;

public class AuxAudioPlayActivity extends BaseActivity {

    private DeviceConnReceiver mDeviceConnReceiver;

    private static final String SERVICE_TAG = "audiochannel-aux";

    private TextView mTvAuxStatus;

    private Intent mAudioChannelIntent;
    
    private AudioManager audioManager;
    
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {  
	    public void onAudioFocusChange(int focusChange) {  
	        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
	        	
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {  
	            
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {  
	        	
	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {  
	        	
	        }
	    }  
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_audio);

        mTvAuxStatus = (TextView)findViewById(R.id.aux_status);

        if (getIntent().getBooleanExtra(Constants.IS_AUX_CONNECTED, false)) {
            mTvAuxStatus.setText(R.string.aux_connected);
        }

        // because of the fail of receiving aux device event, open the aux channel directly.
        Toast.makeText(this, "已切换至aux声道", Toast.LENGTH_LONG).show();
        Intent setChannel = new Intent(
                AudioChannelBroadcastReceiver.ACTION_SWITCH_TO_AUX_CHANNEL);
        sendBroadcast(setChannel);
        //audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
    	//audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN);  
//    	Intent intent_toBTphone= new Intent();
//    	intent_toBTphone.setAction("com.byd.player.bluetooth.action.PLAY");
//        sendBroadcast(intent_toBTphone);
        
        mDeviceConnReceiver = new DeviceConnReceiver(new AuxConnectListener() {

            @Override
            public void onDisconnected() {
                mTvAuxStatus.setText(R.string.aux_disconnected);
                stopService(mAudioChannelIntent);
            }

            @Override
            public void onConnected() {
                mTvAuxStatus.setText(R.string.aux_connected);
                startService(mAudioChannelIntent);
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mDeviceConnReceiver, intentFilter);

        
        // TODO: Need start service when aux device connected
        mAudioChannelIntent = new Intent(this, AudioChannelService.class);
        mAudioChannelIntent.putExtra("service_tag", SERVICE_TAG);
        // startService(mAudioChannelIntent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mDeviceConnReceiver);
//        Toast.makeText(this, "已切换至蓝牙声道", Toast.LENGTH_LONG).show();
//        Intent setChannel = new Intent(
//                AudioChannelBroadcastReceiver.ACTION_SWITCH_TO_BT_CHANNEL);
//        sendBroadcast(setChannel);
        //stopService(mAudioChannelIntent);
        super.onDestroy();
    }

    public void onBackBtn(View v) {
        onBackPressed();
    }
}
