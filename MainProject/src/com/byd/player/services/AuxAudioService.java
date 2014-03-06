package com.byd.player.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.byd.player.AuxAudioPlayActivity;
import com.byd.player.config.Constants;
import com.byd.player.receiver.DeviceConnReceiver;
import com.byd.player.receiver.DeviceConnReceiver.AuxConnectListener;

public class AuxAudioService extends Service {
    private static final boolean DEBUG = true;

    private static final String SERVICE_TAG = "audiochannel-aux";

    private DeviceConnReceiver mDeviceConnReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!DEBUG) {
            mDeviceConnReceiver = new DeviceConnReceiver(new AuxConnectListener() {

                @Override
                public void onDisconnected() {
                }

                @Override
                public void onConnected() {
                    startAudioChannelService();
                    // Start AuxAudioPlayActivity
                    Intent activityIntent = new Intent(AuxAudioService.this,
                            AuxAudioPlayActivity.class);
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activityIntent.putExtra(Constants.IS_AUX_CONNECTED, true);
                    AuxAudioService.this.startActivity(activityIntent);
                }
            });
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(mDeviceConnReceiver, intentFilter);
        } else {
            startAudioChannelService();
        }
    }

    private void startAudioChannelService() {
        Intent service = new Intent(this, AudioChannelService.class);
        service.putExtra("service_tag", SERVICE_TAG);
        startService(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDeviceConnReceiver);
        // Make sure the service is always running
        startService(new Intent(this, AuxAudioService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
