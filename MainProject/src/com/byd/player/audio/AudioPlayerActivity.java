package com.byd.player.audio;

import android.os.Bundle;
import android.view.WindowManager;

import com.byd.player.BaseActivity;
import com.byd.player.R;

public class AudioPlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.audio_player_view);
    }
}
