package com.byd.player.audio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class AudioSearchTask extends AudioLoaderTask {
    public interface SearchListener {
        void onSearchComplete(List<Song> result);
    }

    private SearchListener mListener = null;

    private final int QUERY_EMPTY = 0;
    private final int QUERY_INTERNAL = 1;
    private final int QUERY_EXTENRAL = 2;

    private int mQueryStep = QUERY_EMPTY;
    private List<Song> mQueryResult = new ArrayList<Song>();
    private String mSelection = null;
    private String [] mSelectionArgs = null;

    public AudioSearchTask(Context context, SearchListener listener) {
        super(context, null, -1);
        mListener = listener;
    }

    public void search(String text, int byWhich) {
        mSelectionArgs = new String[] { "%" + text + "%" };
        switch (byWhich) {
        case AudioLoaderManager.SEARCH_BY_NAME:
//            mSelection = "(" + MediaStore.Audio.Media.MIME_TYPE + "=? or "
//                    + MediaStore.Audio.Media.MIME_TYPE + "=?) and "
//                    + MediaStore.Audio.Media.DISPLAY_NAME + " like '" + text + "'";
            mSelection = " " + MediaStore.Audio.Media.TITLE + " like ?";
            break;
        case AudioLoaderManager.SEARCH_BY_SINGER:
        default:
            mSelection = " " + MediaStore.Audio.Media.ARTIST + " like ?";
//            mSelection = "(" + MediaStore.Audio.Media.MIME_TYPE + "=? or "
//                    + MediaStore.Audio.Media.MIME_TYPE + "=?) and " + MediaStore.Audio.Media.ARTIST
//                    + " like '" + text + "'";
            break;
        }
        mQueryStep = QUERY_INTERNAL;
        mQueryResult.clear();
        startQuery(0, (Object) null, MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                AudioLoaderTask.DEF_PROJECTION, mSelection, mSelectionArgs,// AudioLoaderTask.DEF_SELECTION_ARGS,
                null);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        try {
            if (cursor != null && cursor.moveToFirst()) {
                List<Song> songs = getSongs(cursor);
                if (songs != null) {
                    mQueryResult.addAll(songs);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (mQueryStep == QUERY_INTERNAL) {
                mQueryStep = QUERY_EXTENRAL;
                startQuery(0, (Object) null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        AudioLoaderTask.DEF_PROJECTION, mSelection,
                        mSelectionArgs, null);
            } else {
                mListener.onSearchComplete(mQueryResult);
                mQueryStep = QUERY_EMPTY;
            }
        }
    }

}
