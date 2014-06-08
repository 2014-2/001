package com.byd.audioplayer.audio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;



public class Song {
    /**
     * fileName
     */
    private String mFileName = "";

    /**
     * song name
     */
    private String mFileTitle = "";

    /**
     * play total time
     */
    private int mDuration = 0;

    /**
     * singer
     */
    private String mSinger = "";

    /**
     * album name
     */
    private String mAlbum = "";

    /**
     * album art name
     */
    private String mAlbumArt = "";

    /**
     * mYear
     */
    private String mYear = "";

    //    /**
    //     * mFileType
    //     */
    //    private String mFileType = "";

    /**
     * mFileSize
     */
    private String mFileSize = "";

    /**
     * mFilePath
     */
    private String mFilePath = "";

    /**
     * getFileName()
     * 
     * @return
     */
    public String getFileName() {
        return mFileName;
    }

    /**
     * setFileName()
     * 
     * @param mFileName
     */
    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    /**
     * getFileTitle()
     * 
     * @return
     */
    public String getFileTitle() {
        return mFileTitle;
    }

    /**
     * setFileTitle()
     * 
     * @param mFileTitle
     */
    public void setFileTitle(String mFileTitle) {
        this.mFileTitle = mFileTitle;
    }

    /**
     * getDuration()
     * 
     * @return
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * setDuration()
     * 
     * @param mDuration
     */
    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    /**
     * getSinger()
     * 
     * @return
     */
    public String getSinger() {
        return mSinger;
    }

    /**
     * setSinger()
     * 
     * @param mSinger
     */
    public void setSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    /**
     * getAlbum()
     * 
     * @return
     */
    public String getAlbum() {
        return mAlbum;
    }

    /**
     * setAlbum()
     * 
     * @param mAlbum
     */
    public void setAlbum(String album) {
        this.mAlbum = album;
        if (!TextUtils.isEmpty(mAlbum)) {
            mAblumBitmapCatch = BitmapFactory.decodeFile(mAlbum);
        }
    }

    /**
     * getAlbumArt()
     * 
     * @return
     */
    public String getAlbumArt() {
        return mAlbum;
    }

    /**
     * setAlbumArt()
     * 
     * @param mAlbum
     */
    public void setAlbumArt(String mAlbum) {
        this.mAlbumArt = mAlbum;
    }


    /**
     * getYear()
     * 
     * @return
     */
    public String getYear() {
        return mYear;
    }

    /**
     * setYear()
     * 
     * @param mYear
     */
    public void setYear(String mYear) {
        this.mYear = mYear;
    }

    //    /**
    //     * getFileType()
    //     *
    //     * @return
    //     */
    //    public String getFileType() {
    //        return mFileType;
    //    }
    //
    //    /**
    //     * setFileType()
    //     *
    //     * @param mFileType
    //     */
    //    public void setFileType(String mFileType) {
    //        this.mFileType = mFileType;
    //    }

    /**
     * getFileSize()
     * 
     * @return
     */
    public String getFileSize() {
        return mFileSize;
    }

    /**
     * setmFileSize()
     * 
     * @param mFileSize
     */
    public void setFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    /**
     * getFilePath()
     * 
     * @return
     */
    public String getFilePath() {
        return mFilePath;
    }


    /**
     * setmFilePath()
     * 
     * @param mFilePath
     */
    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Song) {
            return mFilePath.equals(((Song)o).mFilePath);
        } else {
            return false;
        }
    }

    private Bitmap mAblumBitmapCatch = null;

    /**
     * getAblumBitmap()
     * @return
     */
    public Bitmap getAblumBitmap() {
        if (!TextUtils.isEmpty(mAlbum) && mAblumBitmapCatch == null) {
            mAblumBitmapCatch = BitmapFactory.decodeFile(mAlbum);
        }
        return mAblumBitmapCatch;
    }

    public void free() {
        if (mAblumBitmapCatch != null) {
            mAblumBitmapCatch.recycle();
            mAblumBitmapCatch = null;
        }
    }
}
