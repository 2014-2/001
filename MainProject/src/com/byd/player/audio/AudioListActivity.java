package com.byd.player.audio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.bluetooth.BlueToothPlayActivity;

public class AudioListActivity extends BaseActivity implements OnItemClickListener {
    private GridView mAudioList = null;
    private TextView mPhoneMusic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AudioManager.getInstance().init(getApplicationContext());

        setContentView(R.layout.audio_list_view);

        mPhoneMusic = (TextView)findViewById(R.id.btn_audio_mobile);
        mPhoneMusic.setClickable(true);
        mPhoneMusic.setFocusable(true);
        mPhoneMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
       	     	intent.setClass(AudioListActivity.this,BlueToothPlayActivity.class);
       	     	startActivity(intent);
			}
		});
        
        mAudioList = (GridView) findViewById(R.id.audio_grid_list);
        BaseAdapter adapter = new AudioAdapter(this, getLayoutInflater());
        mAudioList.setAdapter(adapter);
        mAudioList.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        startActivity(intent);
    }

}
