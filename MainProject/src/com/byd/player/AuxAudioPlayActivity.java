
package com.byd.player;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.byd.player.receiver.DeviceConnReceiver;
import com.byd.player.receiver.DeviceConnReceiver.AuxConnectListener;

public class AuxAudioPlayActivity extends BaseActivity {

    private DeviceConnReceiver mDeviceConnReceiver;

    private TextView mTvAuxStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_audio);

        mTvAuxStatus = (TextView)findViewById(R.id.aux_status);

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
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mDeviceConnReceiver);
        super.onDestroy();
    }

}
