package com.byd.audioplayer.audio;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

public class AudioPlayerManager {
    private static AudioPlayerManager sInstance = new AudioPlayerManager();
    private List<Song> mPlayerList = new ArrayList<Song>();
    private int mPlayerPosition = -1;

    private int mStorageType = 0;

    private ReloadSongListHandler mHandler;

    private PausedSong[] mPausedSongs = new PausedSong[3];

    class ReloadSongListHandler extends Handler {
        public Song mPlayingSong;

        public ReloadSongListHandler(Song playingSong) {
            mPlayingSong = playingSong;
        }
    }

    private AudioPlayerManager() {
    }

    public static AudioPlayerManager getInstance() {
        return sInstance;
    }

    public void setPausedSong(int index, PausedSong song) {
        mPausedSongs[index] = song;
    }

    public PausedSong getPausedSong(int index) {
        return mPausedSongs[index];
    }

    public void setPlayerList(int type, List<Song> songs) {
        mStorageType = type;
        if (songs == null || songs.isEmpty()) {
            return;
        }

        mPlayerList.clear();
        mPlayerList.addAll(songs);
        mPlayerPosition = 0;
    }

    public void storageStatusChange(Song playingSong) {
        mHandler = new ReloadSongListHandler(playingSong) {

            @Override
            public void handleMessage(Message msg) {
                List<Song> songs;
                songs = AudioLoaderManager.getInstance().getSongsByStorage(mStorageType);
                int playingSongPosition = -1;
                for (int i = 0; i < songs.size(); i++) {
                    Song song = songs.get(i);
                    if (song.getFilePath().equals(mPlayingSong.getFilePath())) {
                        playingSongPosition = i;
                    }
                }
                setPlayerList(mStorageType, songs);
                setPlayerPosition(playingSongPosition);
            }
        };
    }

    public int getStorageType() {
        return mStorageType;
    }

    public void notifyDataSetChanged(int storageType) {
        if (mHandler != null && storageType == mStorageType) {
            mHandler.sendEmptyMessage(0);
        }
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

    public int getCurrentPlayingSongPosition() {
        return mPlayerPosition;
    }

    public Song getSongAtPosition(int pos) {
        if (!mPlayerList.isEmpty() && pos >= 0) {
            return mPlayerList.get(pos);
        }
        return null;
    }

    public void setPlaySong(int pos) {
        if (pos < 0 || pos >= mPlayerList.size()) {
            return;
        }
        mPlayerPosition = pos;
    }

    public int getCount() {
        return mPlayerList.size();
    }

    public void clearPlayList() {
        mPlayerList.clear();
        mPlayerPosition = -1;
    }

    class PausedSong {
        private int storage_type;

        private int position;

        private String path;

        private int current_time;

        public PausedSong(int storage_type, int position, String path, int current_time) {
            this.storage_type = storage_type;
            this.position = position;
            this.path = path;
            this.current_time = current_time;
        }

        public int getStorageType() {
            return storage_type;
        }

        public void setStorageType(int storage_type) {
            this.storage_type = storage_type;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getCurrentTime() {
            return current_time;
        }

        public void setCurrentTime(int current_time) {
            this.current_time = current_time;
        }
    }
}
