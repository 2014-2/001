package com.byd.player.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Intent service = new Intent(context, BtService.class); 
        context.startService(service);  
        Log.v("BtService", "BtService开机启动");  
	}
}