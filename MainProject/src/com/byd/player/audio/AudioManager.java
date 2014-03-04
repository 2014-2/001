package com.byd.player.audio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class AudioManager {
    private static AudioManager sInstance = new AudioManager();

    public final static int INTERNAL_TYPE = AudioListActivity.TAB_INDEX_LOCAL;
    public final static int EXTERNAL_TYPE = AudioListActivity.TAB_INDEX_SDCARD;

    public interface DataListener {
        void onDataChange();
    }

    private List<DataListener> mListeners = new ArrayList<AudioManager.DataListener>();

    private Context mContext = null;
    private List<Song> mInteralSongs = new ArrayList<Song>();
    private List<Song> mExtenalSongs = new ArrayList<Song>();

    private int mViewType = INTERNAL_TYPE;
    private int mPlayType = INTERNAL_TYPE;

    private AudioManager() {
    }

    public void init(Context context) {
        mContext = context;
    }

    public static AudioManager getInstance() {
        return sInstance;
    }

    public void setViewType(int type) {
        mViewType = type;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setPlayType(int type) {
        mPlayType = type;
    }

    public int getPlayType() {
        return mPlayType;
    }

    public void addDataListener(DataListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeDataListener(DataListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    public void loadData(int type) {
        new AudioLoader(mContext, sInstance, type).loadData();
    }

    public void clear() {
        mInteralSongs.clear();
        mExtenalSongs.clear();
    }

    public void clearData(int type) {
        switch (type) {
        case INTERNAL_TYPE:
            mInteralSongs.clear();
            break;
        case EXTERNAL_TYPE:
            mExtenalSongs.clear();
            break;
        default:
            break;
        }
    }

    public void add(Song song, int type) {
        switch (type) {
        case INTERNAL_TYPE:
            mInteralSongs.add(song);
            break;
        case EXTERNAL_TYPE:
            mExtenalSongs.add(song);
            break;
        default:
            break;
        }
    }

    public void add(List<Song> songs, int type) {
        switch (type) {
        case INTERNAL_TYPE:
            mInteralSongs.addAll(songs);
            break;
        case EXTERNAL_TYPE:
            mExtenalSongs.addAll(songs);
            break;
        default:
            break;
        }
    }

    public List<Song> getViewSongs() {
        switch (mViewType) {
        case INTERNAL_TYPE:
            return mInteralSongs;
        case EXTERNAL_TYPE:
            return mExtenalSongs;
        default:
            return null;
        }
    }

    public Song getPlaySongAtPosition(int position) {
        switch (mViewType) {
        case INTERNAL_TYPE:
            if (position < mInteralSongs.size()) {
                return mInteralSongs.get(position);
            }
            break;
        case EXTERNAL_TYPE:
            if (position < mExtenalSongs.size()) {
                return mExtenalSongs.get(position);
            }
            break;
        default:
            break;
        }
        return null;
    }

    public int getPlaySongsCount() {
        switch (mViewType) {
        case INTERNAL_TYPE:
            return mInteralSongs.size();
        case EXTERNAL_TYPE:
            return mExtenalSongs.size();
        default:
            break;
        }
        return 0;

    }

    public void notifyDataChange() {
        for (DataListener listener : mListeners) {
            listener.onDataChange();
        }
    }
}
