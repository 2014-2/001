package com.byd.player.audio;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayerManager {
    private static AudioPlayerManager sInstance = new AudioPlayerManager();
    private List<Song> mPlayerList = new ArrayList<Song>();
    private int mPlayerPosition = -1;

    private AudioPlayerManager() {
    }

    public static AudioPlayerManager getInstance() {
        return sInstance;
    }

    public void setPlayerList(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }

        mPlayerList.addAll(songs);
        mPlayerPosition = 0;
    }

    public void setPlayerPosition(int pos) {
        if (mPlayerList.size() <= pos || pos < 0) {
            return;
        }
        mPlayerPosition = pos;
    }

    public Song next() {
        if (mPlayerList.isEmpty()) {
            return null;
        }
        mPlayerPosition++;
        if (mPlayerList.size() == mPlayerPosition) {
            mPlayerPosition = 0;
        }
        return mPlayerList.get(mPlayerPosition);
    }

    public Song previous() {
        if (mPlayerList.isEmpty()) {
            return null;
        }
        mPlayerPosition--;
        if (mPlayerPosition < 0) {
            mPlayerPosition = mPlayerList.size() - 1;
        }
        return mPlayerList.get(mPlayerPosition);
    }

    public Song getCurrentPlaySong() {
        return getSongAtPosition(mPlayerPosition);
    }

    public Song getSongAtPosition(int pos) {
        if (!mPlayerList.isEmpty() && pos >= 0) {
            return mPlayerList.get(pos);
        }
        return null;
    }

    public int getCount() {
        return mPlayerList.size();
    }

    public void clearPlayList() {
        mPlayerList.clear();
        mPlayerPosition = -1;
    }
}
