package com.byd.player.audio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class AudioItem {

    private Song mSong = null;
    private boolean mSelected = false;
    private Bitmap mAblumBitmap = null;

    public AudioItem(Song song) {
        mSong = song;
    }

    public Song getSong() {
        return mSong;
    }

    public Bitmap getAlbumArt() {
        String path = mSong.getAlbumArt();
        if (!TextUtils.isEmpty(path) && mAblumBitmap == null) {
            mAblumBitmap = BitmapFactory.decodeFile(path);
        }
        return mAblumBitmap;
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
