package com.byd.player.receiver;

import com.byd.player.services.AudioChannelService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AudioChannelBroadcastReceiver extends BroadcastReceiver{
	private final String ACTION_SWITCH_TO_BT_CHANNEL = "com.byd.player.receiver.action.BTCHANNEL";
	private final String ACTION_SWITCH_TO_AUX_CHANNEL = "com.byd.player.receiver.action.AUXCHANNEL";
	private final String SERVICE_TAG_BT = "audiochannel-bt";
	private final String SERVICE_TAG_AUX = "audiochannel-aux";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		AudioChannelService audioChannelService = new AudioChannelService();
		if (action.equals(ACTION_SWITCH_TO_BT_CHANNEL)){
			Log.d("AudioChannelBroadcastReceiver", "audiochannel: bt.");
			audioChannelService.selectChannel(SERVICE_TAG_BT);
		} 
		else if (action.equals(ACTION_SWITCH_TO_AUX_CHANNEL)) {
			Log.d("AudioChannelBroadcastReceiver", "audiochannel: aux.");
			audioChannelService.selectChannel(SERVICE_TAG_AUX);
		}
	}
}
