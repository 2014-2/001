package com.byd.player.audio;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.byd.player.AuxAudioPlayActivity;
import com.byd.player.BaseActivity;
import com.byd.player.R;
import com.byd.player.audio.AudioDeleteAsyncTask.DeleteListener;
import com.byd.player.audio.AudioSearchTask.SearchListener;
import com.byd.player.bluetooth.ConnectActivity;
import com.byd.player.config.Constants;
import com.byd.player.receiver.USBMountReceiver;
import com.byd.player.services.AuxAudioService;
import com.byd.player.utils.ToastUtils;
import com.byd.player.utils.VideoContentObserver;

public class AudioListActivity extends BaseActivity implements OnItemClickListener,
        OnItemLongClickListener, SearchListener, DeleteListener {
    private final static String TAG = "AudioListActivity";
    public final static int TAB_INDEX_LOCAL = 0;
    public final static int TAB_INDEX_SDCARD = 1;
    public final static int TAB_INDEX_USB = 2;
    public final static int TAB_INDEX_AUX = 3;
    public final static int TAB_INDEX_MOBILE = 4;

    public final static int MODE_NORMAL = 0;
    public final static int MODE_EDIT = MODE_NORMAL + 1;
    public final static int MODE_SEARCH = MODE_EDIT + 1;

    private final int[] TAB_IDS = new int[] { R.id.btn_audio_Local, R.id.btn_audio_sdcard,
            R.id.btn_audio_usb, R.id.btn_audio_aux, R.id.btn_audio_mobile };

    private GridView mAudioList = null;
    private AudioAdapter mAdapter = null;

    private EditText mSearchText = null;
    private ProgressDialog mProgressDialog = null;

    private USBMountReceiver mUSBMountReceiver;

    private MediaStoreChangedHandler mMediaStoreChangedHandler;

    static class MediaStoreChangedHandler extends Handler {
        WeakReference<AudioListActivity> wrActivity = null;

        MediaStoreChangedHandler(AudioListActivity activity) {
            wrActivity = new WeakReference<AudioListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AudioListActivity activity = wrActivity.get();
            switch (msg.what) {
            case VideoContentObserver.INTERNAL_VIDEO_CONTENT_CHANGED:
            case VideoContentObserver.EXTERNAL_VIDEO_CONTENT_CHANGED:
                AudioLoaderManager.getInstance().loadData(AudioLoaderManager.EXTERNAL_SDCARD_TYPE);
                AudioLoaderManager.getInstance().loadData(AudioLoaderManager.EXTERNAL_USB_TYPE);
                AudioLoaderManager.getInstance().loadData(AudioLoaderManager.INTERNAL_TYPE);
                break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mMediaStoreChangedHandler = new MediaStoreChangedHandler(this);

        AudioLoaderManager.getInstance().init(getApplicationContext());
        AudioLoaderManager.getInstance().loadData(AudioLoaderManager.EXTERNAL_SDCARD_TYPE);
        AudioLoaderManager.getInstance().loadData(AudioLoaderManager.EXTERNAL_USB_TYPE);
        AudioLoaderManager.getInstance().loadData(AudioLoaderManager.INTERNAL_TYPE);

        setContentView(R.layout.audio_list_view);

        initViews();
        setMode(MODE_NORMAL);

        registerUSBStateChangedReceiver();

        registerMediaStoreChangedObserver();

        startService(new Intent(this, AuxAudioService.class));
    }

    private void initViews() {
        mAudioList = (GridView) findViewById(R.id.audio_grid_list);
        mAdapter = new AudioAdapter(this, getLayoutInflater());
        mAudioList.setAdapter(mAdapter);
        mAudioList.setOnItemClickListener(this);
        mAudioList.setOnItemLongClickListener(this);

        initHeaderButtons();
        initBottomButtons();
    }

    private void updateHeadTitle() {
        TextView headTitle = (TextView) findViewById(R.id.header_title);
        int titleId = R.string.audio_page;
        if (mAdapter.isSearchMode()) {
            titleId = R.string.search;
        } else {
            switch (AudioLoaderManager.getInstance().getViewType()) {
            case TAB_INDEX_LOCAL:
                titleId = R.string.title_audio_local;
                break;
            case TAB_INDEX_SDCARD:
                titleId = R.string.title_audio_sdcard;
                break;
            case TAB_INDEX_USB:
                titleId = R.string.title_audio_usb;
                break;
            default:
                break;
            }
        }
        headTitle.setText(titleId);
    }

    private void initHeaderButtons() {
        Button back = (Button) findViewById(R.id.button_header_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAdapter.isNormalMode()) {
                    setMode(MODE_NORMAL);
                } else {
                    AudioListActivity.this.finish();
                }
            }
        });

        Button edit = (Button) findViewById(R.id.button_header_edit);
        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(MODE_EDIT);
            }
        });

        Button search = (Button) findViewById(R.id.button_header_search);
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMode(MODE_SEARCH);
            }
        });

        Button delete = (Button) findViewById(R.id.button_header_delete);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isEditMode()) {
                    FragmentManager fm = AudioListActivity.this.getFragmentManager();
                    List<Song> songs = mAdapter.getSeletedSongs();
                    DeleteDialog.newInstance(AudioListActivity.this, songs).show(fm,
                            "DELETE_DIALOG");
                }
            }
        });

        Button deleteAll = (Button) findViewById(R.id.button_header_delete_all);
        deleteAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isEditMode()) {
                    FragmentManager fm = AudioListActivity.this.getFragmentManager();
                    List<Song> songs = mAdapter.getAllSongs(); 
                    DeleteDialog.newInstance(AudioListActivity.this, songs).show(fm, "DELETE_DIALOG");
                }
            }
        });

        mSearchText = (EditText) findViewById(R.id.search_text);

        Button searchByName = (Button) findViewById(R.id.button_search_name);
        searchByName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isSearchMode()) {
                    String text = mSearchText.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        Message msg = mHandler.obtainMessage(MSG_SHOW_SEARCH_PROGRESS_DIALOG);
                        mHandler.sendMessageDelayed(msg, 200);
                        new AudioSearchTask(AudioListActivity.this, AudioListActivity.this).search(
                                text, AudioLoaderManager.SEARCH_BY_NAME);
                    }
                }
            }
        });

        Button searchBySinger = (Button) findViewById(R.id.button_search_signer);
        searchBySinger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isSearchMode()) {
                    String text = mSearchText.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        Message msg = mHandler.obtainMessage(MSG_SHOW_SEARCH_PROGRESS_DIALOG);
                        mHandler.sendMessageDelayed(msg, 200);
                        new AudioSearchTask(AudioListActivity.this, AudioListActivity.this).search(
                                text, AudioLoaderManager.SEARCH_BY_SINGER);
                    }
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

                    tabIndex(index);
                }
            });
        }
        tabIndex(TAB_INDEX_LOCAL);
    }

    private void setMode(int mode) {
        switch (mode) {
        case MODE_NORMAL:
            if (!mAdapter.isNormalMode()) {
                findViewById(R.id.btn_edit_container).setVisibility(View.VISIBLE);
                findViewById(R.id.button_header_search).setVisibility(View.VISIBLE);
                findViewById(R.id.button_header_edit).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_bottom_container).setVisibility(View.VISIBLE);

                findViewById(R.id.button_header_delete).setVisibility(View.GONE);
                findViewById(R.id.button_header_delete_all).setVisibility(View.GONE);
                findViewById(R.id.search_text_container).setVisibility(View.GONE);
                findViewById(R.id.btn_search_container).setVisibility(View.GONE);
            }
            break;
        case MODE_EDIT:
            if (!mAdapter.isEditMode() && mAdapter.getCount() > 0) {
                findViewById(R.id.btn_edit_container).setVisibility(View.VISIBLE);
                findViewById(R.id.button_header_delete).setVisibility(View.VISIBLE);
                findViewById(R.id.button_header_delete_all).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_bottom_container).setVisibility(View.VISIBLE);

                findViewById(R.id.button_header_search).setVisibility(View.GONE);
                findViewById(R.id.button_header_edit).setVisibility(View.GONE);

                findViewById(R.id.search_text_container).setVisibility(View.GONE);
                findViewById(R.id.btn_search_container).setVisibility(View.GONE);
            }
            break;
        case MODE_SEARCH:
            if (!mAdapter.isSearchMode()) {
                findViewById(R.id.btn_search_container).setVisibility(View.VISIBLE);
                findViewById(R.id.search_text_container).setVisibility(View.VISIBLE);

                findViewById(R.id.btn_edit_container).setVisibility(View.GONE);
                findViewById(R.id.btn_bottom_container).setVisibility(View.GONE);
                mSearchText.requestFocus();
            }
            break;
        }
        mSearchText.setText(null);
        mAdapter.setMode(mode);
        updateHeadTitle();
    }

    private void registerUSBStateChangedReceiver() {
        mUSBMountReceiver = new USBMountReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        registerReceiver(mUSBMountReceiver, filter);
    }

    private void unregisterUSBStateChangedReceiver() {
        unregisterReceiver(mUSBMountReceiver);
    }

    private void registerMediaStoreChangedObserver() {
        getContentResolver().registerContentObserver(
                MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                true,
                new VideoContentObserver(VideoContentObserver.INTERNAL_VIDEO_CONTENT_CHANGED,
                        mMediaStoreChangedHandler));
        getContentResolver().registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true,
                new VideoContentObserver(VideoContentObserver.EXTERNAL_VIDEO_CONTENT_CHANGED,
                        mMediaStoreChangedHandler));
    }

    public void tabIndex(int index) {
        AudioLoaderManager.getInstance().setViewType(index);
        switch (index) {
        case TAB_INDEX_LOCAL:
        case TAB_INDEX_SDCARD:
        case TAB_INDEX_USB:
            mAdapter.onDataChange();
            break;
        case TAB_INDEX_AUX:
            startActivity(new Intent(AudioListActivity.this, AuxAudioPlayActivity.class));
            break;
        case TAB_INDEX_MOBILE:
            Intent intent = new Intent();
            intent.setClass(AudioListActivity.this, ConnectActivity.class);
            startActivity(intent);
            break;
        }

        for (int i = 0; i < TAB_IDS.length; i++) {
            if (i == index) {
                findViewById(TAB_IDS[i]).setEnabled(false);
                findViewById(TAB_IDS[i]).setBackgroundResource(
                        R.drawable.browser_footer_tab_selected);
            } else {
                findViewById(TAB_IDS[i]).setEnabled(true);
                findViewById(TAB_IDS[i]).setBackgroundResource(0);
            }
        }
        updateHeadTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterUSBStateChangedReceiver();
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
        } else {
            List<Song> songs = AudioLoaderManager.getInstance().getViewSongs();
            AudioPlayerManager.getInstance().setPlayerList(songs);
            AudioPlayerManager.getInstance().setPlayerPosition(pos);

            Intent intent = new Intent(this, AudioPlayerActivity.class);
            intent.putExtra(Constants.MUSIC_SONG_POSITION, pos);
            intent.putExtra(Constants.MUSIC_SONG_POSITION, pos);
            startActivity(intent);
            AudioLoaderManager.getInstance().setPlayType(AudioLoaderManager.getInstance().getViewType());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        if (mAdapter.isNormalMode()) {
            setMode(MODE_EDIT);
        }
        mAdapter.setItemSelected(pos);
        return true;
    }

    public List<Song> getDeletedList() {
        return mAdapter.getSeletedSongs();
    }

    private static class DeleteDialog extends DialogFragment {
        private AudioListActivity mActivity = null;
        private List<Song> mSongs;

        public DeleteDialog(AudioListActivity activity, List<Song> songs) {
            mActivity = activity;
            mSongs = songs;
        }

        public static DialogFragment newInstance(AudioListActivity activity, List<Song> songs) {
            DeleteDialog deleteDialog = new DeleteDialog(activity, songs);
            return deleteDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("DELETE").setMessage("音乐文件将从文件系统彻底删除。请确认是否删除？");

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mActivity.onDeleteStart();
                    AudioLoaderManager.getInstance().deleteSongs(mSongs, mActivity);
                }
            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            return builder.create();
        }

    }

    @Override
    public void onDeleteStart() {
        Message msg = mHandler.obtainMessage(MSG_SHOW_DELETE_PROGRESS_DIALOG);
        mHandler.sendMessageDelayed(msg, 300);
    }

    @Override
    public void onDeleteUpdateProgress(int progress, int count) {

    }

    @Override
    public void onDeleteEnd() {
        mHandler.removeMessages(MSG_SHOW_DELETE_PROGRESS_DIALOG);
        mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
    }

    @Override
    public void onDeleteCancelled() {
        mHandler.removeMessages(MSG_SHOW_DELETE_PROGRESS_DIALOG);
        mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
    }

    // Don't show the delete dialog if not spent too much time.
    private final static int MSG_SHOW_DELETE_PROGRESS_DIALOG = 10;
    private final static int MSG_SHOW_SEARCH_PROGRESS_DIALOG = 11;
    private final static int MSG_DISMISS_PROGRESS_DIALOG = 21;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_SHOW_DELETE_PROGRESS_DIALOG:
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(AudioListActivity.this);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setTitle("删除歌曲");
                    mProgressDialog.setMessage("正在删除歌曲,请稍候...");
                }
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
                break;
            case MSG_SHOW_SEARCH_PROGRESS_DIALOG:
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(AudioListActivity.this);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setTitle("搜索歌曲");
                    mProgressDialog.setMessage("正在搜索歌曲,请稍候...");
                }
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
                break;
            case MSG_DISMISS_PROGRESS_DIALOG:
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                break;
            }
        }

    };

    @Override
    public void onSearchComplete(List<Song> result) {
        if (result == null || result.isEmpty()) {
            ToastUtils.showToast(this, "未搜索到相关歌曲");
        } else {
            mAdapter.setData(result);
        }
        mHandler.removeMessages(MSG_SHOW_SEARCH_PROGRESS_DIALOG);
        mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
    }

}
