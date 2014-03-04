package com.byd.player.audio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.bluetooth.ConnectActivity;
import com.byd.player.config.Constants;
import com.byd.player.services.AuxAudioService;

public class AudioListActivity extends BaseActivity implements OnItemClickListener {
    private final static String TAG = "AudioListActivity";
    public final static int TAB_INDEX_LOCAL = 0;
    public final static int TAB_INDEX_SDCARD = 1;
    public final static int TAB_INDEX_USB = 2;
    public final static int TAB_INDEX_AUX = 3;
    public final static int TAB_INDEX_MOBILE = 4;

    public final static int MODE_NORMAL = 0;
    public final static int MODE_EDIT = MODE_NORMAL + 1;

    private final int[] TAB_IDS = new int[] { R.id.btn_audio_Local, R.id.btn_audio_sdcard,
            R.id.btn_audio_usb, R.id.btn_audio_aux, R.id.btn_audio_mobile };

    private GridView mAudioList = null;
    private AudioAdapter mAdapter = null;

    private TextView mAuxStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AudioManager.getInstance().init(getApplicationContext());
        AudioManager.getInstance().loadData(AudioManager.EXTERNAL_TYPE);
        AudioManager.getInstance().loadData(AudioManager.INTERNAL_TYPE);

        setContentView(R.layout.audio_list_view);

        initViews();
        startService(new Intent(this, AuxAudioService.class));
    }

    private void initViews() {
        mAudioList = (GridView) findViewById(R.id.audio_grid_list);
        mAdapter = new AudioAdapter(this, getLayoutInflater());
        mAudioList.setAdapter(mAdapter);
        mAudioList.setOnItemClickListener(this);

        initHeaderButtons();
        initBottomButtons();
    }

    private void initHeaderButtons() {
        mAuxStatus = (TextView) findViewById(R.id.audio_aux_status);

        Button back = (Button) findViewById(R.id.button_header_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioListActivity.this.finish();
            }
        });

        Button edit = (Button) findViewById(R.id.button_header_edit);
        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(MODE_EDIT);
            }
        });

        Button delete = (Button) findViewById(R.id.button_header_delete);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isEditMode()) {
                    Toast.makeText(AudioListActivity.this, "选项待删除....", Toast.LENGTH_SHORT).show();
                    setMode(MODE_NORMAL);
                }
            }
        });
    }

    private void initBottomButtons() {
        for (int i = 0; i < TAB_IDS.length; i++) {
            findViewById(TAB_IDS[i]).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = TAB_INDEX_LOCAL;
                    for (int i = 0; i < TAB_IDS.length; i++) {
                        if (TAB_IDS[i] == v.getId()) {
                            index = i;
                        }
                    }

                    switch (index) {
                        case TAB_INDEX_MOBILE:
                            Intent intent = new Intent();
                            intent.setClass(AudioListActivity.this, ConnectActivity.class);
                            startActivity(intent);
                            break;
                        default:
                            tabIndex(index);
                            break;
                    }

                }
            });
        }
        tabIndex(TAB_INDEX_LOCAL);
    }

    private void setMode(int mode) {
        if (!mAdapter.isEditMode() && mAdapter.getCount() > 0) {
            findViewById(R.id.button_header_delete).setVisibility(View.VISIBLE);
            findViewById(R.id.button_header_edit).setVisibility(View.GONE);
            mAdapter.setMode(MODE_EDIT);
        } else {
            findViewById(R.id.button_header_delete).setVisibility(View.GONE);
            findViewById(R.id.button_header_edit).setVisibility(View.VISIBLE);
            mAdapter.setMode(MODE_NORMAL);
        }
    }

    private void hideViews() {
        mAudioList.setVisibility(View.GONE);
        mAuxStatus.setVisibility(View.GONE);
    }

    public void tabIndex(int index) {
        AudioManager.getInstance().setViewType(index);
        switch (index) {
            case TAB_INDEX_LOCAL:
            case TAB_INDEX_SDCARD:
            case TAB_INDEX_USB:
                if (mAudioList.getVisibility() != View.VISIBLE) {
                    hideViews();
                    mAudioList.setVisibility(View.VISIBLE);
                }
                mAdapter.onDataChange();
                break;
            case TAB_INDEX_AUX:
                if (mAuxStatus.getVisibility() != View.VISIBLE) {
                    hideViews();
                    mAuxStatus.setVisibility(View.VISIBLE);
                }
                break;
            case TAB_INDEX_MOBILE:
                break;
        }

        for(int i=0;i<TAB_IDS.length;i++) {
            if(i == index) {
                findViewById(TAB_IDS[i]).setEnabled(false);
                findViewById(TAB_IDS[i]).setBackgroundResource(
                        R.drawable.browser_footer_tab_selected);
            } else {
                findViewById(TAB_IDS[i]).setEnabled(true);
                findViewById(TAB_IDS[i]).setBackgroundResource(0);
            }
        }
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
    public void onBackPressed() {
        if (!mAdapter.isNormalMode()) {
            setMode(MODE_NORMAL);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        if (mAdapter.isEditMode()) {
            mAdapter.setItemSelected(pos);
            return;
        }
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(Constants.MUSIC_SONG_POSITION, pos);
        startActivity(intent);
        AudioManager.getInstance().setPlayType(AudioManager.getInstance().getViewType());
    }

}
