package com.byd.audioplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.byd.audioplayer.audio.AudioListActivity;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, AudioListActivity.class));
        finish();
    }

}
