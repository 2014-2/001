package com.byd.player;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.player.config.Constants;
import com.byd.player.history.BYDDatabase;
import com.byd.player.history.PlayRecord;
import com.byd.player.utils.DialogUtil;
import com.byd.player.utils.VideoContentObserver;
import com.byd.player.utils.VideoThumbnailLoader;
import com.byd.player.video.MovieInfo;
import com.byd.player.video.VideoPlayActivity;

/**
 * 
 * @author Des
 *
 */
public class BrowserActivity extends BaseActivity implements OnClickListener {
	private final static int MSG_TAB_ON_CHANGED = 10;
	private boolean isEditMode;
	private View buttonHeaderLeft, buttonHeaderRight;

	public final static int TAB_INDEX_LOCAL = 0;
	public final static int TAB_INDEX_SDCARD = 1;
	public final static int TAB_INDEX_USB = 2;
	public final static int TAB_INDEX_HISTORY = 3;
	private static int[] tabResId = new int[] {
	  R.id.tvLocal, R.id.tvSDCard, R.id.tvUsb, R.id.tvHistory      
	};
	private View[] frameContentView = new View[tabResId.length];
	private VideoAdapter[] videoAdapters = new VideoAdapter[tabResId.length];
	private int currentTabSelected;
	private FrameLayout mediaContentLayout;
	private SparseArray<ArrayList<MovieInfo>> mMediaStore = new SparseArray<ArrayList<MovieInfo>>(tabResId.length);
	private LayoutInflater mLayoutInflater;
	
