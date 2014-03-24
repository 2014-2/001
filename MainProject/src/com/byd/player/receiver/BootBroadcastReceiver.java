package com.byd.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        // TODO Auto-generated method stub

        //1. start bt service.
        //        Intent service = new Intent(context, BtService.class);
        //        context.startService(service);

    }
}