package com.byd.videoplayer.history;

import com.byd.videoplayer.video.MovieInfo;

import android.content.ContentValues;
import android.database.Cursor;
/**
 * 
 * @author Des
 *
 */
public class PlayRecord {
    private MovieInfo movieInfo;
    private int lastPlayPosition;
    private long time;

    public MovieInfo getMovieInfo() {
        return movieInfo;
    }

    public void setMovieInfo(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
    }

    public int getLastPlayPosition() {
        return lastPlayPosition;
    }

    public void setLastPlayPosition(int lastPlayPosition) {
        this.lastPlayPosition = lastPlayPosition;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ContentValues getDBContentValue() {
        if(movieInfo == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(PlayRecordTable.DATA, movieInfo.path);
        contentValues.put(PlayRecordTable.NAME, movieInfo.displayName);
        contentValues.put(PlayRecordTable.DURATION, movieInfo.duration);
        contentValues.put(PlayRecordTable.POSITION, lastPlayPosition);
        contentValues.put(PlayRecordTable.TIME, time);
        return contentValues;
    }
    
    public void fillValues(Cursor cursor) {
        if (cursor != null) {
            try {
                movieInfo = new MovieInfo();
                movieInfo.path = cursor.getString(cursor
                        .getColumnIndex(PlayRecordTable.DATA));
                movieInfo.displayName = cursor.getString(cursor
                        .getColumnIndex(PlayRecordTable.NAME));
                movieInfo.duration = cursor.getInt(cursor
                        .getColumnIndex(PlayRecordTable.DURATION));
                lastPlayPosition = cursor.getInt(cursor
                        .getColumnIndex(PlayRecordTable.POSITION));
                time = cursor.getLong(cursor
                        .getColumnIndex(PlayRecordTable.TIME));
            }
            catch (Exception ex) {
            }
        }
    }
}
