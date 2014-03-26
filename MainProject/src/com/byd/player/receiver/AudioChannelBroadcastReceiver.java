package com.byd.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.byd.player.services.AudioChannelService;

public class AudioChannelBroadcastReceiver extends BroadcastReceiver{
    public static final String ACTION_SWITCH_TO_BT_CHANNEL = "com.byd.player.receiver.action.BTCHANNEL";

    public static final String ACTION_SWITCH_TO_AUX_CHANNEL = "com.byd.player.receiver.action.AUXCHANNEL";
    private final String SERVICE_TAG_BT = "audiochannel-bt";
    private final String SERVICE_TAG_AUX = "audiochannel-aux";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AudioChannelService audioChannelService = new AudioChannelService();
        if (action.equals(ACTION_SWITCH_TO_BT_CHANNEL)){
//            Log.d("AudioChannelBroadcastReceiver", "audiochannel: bt.");
//            try {
//				audioChannelService.startPlayAudio(SERVICE_TAG_BT);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
        else if (action.equals(ACTION_SWITCH_TO_AUX_CHANNEL)) {
//            Log.d("AudioChannelBroadcastReceiver", "audiochannel: aux.");
//            try {
//				audioChannelService.startPlayAudio(SERVICE_TAG_AUX);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
    }
}
