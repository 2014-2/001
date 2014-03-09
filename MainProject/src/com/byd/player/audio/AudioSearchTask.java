package com.byd.player.audio;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class AudioSearchTask extends AudioLoaderTask {
    public interface SearchListener{
        void onSearchComplete(List<Song> result);
    }
    private SearchListener mListener = null;

    public AudioSearchTask(Context context, SearchListener listener) {
        super(context, null, -1);
        mListener = listener;
    }

    public void search(String text, int byWhich) {
        String selection = null;
        switch (byWhich) {
        case AudioLoaderManager.SEARCH_BY_NAME:
            selection = "(" + MediaStore.Audio.Media.MIME_TYPE + "=? or "
                    + MediaStore.Audio.Media.MIME_TYPE + "=?) and "
                    + MediaStore.Audio.Media.DISPLAY_NAME + " like '"
                    + text + "'";
            break;
        case AudioLoaderManager.SEARCH_BY_SINGER:
            selection = "(" + MediaStore.Audio.Media.MIME_TYPE + "=? or "
                    + MediaStore.Audio.Media.MIME_TYPE + "=?) and "
                    + MediaStore.Audio.Media.ARTIST + " like '"
                    + text + "'";
            break;
        }
        startQuery(0, (Object) null, MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                AudioLoaderTask.DEF_PROJECTION, selection,
                AudioLoaderTask.DEF_SELECTION_ARGS, null);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        List<Song> songs = null;
        if (cursor == null) {
            mListener.onSearchComplete(songs);
            return;
        }
        try {
            if (cursor.moveToFirst()) {
                songs = getSongs(cursor);
            }
        } finally {
            mListener.onSearchComplete(songs);
            cursor.close();
        }
    }

}
