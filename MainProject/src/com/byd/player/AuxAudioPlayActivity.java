
package com.byd.player;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.player.config.Constants;
import com.byd.player.receiver.AudioChannelBroadcastReceiver;
import com.byd.player.services.AudioChannelService;

public class AuxAudioPlayActivity extends BaseActivity {

    private TextView mTvAuxStatus;

    private Intent mAudioChannelIntent;

    private AudioManager audioManager;

    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                switchToBTChannel();
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

        mAudioChannelIntent = new Intent(this, AudioChannelService.class);
        startService(mAudioChannelIntent);
    }

    @Override
    protected void onDestroy() {
        stopService(mAudioChannelIntent);
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {

        audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN);
        switchToAUXChannel();
        super.onResume();
    }

    private void switchToBTChannel()
    {
        Intent setChannel = new Intent(
                AudioChannelBroadcastReceiver.ACTION_SWITCH_TO_BT_CHANNEL);
        sendBroadcast(setChannel);
        audioManager.abandonAudioFocus(afChangeListener);
    }

    private void switchToAUXChannel()
    {
        Intent setChannel = new Intent(
                AudioChannelBroadcastReceiver.ACTION_SWITCH_TO_AUX_CHANNEL);
        sendBroadcast(setChannel);
    }

    public void onBackBtn(View v) {
        switchToBTChannel();
        onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
        {
            switchToBTChannel();
        }
        return super.onKeyDown(keyCode, event);
    }
}
