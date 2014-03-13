package com.byd.player.audio;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class SongInfoUtils {
    /**
     * mSongsList
     */
    private ArrayList<Song> mSongsList = new ArrayList<Song>();

    /**
     * mContext
     */
    private Context mContext = null;

    /**
     * SongInfoUtils()
     * @param aContext
     */
    public SongInfoUtils(Context aContext) {
        mContext = aContext;
    }

    /**
     * getFileInfo()
     * @param aFileAbsoulatePath
     * @return
     */
    public String[] getFileInfo(String aFileAbsoulatePath) {
        String[] fileMessage = new String[3];
        File file = new File(aFileAbsoulatePath);
        String fileName = file.getName();
        String filePath = "/mnt" + file.getPath();

        if (file.exists()) {
            if (mContext != null) {
                readDataFromSD();

                int count = mSongsList.size();
                for (int i = 0; i < count; i++) {
                    if (mSongsList.get(i).getFilePath().equals(filePath)
                            && mSongsList.get(i).getFileName()
                                    .equals(fileName)) {
                        fileMessage[0] = mSongsList.get(i).getFileTitle();
                        fileMessage[1] = mSongsList.get(i).getAlbumArt();
                        fileMessage[2] = mSongsList.get(i).getSinger();
                        break;
                    }
                }
            }
        }

        return fileMessage;
    }
    
    /**
     * readDataFromSD()
     */
    public void readDataFromSD() {

        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA },
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
        if (cursor.moveToFirst()) {
            getSongList(cursor);
        }
        return;
    }
    
    /**
     * getSongList()
     * @param cursor
     */
    public void getSongList(Cursor cursor) {
        Song song = null;
        do {
            song = new Song();
            song.setFileName(cursor.getString(1));// file Name
            song.setFileTitle(cursor.getString(2));// song name
            song.setDuration(cursor.getInt(3));// play time
            song.setSinger(cursor.getString(4));// artist
            song.setAlbumArt(cursor.getString(5));// album
            if (cursor.getString(6) != null) {
                song.setYear(cursor.getString(6));
            } else {
                song.setYear("undefine");
            }
            if ("audio/mpeg".equals(cursor.getString(7).trim())) {// file type
                song.setFileType("mp3");
            } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
                song.setFileType("wma");
            }
            if (cursor.getString(8) != null) {// fileSize
                float temp = cursor.getInt(8) / 1024f / 1024f;
                String sizeStr = (temp + "").substring(0, 4);
                song.setFileSize(sizeStr + "M");
            } else {
                song.setFileSize("undefine");
            }

            if (cursor.getString(9) != null) {//file path
                song.setFilePath(cursor.getString(9));
            }

            mSongsList.add(song);
        } while (cursor.moveToNext());

        cursor.close();

        return;
    }
}
