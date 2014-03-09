
package com.byd.player;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.byd.player.config.Constants;
import com.byd.player.receiver.DeviceConnReceiver;
import com.byd.player.receiver.DeviceConnReceiver.AuxConnectListener;
import com.byd.player.services.AudioChannelService;

public class AuxAudioPlayActivity extends BaseActivity {

    private DeviceConnReceiver mDeviceConnReceiver;

    private static final String SERVICE_TAG = "audiochannel-aux";

    private TextView mTvAuxStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_audio);

        mTvAuxStatus = (TextView)findViewById(R.id.aux_status);

        if (getIntent().getBooleanExtra(Constants.IS_AUX_CONNECTED, false)) {
            mTvAuxStatus.setText(R.string.aux_connected);
        }

        mDeviceConnReceiver = new DeviceConnReceiver(new AuxConnectListener() {

            @Override
            public void onDisconnected() {
                mTvAuxStatus.setText(R.string.aux_disconnected);
            }

            @Override
            public void onConnected() {
                mTvAuxStatus.setText(R.string.aux_connected);
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mDeviceConnReceiver, intentFilter);

        // TODO: Need start service when aux device connected
        Intent service = new Intent(this, AudioChannelService.class);
        service.putExtra("service_tag", SERVICE_TAG);
        startService(service);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mDeviceConnReceiver);
        super.onDestroy();
    }

}
