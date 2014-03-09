package com.byd.player.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.byd.player.config.Constants;

public class AudioLoaderTask extends AsyncQueryHandler {

    public final static String[] DEF_PROJECTION = new String[] {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATA };

    public final static String DEF_SELECTION_LOCAL = MediaStore.Audio.Media.MIME_TYPE + "=? or "
            + MediaStore.Audio.Media.MIME_TYPE + "=?";

    public final static String DEF_SELECTION_SDCARD = "(" + MediaStore.Audio.Media.MIME_TYPE + "=? or "
            + MediaStore.Audio.Media.MIME_TYPE + "=?) and " 
            + MediaStore.Audio.Media.DATA + " not like '"
            + Constants.USB_REGIX + "'";
    
    public final static String DEF_SELECTION_USB = "(" + MediaStore.Audio.Media.MIME_TYPE + "=? or "
            + MediaStore.Audio.Media.MIME_TYPE + "=?) and "
            + MediaStore.Audio.Media.DATA + " like '"
            + Constants.USB_REGIX + "'";

    public final static String[] DEF_SELECTION_ARGS = new String[] { "audio/mpeg", "audio/x-ms-wma" };

    protected AudioLoaderManager mAudioManager;
    private int mType;

    public AudioLoaderTask(Context context, AudioLoaderManager am, int type) {
        super(context.getContentResolver());
        mAudioManager = am;
        mType = type;
    }

    public void loadData() {
        switch (mType) {
        case AudioLoaderManager.INTERNAL_TYPE:
            startQuery(0, (Object) null, MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                    AudioLoaderTask.DEF_PROJECTION, AudioLoaderTask.DEF_SELECTION_LOCAL,
                    AudioLoaderTask.DEF_SELECTION_ARGS, null);
            break;

        case AudioLoaderManager.EXTERNAL_SDCARD_TYPE:
            startQuery(0, (Object) null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    AudioLoaderTask.DEF_PROJECTION, AudioLoaderTask.DEF_SELECTION_SDCARD,
                    AudioLoaderTask.DEF_SELECTION_ARGS, null);
            break;
        case AudioLoaderManager.EXTERNAL_USB_TYPE:
            startQuery(0, (Object) null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    AudioLoaderTask.DEF_PROJECTION, AudioLoaderTask.DEF_SELECTION_USB,
                    AudioLoaderTask.DEF_SELECTION_ARGS, null);
            break;
        default:
            break;
        }
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        List<Song> songs = null;
        try {
            if (cursor.moveToFirst()) {
                songs = getSongs(cursor);

            }
        } finally {
            cursor.close();
            mAudioManager.clearData(mType);
            mAudioManager.add(songs, mType);
            mAudioManager.notifyDataChange();
        }
    }

    /**
     * getSongList()
     * 
     * @param cursor
     */
    public List<Song> getSongs(Cursor cursor) {
        List<Song> songs = new ArrayList<Song>();
        do {
            Song song = null;
            song = new Song();
            song.setFileName(cursor.getString(1));// file Name
            song.setFileTitle(cursor.getString(2));// song name
            song.setDuration(cursor.getInt(3));// play time
            song.setSinger(cursor.getString(4));// artist
            song.setAlbum(cursor.getString(5));// album
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

            if (cursor.getString(9) != null) {// file path
                song.setFilePath(cursor.getString(9));
            }
            if (new File(song.getFilePath()).exists()) {
                songs.add(song);
            }
            
        } while (cursor.moveToNext());
        return songs;
    }
}
