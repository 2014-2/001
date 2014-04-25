package com.byd.audioplayer.audio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.byd.audioplayer.audio.AudioDeleteAsyncTask.DeleteListener;

public class AudioLoaderManager {
    private static AudioLoaderManager sInstance = new AudioLoaderManager();

    public final static int INTERNAL_TYPE = AudioListActivity.TAB_INDEX_LOCAL;
    public final static int EXTERNAL_SDCARD_TYPE = AudioListActivity.TAB_INDEX_SDCARD;
    public final static int EXTERNAL_USB_TYPE = AudioListActivity.TAB_INDEX_USB;

    public final static int SEARCH_BY_NAME = 0;
    public final static int SEARCH_BY_SINGER = 1;

    public interface DataListener {
        void onDataChange();

        List<Song> getSeletedSongs();
    }

    private List<DataListener> mListeners = new ArrayList<AudioLoaderManager.DataListener>();

    private Context mContext = null;
    private List<Song> mInteralSongs = new ArrayList<Song>();
    private List<Song> mExtenalSDCARDSongs = new ArrayList<Song>();
    private List<Song> mExtenalUSBSongs = new ArrayList<Song>();

    private int mViewType = INTERNAL_TYPE;
    private int mPlayType = INTERNAL_TYPE;

    private AudioLoaderManager() {
    }

    public void init(Context context) {
        mContext = context;
    }

    public static AudioLoaderManager getInstance() {
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
        new AudioLoaderTask(mContext, sInstance, type).loadData();
    }

    public void clear() {
        mInteralSongs.clear();
        mExtenalUSBSongs.clear();
        mExtenalSDCARDSongs.clear();
    }

    public void clearData(int type) {
        switch (type) {
        case INTERNAL_TYPE:
            for (Song song : mInteralSongs) {
                song.free();
            }
            mInteralSongs.clear();
            break;
        case EXTERNAL_SDCARD_TYPE:
            for (Song song : mExtenalSDCARDSongs) {
                song.free();
            }
            mExtenalSDCARDSongs.clear();
            break;
        case EXTERNAL_USB_TYPE:
            for (Song song : mExtenalUSBSongs) {
                song.free();
            }
            mExtenalUSBSongs.clear();
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
        case EXTERNAL_SDCARD_TYPE:
            mExtenalSDCARDSongs.add(song);
            break;
        case EXTERNAL_USB_TYPE:
            mExtenalUSBSongs.add(song);
            break;
        default:
            break;
        }
    }

    public void add(List<Song> songs, int type) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        switch (type) {
        case INTERNAL_TYPE:
            mInteralSongs.addAll(songs);
            break;
        case EXTERNAL_SDCARD_TYPE:
            mExtenalSDCARDSongs.addAll(songs);
            break;
        case EXTERNAL_USB_TYPE:
            mExtenalUSBSongs.addAll(songs);
            break;
        default:
            break;
        }
    }

    public List<Song> getViewSongs() {
        switch (mViewType) {
        case INTERNAL_TYPE:
            return mInteralSongs;
        case EXTERNAL_SDCARD_TYPE:
            return mExtenalSDCARDSongs;
        case EXTERNAL_USB_TYPE:
            return mExtenalUSBSongs;
        default:
            return null;
        }
    }

    // public Song getPlaySongAtPosition(int position) {
    // switch (mViewType) {
    // case INTERNAL_TYPE:
    // if (position < mInteralSongs.size()) {
    // return mInteralSongs.get(position);
    // }
    // break;
    // case EXTERNAL_SDCARD_TYPE:
    // if (position < mExtenalSDCARDSongs.size()) {
    // return mExtenalSDCARDSongs.get(position);
    // }
    // break;
    // case EXTERNAL_USB_TYPE:
    // if (position < mExtenalUSBSongs.size()) {
    // return mExtenalUSBSongs.get(position);
    // }
    // break;
    // default:
    // break;
    // }
    // return null;
    // }
    //
    // public int getPlaySongsCount() {
    // switch (mViewType) {
    // case INTERNAL_TYPE:
    // return mInteralSongs.size();
    // case EXTERNAL_SDCARD_TYPE:
    // return mExtenalSDCARDSongs.size();
    // case EXTERNAL_USB_TYPE:
    // return mExtenalUSBSongs.size();
    // default:
    // break;
    // }
    // return 0;
    //
    // }

    public void deleteSongs(List<Song> songs, DeleteListener listener) {
        switch (mViewType) {
        case INTERNAL_TYPE:
            mInteralSongs.removeAll(songs);
            break;
        case EXTERNAL_SDCARD_TYPE:
            mExtenalSDCARDSongs.removeAll(songs);
            break;
        case EXTERNAL_USB_TYPE:
            mExtenalUSBSongs.removeAll(songs);
            break;
        }

        for (Song song : songs) {
            song.free();
        }

        new AudioDeleteAsyncTask(songs, listener).execute((Object) null);
        notifyDataChange();
    }

    public void notifyDataChange() {
        for (DataListener listener : mListeners) {
            listener.onDataChange();
        }
    }
}
