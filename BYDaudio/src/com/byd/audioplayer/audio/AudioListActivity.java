package com.byd.audioplayer.audio;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.byd.audioplayer.AuxAudioPlayActivity;
import com.byd.audioplayer.BaseActivity;
import com.byd.audioplayer.R;
import com.byd.audioplayer.audio.AudioDeleteAsyncTask.DeleteListener;
import com.byd.audioplayer.audio.AudioPlayerManager.PausedSong;
import com.byd.audioplayer.audio.AudioPlayerService.OnSongChangedListener;
import com.byd.audioplayer.audio.AudioSearchTask.SearchListener;
import com.byd.audioplayer.bluetooth.BTPlayerActivity;
import com.byd.audioplayer.config.Constants;
import com.byd.audioplayer.receiver.USBMountReceiver;
import com.byd.audioplayer.utils.ToastUtils;
import com.byd.audioplayer.utils.VideoContentObserver;

/**
 * 启动Audio List页面，指定default的页面的方法：
 * Intent intent = new Intent();
 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 * intent.setClassName("com.byd.player.audio", "com.byd.player.audio.AudioListActivity");
 * intent.putExtra("audio_page", number); // number: 0-本地, 1-sdcard, 2-usb, 3-aux, 4-手机
 * startActivity(intent)
 */
