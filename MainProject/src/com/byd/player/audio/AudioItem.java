package com.byd.player.audio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class AudioItem {

    private Song mSong = null;
    private boolean mSelected = false;

    public AudioItem(Song song) {
        mSong = song;
    }

    public Song getSong() {
        return mSong;
    }

    public Bitmap getAlbumBitmap() {
        return mSong.getAblumBitmap();
    }

    public String getAudioName() {
        return mSong.getFileTitle();
    }

    public String getSinger() {
        return mSong.getSinger();
    }

    public void setSelected(boolean flag) {
        mSelected = flag;
    }

    public boolean isSelected() {
        return mSelected;
    }
}