	public static boolean[] tabContentChanged = new boolean[tabResId.length];
	private MyHandler mHandler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);
        mLayoutInflater = LayoutInflater.from(this);
        mHandler = new MyHandler(this);
        initUI();
        registerContentObserver();
    }
    
    private void initTabComponent() {
        mediaContentLayout = (FrameLayout) findViewById(R.id.media_content_frame);
        tabToIndex(TAB_INDEX_LOCAL);
        for(int resId : tabResId) {
            findViewById(resId).setOnClickListener(this);
        }
    }
    
    private void registerContentObserver() {
        getContentResolver().registerContentObserver(MediaStore.Video.Media.INTERNAL_CONTENT_URI, true, 
                new VideoContentObserver(VideoContentObserver.INTERNAL_VIDEO_CONTENT_CHANGED, mHandler)); 
        getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, 
                new VideoContentObserver(VideoContentObserver.EXTERNAL_VIDEO_CONTENT_CHANGED, mHandler)); 
    }
    
    private void tabToIndex(int tabIndex) {
        if(tabIndex < tabResId.length && tabIndex >= 0) {
            currentTabSelected = tabIndex;
        }
        boolean isEmpty = getVideoList(currentTabSelected) == null;
        View videoContentFrame;
        if(frameContentView[currentTabSelected] == null) {
            videoContentFrame = mLayoutInflater.inflate(R.layout.video_content_frame, null);
            frameContentView[currentTabSelected] = videoContentFrame;
        } else {
            videoContentFrame = frameContentView[currentTabSelected];
        }
        if(isEmpty) {
            videoContentFrame.findViewById(R.id.gv_media_list).setVisibility(View.GONE);
            videoContentFrame.findViewById(R.id.tv_no_records).setVisibility(View.VISIBLE);
        } else {
            videoContentFrame.findViewById(R.id.gv_media_list).setVisibility(View.VISIBLE);
            videoContentFrame.findViewById(R.id.tv_no_records).setVisibility(View.GONE);
        }
        mediaContentLayout.removeAllViews();
        mediaContentLayout.addView(frameContentView[currentTabSelected]);
        if(!isEmpty) {
            if(videoAdapters[currentTabSelected] == null) {
                videoAdapters[currentTabSelected] = new VideoAdapter(this); 
            }
            videoAdapters[currentTabSelected].setDataList(getVideoList(currentTabSelected));
            GridView gridView = (GridView) frameContentView[currentTabSelected].findViewById(R.id.gv_media_list);
            gridView.setAdapter(videoAdapters[currentTabSelected]);
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> gridView, View arg1, int position,
                        long id) {
                    MovieInfo info = (MovieInfo) gridView.getAdapter().getItem(position);
                    Intent intent = new Intent(BrowserActivity.this, VideoPlayActivity.class);
                    Bundle params = new Bundle();
                    params.putString("video_url", info.path);
                    params.putString("name", info.displayName);
                    params.putInt("duration", info.duration);
                    intent.putExtra(Constants.VIDEO_PLAY_PARAMS, params);
                    startActivity(intent);
                }
            });
        }
        //update tab selected
        for(int resId : tabResId) {
            if(resId == tabResId[currentTabSelected]) {
                findViewById(tabResId[currentTabSelected]).setEnabled(false);
                findViewById(tabResId[currentTabSelected]).setBackgroundResource(R.drawable.browser_footer_tab_selected);
            } else {
               findViewById(resId).setEnabled(true);
               findViewById(resId).setBackgroundResource(0);
            }
        }
        updateTitleBar();
    }
    
    private void initUI() {
        buttonHeaderLeft = ((TextView) findViewById(R.id.button_header_left));
        buttonHeaderLeft.setOnClickListener(this);
        buttonHeaderLeft.setVisibility(View.INVISIBLE);
        buttonHeaderRight = ((TextView) findViewById(R.id.button_header_right));
        buttonHeaderRight.setOnClickListener(this);
        findViewById(R.id.tvDelete).setOnClickListener(this);
        initTabComponent();
    }
    
    private void updateTitleBar() {
        TextView tvHeadTitle = (TextView) findViewById(R.id.tv_header_title);
        switch(currentTabSelected) {
            case TAB_INDEX_LOCAL:
                tvHeadTitle.setText(R.string.local_video);
                break;
            case TAB_INDEX_SDCARD:
                tvHeadTitle.setText(R.string.sdcard);
                break;
            case TAB_INDEX_USB:
                tvHeadTitle.setText(R.string.usb);
                break;
            case TAB_INDEX_HISTORY:
                tvHeadTitle.setText(R.string.play_history);
                break;
        }
    }
    
    static class MyHandler extends Handler {
        WeakReference<BrowserActivity> wrActivity = null;
        MyHandler(BrowserActivity activity) {
            wrActivity = new WeakReference<BrowserActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case VideoContentObserver.INTERNAL_VIDEO_CONTENT_CHANGED:
                    tabContentChanged[TAB_INDEX_LOCAL] = true;
                    break;
                case VideoContentObserver.EXTERNAL_VIDEO_CONTENT_CHANGED:
                    tabContentChanged[TAB_INDEX_SDCARD] = true;
                    break;
                case VideoContentObserver.HISTORY_VIDEO_CONTENT_CHANGED:
                    tabContentChanged[TAB_INDEX_HISTORY] = true;
                    break;
                case MSG_TAB_ON_CHANGED:
                    BrowserActivity activity = wrActivity.get();
                    if(activity != null) {
                        activity.tabToIndex(msg.arg1);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_header_left:
                if(videoAdapters[currentTabSelected] != null) {
                    videoAdapters[currentTabSelected].chooseAll();
                }
                break;
            case R.id.button_header_right:
                changeEditMode();
                break;
            case R.id.tvDelete:
                new DeleteFileTask().execute(currentTabSelected);
                break;
            case R.id.tvLocal:
                mHandler.obtainMessage(MSG_TAB_ON_CHANGED, TAB_INDEX_LOCAL, 0).sendToTarget();
                break;
            case R.id.tvSDCard:
                mHandler.obtainMessage(MSG_TAB_ON_CHANGED, TAB_INDEX_SDCARD, 0).sendToTarget();
                break;
            case R.id.tvUsb:
                mHandler.obtainMessage(MSG_TAB_ON_CHANGED, TAB_INDEX_USB, 0).sendToTarget();
                break;
            case R.id.tvHistory:
                mHandler.obtainMessage(MSG_TAB_ON_CHANGED, TAB_INDEX_HISTORY, 0).sendToTarget();
                break;
        }
    }
    
    
    @Override
    public void onBackPressed() {
        if(isEditMode) {
            changeEditMode();
        } else {
            super.onBackPressed();
        }
    }

    
    private ArrayList<MovieInfo> getVideoList(int tabIndex) {
        switch(tabIndex) {
            case TAB_INDEX_LOCAL:
            case TAB_INDEX_SDCARD:
                Uri mediaUri = (tabIndex == TAB_INDEX_LOCAL ? 
                        MediaStore.Video.Media.INTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                if (android.os.Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED)) {
                    boolean contentChanged = tabContentChanged[tabIndex];
                    if(contentChanged) {
                        mMediaStore.remove(tabIndex);
                        tabContentChanged[tabIndex] = false;
                    } else if (mMediaStore.get(tabIndex) != null) {
                        return mMediaStore.get(tabIndex);
                    }
                    ArrayList<MovieInfo> playList = queryMediaByUri(mediaUri, null);
                    mMediaStore.put(tabIndex, playList);
                    return playList;
                }
                break;
            case TAB_INDEX_USB: {
                boolean contentChanged = tabContentChanged[tabIndex];
                if(contentChanged) {
                    mMediaStore.remove(tabIndex);
                    tabContentChanged[tabIndex] = false;
                } else if (mMediaStore.get(tabIndex) != null) {
                    return mMediaStore.get(tabIndex);
                }
                ArrayList<MovieInfo> playList = queryMediaByUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 
                        Constants.USB_REGIX);
                if(playList != null && playList.size() > 0) {
                    mMediaStore.put(tabIndex, playList);
                } else {
                    return null;
                }
                return playList;
            }
            case TAB_INDEX_HISTORY: {
                boolean contentChanged = tabContentChanged[tabIndex];
                if(contentChanged) {
                    mMediaStore.remove(tabIndex);
                    tabContentChanged[tabIndex] = false;
                } else if (mMediaStore.get(tabIndex) != null) {
                    return mMediaStore.get(tabIndex);
                }
                List<PlayRecord> playRecords = BYDDatabase.getInstance(this).getPlayRecord();
                if(playRecords != null) {
                    ArrayList<MovieInfo> list = new ArrayList<MovieInfo>(playRecords.size());
                    for(PlayRecord item : playRecords) {
                        list.add(item.getMovieInfo());
                    }
                    mMediaStore.put(tabIndex, list);
                    return list;
                }
                break;
            }
        }
        return null;
    }
    
    private ArrayList<MovieInfo> queryMediaByUri(Uri mediaUri, String pathRegix) {
        String selection = null;
        String[] selectionArgs = null;
        if(!TextUtils.isEmpty(pathRegix)) {
            selection = MediaStore.Video.Media.DATA + " LIKE '" + pathRegix + "%'";
            //selectionArgs = new String[]{pathRegix};
        }
        Cursor cursor = getContentResolver().query(mediaUri,
                new String[] {"_display_name", "_data", MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DURATION}, 
                    selection, selectionArgs, null);
        int n = cursor.getCount();
        cursor.moveToFirst();
        ArrayList<MovieInfo> playList = new ArrayList<MovieInfo>();
        for (int i = 0; i != n; ++i) {
            MovieInfo mInfo = new MovieInfo();
            mInfo.displayName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Video.Media.TITLE));
            mInfo.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            mInfo.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            playList.add(mInfo);
            cursor.moveToNext();
        }
        return playList;
    }
    
    private void changeEditMode() {
        if (videoAdapters[currentTabSelected] != null && videoAdapters[currentTabSelected].getCount() > 0) {
           isEditMode = !isEditMode;
           //showComposeBar(isEditMode);
           videoAdapters[currentTabSelected].notifyDataSetChanged();
        }
        findViewById(R.id.tab_layout).setVisibility(isEditMode?View.GONE:View.VISIBLE);
        findViewById(R.id.edit_layout).setVisibility(isEditMode?View.VISIBLE:View.GONE);
        if(isEditMode) {
            buttonHeaderLeft.setVisibility(View.VISIBLE);
            ((TextView) buttonHeaderRight).setText(R.string.cancel);
        } else {
            buttonHeaderLeft.setVisibility(View.GONE);
            ((TextView) buttonHeaderRight).setText(R.string.edit);
        }
    }
    
    class VideoAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<MovieInfo> mArrayList;
        private boolean[] selectedList;
        
        public VideoAdapter(Context context) {
            mContext = context;
            mArrayList = new ArrayList<MovieInfo>();
        }
        
        public void setDataList(ArrayList<MovieInfo> list) {
            mArrayList.clear();
            if(list != null) {
                mArrayList.addAll(list);
            }
            if(mArrayList.size() > 0) {
                selectedList = new boolean[mArrayList.size()];
            }
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        
        private void setChoosed(int position) {
            if(selectedList != null && position < selectedList.length) {
                selectedList[position] = !selectedList[position];
            }
         }
         
         /**
          * Set choose all or not
          * @param flag
          */
         public void chooseAll() {
            if(selectedList != null) {
               boolean value = true;
               for(int i=0; i< selectedList.length; i++) {
                  if(!selectedList[i]) {
                     value = true;
                     break;
                  } else {
                     value = false;
                  }
               }
               for(int i=0; i< selectedList.length; i++) {
                   selectedList[i] = value;
               }
               notifyDataSetChanged();
            }
         }
         
         private boolean isChoosed(int position) {
             if(selectedList != null) {
                if(position < selectedList.length) {
                   return (selectedList[position]);
                }
             }
             return false;
         }
         
         public ArrayList<MovieInfo> getChoosedList() {
             ArrayList<MovieInfo> choosedList = new ArrayList<MovieInfo>();
             for(int index = 0; index < selectedList.length; index++) {
                 if(selectedList[index]) {
                     choosedList.add(mArrayList.get(index));
                 }
             }
             return choosedList;
         }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.browser_gridview_item, null);
                viewHolder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_grid_thumbnail);
                viewHolder.tvDuration = (TextView) convertView.findViewById(R.id.tv_duration);
                viewHolder.tvVideoName = (TextView) convertView.findViewById(R.id.tv_video_name);
                viewHolder.cbSelected = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            MovieInfo movieInfo = (MovieInfo) getItem(position);
            viewHolder.tvVideoName.setText(movieInfo.displayName);
            viewHolder.tvDuration.setText(getDurationText(movieInfo.duration));
            viewHolder.cbSelected.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            viewHolder.cbSelected.setChecked(isChoosed(position));
            viewHolder.cbSelected.setTag(position);
            viewHolder.cbSelected.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    setChoosed(position);
                }
            });
            VideoThumbnailLoader.loadBitmap(movieInfo.path, viewHolder.ivThumbnail);
            return convertView;
        }
        
        @SuppressLint("DefaultLocale")
        private String getDurationText(int duration) {
            duration /= 1000;
            int minute = duration / 60;
            int hour = minute / 60;
            int second = duration % 60;
            minute %= 60;
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        
        
        class ViewHolder {
            ImageView ivThumbnail;
            TextView tvVideoName;
            TextView tvDuration;
            CheckBox cbSelected;
        }
    }

    private class DeleteFileTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected void onPreExecute() {
            DialogUtil.showProgressDialog(BrowserActivity.this);
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            if(params == null || params.length != 1) {
                return -1;
            }
            int tabIndex = params[0];
            int effectCount = 0;
            if(tabIndex >= TAB_INDEX_LOCAL && tabIndex <= TAB_INDEX_HISTORY) {
                VideoAdapter adapter = videoAdapters[tabIndex];
                ArrayList<MovieInfo> choosedList = adapter.getChoosedList();
                for(MovieInfo movie : choosedList) {
                    if(!TextUtils.isEmpty(movie.path)) {
                        File videoFile = new File(movie.path);
                        videoFile.delete();
                        Uri mediaUri = (tabIndex == TAB_INDEX_LOCAL ? 
                                MediaStore.Video.Media.INTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        getContentResolver().delete(mediaUri ,
                               "_data='" + movie.path + "'", null); 
                        getContentResolver().notifyChange(mediaUri, null);
                        effectCount++;
                    }
                }
            }
            if(effectCount == 0) {
                return -1;
            }
            return tabIndex;
        }
        
        @Override
        protected void onPostExecute(Integer tabIndex) {
            DialogUtil.closeProgressDialog();
            if(tabIndex != -1) {
                tabContentChanged[tabIndex] = true;
                mHandler.obtainMessage(MSG_TAB_ON_CHANGED, tabIndex, 0).sendToTarget();
                changeEditMode();
            } else {
                Toast.makeText(BrowserActivity.this, R.string.select_tips, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(tabIndex);
        }
        
    }
}
