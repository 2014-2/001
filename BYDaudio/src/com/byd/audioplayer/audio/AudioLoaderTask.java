package com.byd.audioplayer.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.byd.audioplayer.config.Constants;

public class AudioLoaderTask extends AsyncQueryHandler {

    public final static String[] DEF_PROJECTION = new String[] {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATA };

    public final static String DEF_SELECTION_LOCAL = MediaStore.Audio.Media.DATA + " like '%" + Constants.LOCAL_REGIX + "%'";

    public final static String DEF_SELECTION_SDCARD = MediaStore.Audio.Media.DATA + " like '%" + Constants.SDCARD_REGIX + "%'";

    public final static String DEF_SELECTION_USB = MediaStore.Audio.Media.DATA + " like '%" + Constants.USB_REGIX + "%'";

    //    public final static String[] DEF_SELECTION_ARGS = new String[] { "audio/mpeg", "audio/x-ms-wma", "audio/mp4", "audio/x-aac" };

    protected AudioLoaderManager mAudioManager;
    private int mType;
    private Context mContext = null;

    public AudioLoaderTask(Context context, AudioLoaderManager am, int type) {
        super(context.getContentResolver());
        mContext = context;
        mAudioManager = am;
        mType = type;
    }

    public void loadData() {
        switch (mType) {
            case AudioLoaderManager.INTERNAL_TYPE:
                startQuery(0, (Object) null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        AudioLoaderTask.DEF_PROJECTION, AudioLoaderTask.DEF_SELECTION_LOCAL,
                        null, null);
                break;

            case AudioLoaderManager.EXTERNAL_SDCARD_TYPE:
                startQuery(0, (Object) null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        AudioLoaderTask.DEF_PROJECTION, AudioLoaderTask.DEF_SELECTION_SDCARD,
                        null, null);
                break;
            case AudioLoaderManager.EXTERNAL_USB_TYPE:
                startQuery(0, (Object) null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        AudioLoaderTask.DEF_PROJECTION, AudioLoaderTask.DEF_SELECTION_USB,
                        null, null);
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
        new LoadSongsTask().execute(cursor);
    }

    class LoadSongsTask extends AsyncTask<Cursor, Void, List<Song>> {
        @Override
        protected List<Song> doInBackground(Cursor... params) {
            List<Song> songs = null;
            try {
                if (params[0].moveToFirst()) {
                    songs = getSongs(params[0]);
                }
            } finally {
                params[0].close();
            }
            return songs;
        }

        @Override
        protected void onPostExecute(List<Song> result) {
            mAudioManager.clearData(mType);
            mAudioManager.add(result, mType);
            mAudioManager.notifyDataChange();
            super.onPostExecute(result);
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
            String fileName = getStringForChinese(cursor.getString(1));
            song.setFileName(fileName);// file Name
            String fileTitle = getStringForChinese(cursor.getString(2));
            song.setFileTitle(fileTitle);// song name
            song.setDuration(cursor.getInt(3));// play time
            String artist = getStringForChinese(cursor.getString(4));
            song.setSinger(artist);// artist
            //            song.setAlbumArt(cursor.getString(5)); // album
            final int albumId = cursor.getInt(6); // album id
            String album = getAlbumArt(mContext.getContentResolver(), albumId);
            song.setAlbumArt(album);

            if (cursor.getString(7) != null) {
                song.setYear(cursor.getString(7));
            } else {
                song.setYear("undefine");
            }
            //            if ("audio/mpeg".equals(cursor.getString(8).trim())) {// file type
            //                song.setFileType("mp3");
            //            } else if ("audio/x-ms-wma".equals(cursor.getString(8).trim())) {
            //                song.setFileType("wma");
            //            } else if ("audio/x-aac".equals(cursor.getString(8).trim())) {
            //                song.setFileType("aac");
            //            }
            if (cursor.getString(9) != null) {// fileSize
                float temp = cursor.getInt(9) / 1024f / 1024f;
                String sizeStr = (temp + "").substring(0, 4);
                song.setFileSize(sizeStr + "M");
            } else {
                song.setFileSize("undefine");
            }

            String path = cursor.getString(10);
            if (path != null && path.contains("system/media/audio")) {
                // if audio in system path, ignore it
                continue;
            }
            song.setFilePath(path);
            if (new File(song.getFilePath()).exists()) {
                songs.add(song);
            }

        } while (cursor.moveToNext());
        return songs;
    }

    public static String getAlbumArt(ContentResolver resolver, int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = null;
        String album_art = null;
        try {
            cur = resolver.query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                    projection, null, null, null);

            if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
                cur.moveToNext();
                album_art = cur.getString(0);
            }
        } finally {
            if (cur != null) {
                cur.close();
                cur = null;
            }
        }
        return album_art;
    }

    public static String getStringForChinese(String value) {
        String newValue = value;
        try {
            if (value.equals(new String(value.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
                newValue = new String(value.getBytes("ISO-8859-1"), "GBK");
            }
        } catch (Exception e) {
        }
        return newValue;
    }

}
