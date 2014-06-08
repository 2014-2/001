package com.byd.audioplayer.audio;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class AudioDeleteAsyncTask extends AsyncTask {

    public interface DeleteListener {
        void onDeleteStart();
        void onDeleteUpdateProgress(int progress, int count);
        void onDeleteEnd();
        void onDeleteCancelled();
    }

    private List<Song> mSongs = null;
    private DeleteListener mListener = null;

    public AudioDeleteAsyncTask(List<Song> songs, DeleteListener listener) {
        mSongs = songs;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.onDeleteStart();
    }

    @Override
    protected Object doInBackground(Object... arg0) {
        for (int i = 0; i < mSongs.size(); i++) {
            String path = mSongs.get(i).getFilePath();
            File file = new File(path);
            if (file.exists()) {
                boolean result = file.delete();
                Log.i("AudioDeleteAsyncTask", "Delete File:" + path + ", result=" + result);
            } else {
                Log.i("AudioDeleteAsyncTask", "Delete File:" + path + ",but not exists");
            }
            publishProgress(i);
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mListener.onDeleteCancelled();
    }

    @Override
    protected void onPostExecute(Object result) {
        mListener.onDeleteEnd();
    }
}
