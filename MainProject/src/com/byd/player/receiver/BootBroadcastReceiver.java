package com.byd.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byd.player.services.AuxAudioService;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        // TODO Auto-generated method stub
    	
    	//1. start bt service.
//        Intent service = new Intent(context, BtService.class);
//        context.startService(service);
        
        //2. start aux service.
        Intent auxService = new Intent(context, AuxAudioService.class);
        context.startService(auxService);
    }
}