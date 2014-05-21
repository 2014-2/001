
package com.byd.audioplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IPlayerService;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.byd.audioplayer.utils.ToastUtils;

public class AuxAudioPlayActivity extends BaseActivity {
    private static final String TAG = AuxAudioPlayActivity.class.getSimpleName();

    private TextView mTvAuxStatus;

    private Intent mAudioChannelIntent;

    private AudioManager audioManager;

    private Thread AuxCheckThread;

    private boolean lastTimeAuxStatus = false;

    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                //switchToBTChannel();
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_LOSS");
                stopPlay();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d("AuxAudioPlayActivity", "AUDIOFOCUS_GAIN");
                startPlaybackService("3");
            }
        }
    };

    private Handler AuxStatusHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            boolean isAuxConnected = (Boolean)msg.obj;
            if (isAuxConnected != lastTimeAuxStatus) {
                if (isAuxConnected) {
                    mTvAuxStatus.setText(R.string.aux_connected);
                    ToastUtils.showToast(getApplicationContext(), R.string.aux_connected);
                } else {
                    mTvAuxStatus.setText(R.string.aux_disconnected);
                }
            }
            lastTimeAuxStatus = isAuxConnected;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aux_audio);

        mTvAuxStatus = (TextView)findViewById(R.id.aux_status);

        if (AuxCheckThread == null || !AuxCheckThread.isAlive()) {
            AuxCheckThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (true) {
                            Message msg = Message.obtain();
                            msg.obj = checkAux();
                            AuxStatusHandler.sendMessage(msg);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            AuxCheckThread.start();
        }

        //mAudioChannelIntent = new Intent(this, AudioChannelService.class);
        //startService(mAudioChannelIntent);
        audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onDestroy() {
        //stopService(mAudioChannelIntent);
        audioManager.abandonAudioFocus(afChangeListener);
        if (AuxCheckThread != null) {
            AuxCheckThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        play();
        //switchToAUXChannel();
        super.onResume();
    }

    public void onBackBtn(View v) {
        //switchToBTChannel();
        stopPlay();
        onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
        {
            //switchToBTChannel();
            stopPlay();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void startPlaybackService(String channel){
        if (false == isChannelValid(channel))
            return;
        IPlayerService playerService = IPlayerService.Stub.asInterface(ServiceManager
                .getService("PlayerService"));
        try {
            playerService.startPlayer(getPackageName(), channel);
            // playing = true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaybackService(String channel){
        if (false == isChannelValid(channel))
            return;
        IPlayerService playerService = IPlayerService.Stub.asInterface(ServiceManager
                .getService("PlayerService"));
        try {
            playerService.stopPlayer(getPackageName(), channel);
            // playing = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void pausePlaybackService(String channel){
        if (false == isChannelValid(channel))
            return;
        IPlayerService playerService = IPlayerService.Stub.asInterface(ServiceManager
                .getService("PlayerService"));
        try {
            playerService.pausePlayer(getPackageName(), channel);
            // playing = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean isChannelValid(String channel){
        if(channel.equals("0") || channel.equals("1") || channel.equals("2") || channel.equals("3"))
        {
            return true;
        }else{
            return false;
        }
    }

    private void play() {
        int focus = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN);
        startPlaybackService("3");
    }

    private void stopPlay() {
        audioManager.abandonAudioFocus(afChangeListener);
        stopPlaybackService("3");
    }

    private boolean checkAux() {
        File gpio = new File("/sys/kernel/debug/gpio");
        if (gpio.exists() && gpio.isFile()) {
            String content = "";
            try {
                InputStream instream = new FileInputStream(gpio);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    // 分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            Log.d(TAG, "gpio:" + content);
            int index = content.indexOf("(aux_det             ) in  hi");
            if (index >= 0) {
                return true;
            }
        }
        return false;
    }
}