@SuppressLint("ValidFragment")
public class AudioListActivity extends BaseActivity implements OnItemClickListener,
OnItemLongClickListener, SearchListener, DeleteListener {
    private final static String TAG = "AudioListActivity";

    private final static String TAB_INDEX = "audio_page";

    public final static int TAB_INDEX_LOCAL = 0;
    public final static int TAB_INDEX_SDCARD = 1;
    public final static int TAB_INDEX_USB = 2;
    public final static int TAB_INDEX_AUX = 3;
    public final static int TAB_INDEX_MOBILE = 4;

    public final static int MODE_NORMAL = 0;
    public final static int MODE_EDIT = MODE_NORMAL + 1;
    public final static int MODE_SEARCH = MODE_EDIT + 1;

    private final static int REQCODE_AUX = 100;
    private final static int REQCODE_BT = 200;

    private final static int REQUEST_CODE_PLAY = 999;

    private final int[] TAB_IDS = new int[] { R.id.btn_audio_Local, R.id.btn_audio_sdcard,
            R.id.btn_audio_usb, R.id.btn_audio_aux, R.id.btn_audio_mobile };
    private final int[] TAB_NORMAL_BGS_ZH = new int[] { R.drawable.bg_audio_local_normal,
            R.drawable.bg_sdcard_normal, R.drawable.bg_usb_normal, R.drawable.bg_aux_normal,
            R.drawable.bg_mobile_normal, };
    private final int[] TAB_SELECTED_BGS_ZH = new int[] { R.drawable.bg_audio_local_selected,
            R.drawable.bg_sdcard_selcted, R.drawable.bg_usb_selected, R.drawable.bg_aux_selected,
            R.drawable.bg_mobile_selected, };

    private final int[] TAB_NORMAL_BGS_EN = new int[] { R.drawable.bg_audio_local_normal_en,
            R.drawable.bg_sdcard_normal_en, R.drawable.bg_usb_normal, R.drawable.bg_aux_normal,
            R.drawable.bg_mobile_normal_en, };
    private final int[] TAB_SELECTED_BGS_EN = new int[] { R.drawable.bg_audio_local_selected_en,
            R.drawable.bg_sdcard_selcted_en, R.drawable.bg_usb_selected, R.drawable.bg_aux_selected,
            R.drawable.bg_mobile_selected_en, };

    private GridView mAudioList = null;
    private AudioAdapter mAdapter = null;

    private EditText mSearchText = null;
    private ProgressDialog mProgressDialog = null;

    private AudioInternalScanner mInternalScanner = null;
    private USBMountReceiver mUSBMountReceiver;

    private MediaStoreChangedHandler mMediaStoreChangedHandler;

    private OnSongChangedListener mOnSongChangedListener;

    private Intent mAudioServiceIntent;

    private boolean mIsStartFromWheel = false;

    private Handler mListHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mAdapter.notifyDataSetChanged();
            if (null == mOnSongChangedListener) {
                mOnSongChangedListener = new OnSongChangedListener() {
                    @Override
                    public void onSongChanged(int newPosition) {
                        mAdapter.notifyDataSetChanged();
                    }
                };
            }
            AudioPlayerService.setOnSongChangedListener(mOnSongChangedListener);
            super.handleMessage(msg);
        }
    };

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
        refreshDatas();

        setContentView(R.layout.audio_list_view);

        mAudioServiceIntent = new Intent(this, AudioPlayerService.class);

        initViews();
        setMode(MODE_NORMAL);

        registerUSBStateChangedReceiver();
        registerMediaStoreChangedObserver();

        mInternalScanner = new AudioInternalScanner(this);

        Intent startIntent = getIntent();
        if (startIntent != null && startIntent.hasExtra(TAB_INDEX)) {
            mIsStartFromWheel = true;
            final int tabIndex = getIntent().getIntExtra(TAB_INDEX, TAB_INDEX_LOCAL);
            tabIndex(tabIndex);
            Log.d(TAG, "onCreate tabIndex=" + tabIndex);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && !intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
            mIsStartFromWheel = true;
            final int tabIndex = intent.getIntExtra(TAB_INDEX, TAB_INDEX_LOCAL);
            tabIndex(tabIndex);
            Log.d(TAG, "onNewIntent tabIndex=" + tabIndex);
        }
    }

    /**
     * Reload Audio List form MediaProvider.
     */
    public void refreshDatas() {
        AudioLoaderManager.getInstance().loadData(AudioLoaderManager.EXTERNAL_SDCARD_TYPE);
        AudioLoaderManager.getInstance().loadData(AudioLoaderManager.EXTERNAL_USB_TYPE);
        AudioLoaderManager.getInstance().loadData(AudioLoaderManager.INTERNAL_TYPE);
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
        ImageView back = (ImageView) findViewById(R.id.button_header_back);
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
                    DeleteDialog.newInstance(AudioListActivity.this, songs).show(fm,
                            "DELETE_DIALOG");
                }
            }
        });

        mSearchText = (EditText) findViewById(R.id.search_text);

        Button searchByName = (Button) findViewById(R.id.button_search_name);
        searchByName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isSearchMode()) {
                    String text = mSearchText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        Message msg = mHandler.obtainMessage(MSG_SHOW_SEARCH_PROGRESS_DIALOG);
                        mHandler.sendMessageDelayed(msg, 2000);
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
                        mHandler.sendMessageDelayed(msg, 2000);
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
        int index = AudioLoaderManager.getInstance().getViewType();
        if (index < 0 || index >= TAB_INDEX_AUX) {
            index = TAB_INDEX_LOCAL;
        }
        tabIndex(index);
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
        mUSBMountReceiver = new USBMountReceiver(this);
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
        if (mIsStartFromWheel) {
            int storageType = AudioPlayerManager.getInstance().getStorageType();
            List<Song> songs = AudioLoaderManager.getInstance().getViewSongs();
            if (index <= 3 && index >= 0 && null != AudioPlayerService.mPlayingSong
                    && null != AudioPlayerService.mPlayer && songs.size() > 0) {
                PausedSong pausedSong = AudioPlayerManager.getInstance().new PausedSong(
                        storageType, AudioPlayerService.mSongPosition,
                        AudioPlayerService.mPlayingSong.getFilePath(),
                        AudioPlayerService.mPlayer.getCurrentPosition());
                AudioPlayerManager.getInstance().setPausedSong(storageType, pausedSong);
            }
        }
        switch (index) {
            case TAB_INDEX_LOCAL:
            case TAB_INDEX_SDCARD:
            case TAB_INDEX_USB:
                AudioLoaderManager.getInstance().setViewType(index);
                mAdapter.onDataChange();
                if (mIsStartFromWheel) {
                    startPlaySong();
                    mIsStartFromWheel = false;
                }
                break;
            case TAB_INDEX_AUX:
                Intent intent_aux = new Intent();
                intent_aux.setClass(AudioListActivity.this, AuxAudioPlayActivity.class);
                startActivityForResult(intent_aux, REQCODE_AUX);
                break;
            case TAB_INDEX_MOBILE:
                Intent intent_bt = new Intent();
                intent_bt.setClass(AudioListActivity.this, BTPlayerActivity.class);
                startActivityForResult(intent_bt, REQCODE_BT);
                break;
        }
        Constants.recordAudioPageIndex(getApplicationContext(), index);

        for (int i = 0; i < TAB_IDS.length; i++) {
            if (i == index) {
                findViewById(TAB_IDS[i]).setEnabled(false);
                if(true == isZh())
                {
                    findViewById(TAB_IDS[i]).setBackgroundResource(TAB_SELECTED_BGS_ZH[i]);
                }
                else {
                    findViewById(TAB_IDS[i]).setBackgroundResource(TAB_SELECTED_BGS_EN[i]);
                }
            } else {
                findViewById(TAB_IDS[i]).setEnabled(true);
                if(true == isZh())
                {
                    findViewById(TAB_IDS[i]).setBackgroundResource(TAB_NORMAL_BGS_ZH[i]);
                }
                else {
                    findViewById(TAB_IDS[i]).setBackgroundResource(TAB_NORMAL_BGS_EN[i]);
                }
            }
        }
        updateHeadTitle();
    }

    private void startPlaySong() {
        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // try {
        // Thread.sleep(500);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        int viewType = AudioLoaderManager.getInstance().getViewType();
        List<Song> songs = AudioLoaderManager.getInstance().getViewSongs();
        if (songs.size()<=0) {
            mIsStartFromWheel = true;
            tabIndex(viewType + 1);
            return;
        }
        // AudioPlayerManager.getInstance().setPlayerList(
        // AudioLoaderManager.getInstance().getViewType(), songs);
        // AudioPlayerManager.getInstance().setPlayerPosition(0);
        // mAudioServiceIntent.putExtra(Constants.PLAYER_MSG,
        // Constants.PlayerCommand.PLAY);
        // mAudioServiceIntent.putExtra(Constants.MUSIC_SONG_POSITION,
        // 0);
        // mAudioServiceIntent.putExtra(Constants.MUSIC_SONG_CURRENT_TIME,
        // 10000);
        // startService(mAudioServiceIntent);
        // AudioLoaderManager.getInstance().setPlayType(
        // AudioLoaderManager.getInstance().getViewType());
        PausedSong pausedSong = AudioPlayerManager.getInstance().getPausedSong(viewType);
        int songPosition = 0;
        int currentTime = 0;
        if (null != pausedSong) {
            if (pausedSong.getPosition() < songs.size()
                    && songs.get(pausedSong.getPosition()).getFilePath()
                    .equals(pausedSong.getPath())) {
                // 列表没有变动，index对应的歌曲是正确的
                songPosition = pausedSong.getPosition();
                currentTime = pausedSong.getCurrentTime();
            } else {
                // 列表出现了变动，index对应歌曲不正确，重新查找
                int newIndex = findIndexInSongList(songs, pausedSong.getPath());
                if (newIndex != -1) {
                    // 歌曲找到了，只是index有变动，恢复播放
                    songPosition = newIndex;
                    currentTime = pausedSong.getCurrentTime();
                } else {
                    // 歌曲无法找到，播放默认歌曲
                }
            }
        }

        // Start player activity
        AudioPlayerManager.getInstance().setPlayerList(viewType, songs);
        AudioPlayerManager.getInstance().setPlayerPosition(songPosition);
        Intent intent = new Intent(AudioListActivity.this, AudioPlayerActivity.class);
        intent.putExtra(Constants.MUSIC_SONG_POSITION, songPosition);
        intent.putExtra(Constants.MUSIC_SONG_CURRENT_TIME, currentTime);
        startActivityForResult(intent, REQUEST_CODE_PLAY);
        AudioLoaderManager.getInstance().setPlayType(
                AudioLoaderManager.getInstance().getViewType());
        mListHandler.sendEmptyMessageDelayed(0, 500);
        // }
        // }).start();
    }

    private int findIndexInSongList(List<Song> songs, String path) {
        int index = -1;
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getFilePath().equals(path)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onResume() {
        mAdapter.notifyDataSetInvalidated();
        super.onResume();

        mInternalScanner.startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mInternalScanner.stopScan();
    }

    @Override
    protected void onDestroy() {
        unregisterUSBStateChangedReceiver();
        stopService(new Intent(this, AudioPlayerService.class));
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
            AudioPlayerManager.getInstance().setPlayerList(
                    AudioLoaderManager.getInstance().getViewType(), songs);
            AudioPlayerManager.getInstance().setPlayerPosition(pos);

            Intent intent = new Intent(this, AudioPlayerActivity.class);
            intent.putExtra(Constants.MUSIC_SONG_POSITION, pos);
            startActivityForResult(intent, REQUEST_CODE_PLAY);
            AudioLoaderManager.getInstance().setPlayType(
                    AudioLoaderManager.getInstance().getViewType());
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

    public static class DeleteDialog extends DialogFragment {
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
            builder.setTitle(R.string.delete).setMessage(R.string.confirm_delete);

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
                        mProgressDialog.setTitle(R.string.delete_song_title);
                        mProgressDialog.setMessage(getApplicationContext().getString(R.string.deleting_song));
                    }
                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                    }
                    break;
                case MSG_SHOW_SEARCH_PROGRESS_DIALOG:
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(AudioListActivity.this);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setTitle(R.string.search_song_title);
                        mProgressDialog.setMessage(getApplicationContext().getString(R.string.searching_song));
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
            ToastUtils.showToast(this, R.string.not_found_music);
        } else {
            mAdapter.setData(result);
        }
        mHandler.removeMessages(MSG_SHOW_SEARCH_PROGRESS_DIALOG);
        mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQCODE_BT)
        {
            final int tabIndex = AudioLoaderManager.getInstance().getViewType();
            findViewById(TAB_IDS[tabIndex]).setEnabled(false);
            if(true == isZh())
            {
                findViewById(TAB_IDS[tabIndex]).setBackgroundResource(TAB_SELECTED_BGS_ZH[tabIndex]);
            }else {
                findViewById(TAB_IDS[tabIndex]).setBackgroundResource(TAB_SELECTED_BGS_EN[tabIndex]);
            }

            findViewById(TAB_IDS[4]).setEnabled(true);

            if(true == isZh())
            {
                findViewById(TAB_IDS[4]).setBackgroundResource(TAB_NORMAL_BGS_ZH[4]);
            }else {
                findViewById(TAB_IDS[4]).setBackgroundResource(TAB_NORMAL_BGS_EN[4]);
            }
        } else if (requestCode == REQCODE_AUX)
        {
            final int tabIndex = AudioLoaderManager.getInstance().getViewType();
            findViewById(TAB_IDS[tabIndex]).setEnabled(false);
            if(true == isZh())
            {
                findViewById(TAB_IDS[tabIndex]).setBackgroundResource(TAB_SELECTED_BGS_ZH[tabIndex]);
            }else {
                findViewById(TAB_IDS[tabIndex]).setBackgroundResource(TAB_SELECTED_BGS_EN[tabIndex]);
            }
            findViewById(TAB_IDS[3]).setEnabled(true);
            if(true == isZh())
            {
                findViewById(TAB_IDS[3]).setBackgroundResource(TAB_NORMAL_BGS_ZH[3]);
            }else {
                findViewById(TAB_IDS[3]).setBackgroundResource(TAB_NORMAL_BGS_EN[3]);
            }
        } else if (requestCode == REQUEST_CODE_PLAY) {
            if (null == mOnSongChangedListener) {
                mOnSongChangedListener = new OnSongChangedListener() {
                    @Override
                    public void onSongChanged(int newPosition) {
                        mAdapter.notifyDataSetChanged();
                    }
                };
            }
            AudioPlayerService.setOnSongChangedListener(mOnSongChangedListener);
        }
    }

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
