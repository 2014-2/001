package com.byd.player.history;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
/**
 * 
 * 存储播放历史
 * 
 * @author Des
 *
 */
public class BYDDatabase {

    private static final String TAG = "BYD_DB";

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "BYD_DB.db";

    private static BYDDatabase sSingleton = null;

    private static BYDDatabaseHelper mOpenHelper = null;

    private Context mContext = null;

    private BYDDatabase(Context context) {
        mContext = context;
        mOpenHelper = new BYDDatabaseHelper(mContext);
    }

    /**
     * SQLiteOpenHelper
     * 
     */
    private static class BYDDatabaseHelper extends SQLiteOpenHelper {

        BYDDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            /** Create the maxim table. */
            Log.d(TAG, "Create Database " + DATABASE_NAME);
            db.execSQL(PlayRecordTable.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Upgrading from version " + oldVersion + " to "
                    + newVersion + ", data will be lost!");
        }
    }

    /**
     * 获取Database单例
     * 
     * @param context
     * @return
     */
    public static synchronized BYDDatabase getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new BYDDatabase(context);
        }
        return sSingleton;
    }

    public static SQLiteDatabase getDatabase(boolean writeable) {
        if (writeable) {
            return mOpenHelper.getWritableDatabase();
        } else {
            return mOpenHelper.getReadableDatabase();
        }
    }

    /**
     * 关闭DB相关操作
     */
    public synchronized void close() {
        if (null != sSingleton) {
            mOpenHelper.close();
            sSingleton = null;
        }
    }

    /**
     * 根据用户点击后播放，插入记录播放历史
     * 
     * @param playRecord
     * @return
     */
    public long insertVideoPlayRecord(PlayRecord playRecord) {
        if (playRecord != null) {
            ContentValues contentValues = playRecord.getDBContentValue();
            Cursor cursor = getDatabase(true).rawQuery(
                    "SELECT * FROM " + PlayRecordTable.TABLE_NAME + " WHERE "
                            + PlayRecordTable.DATA + "=?",
                    new String[] {playRecord.getMovieInfo().path });
            if (cursor.getCount() > 0) {
                return getDatabase(true).updateWithOnConflict(
                        PlayRecordTable.TABLE_NAME, contentValues,
                        PlayRecordTable.DATA + "=?",
                        new String[] {playRecord.getMovieInfo().path },
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            else {
                return getDatabase(true).insert(PlayRecordTable.TABLE_NAME,
                        null, contentValues);
            }
        }
        return -1;
    }

    /**
     * 在视频播放器列表中获取播放列表
     * 
     * @return 播放列表
     */
    public List<PlayRecord> getPlayRecord() {
        Cursor cursor = getDatabase(true).rawQuery(
                "SELECT * FROM " + PlayRecordTable.TABLE_NAME, null);
        if (cursor.getCount() > 0) {
            ArrayList<PlayRecord> playRecords = new ArrayList<PlayRecord>();
            cursor.moveToFirst();
            do {
                PlayRecord record = new PlayRecord();
                record.fillValues(cursor);
                playRecords.add(record);
            } while (cursor.moveToNext());
            cursor.close();
            cursor = null;
            return playRecords;
        }
        return null;
    }

    /**
     * 根据用户选择编辑的视频播放记录删除相关的数据库记录
     * 
     * @param playRecordsData
     */
    public void deletePlayRecords(List<String> playRecordsData) {
        if(playRecordsData != null && playRecordsData.size() > 0) {
            try {
                getDatabase(true).beginTransaction();
                for(final String item : playRecordsData) {
                  if(TextUtils.isEmpty(item) || TextUtils.isEmpty(item)) {
                     continue;
                  }
                  getDatabase(true).delete(PlayRecordTable.TABLE_NAME,
                          PlayRecordTable.DATA + "=?",
                         new String[] { item});
                }
                getDatabase(true).setTransactionSuccessful();
             } finally {
                getDatabase(true).endTransaction();
             }
        }
    }
    
}
