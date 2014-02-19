
package com.byd.player.auxaudio;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.byd.player.BaseActivity;
import com.byd.player.R;

public class AuxAudioActivity extends BaseActivity {
    private AuxPlugReceiver auxPlugReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_audio);

        // register aux plug receiver
        registerAuxPlugReceiver();
    }

    private void registerAuxPlugReceiver() {
        auxPlugReceiver = new AuxPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();

        // use headset to test the receiver
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(auxPlugReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(auxPlugReceiver);
        super.onDestroy();
    }
}
