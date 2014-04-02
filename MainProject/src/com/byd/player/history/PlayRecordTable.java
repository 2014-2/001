package com.byd.player.history;

import android.provider.BaseColumns;

/**
 * 
 * @author Des
 *
 */
public final class PlayRecordTable implements BaseColumns {

	public static final String TABLE_NAME = "PLAY_RECORDE";

	public static final String ID = BaseColumns._ID;
	public static final String DATA = "_data";
	public static final String NAME = "file_name";
	public static final String DURATION = "duration";
    public static final String POSITION = "last_play_position";
    public static final String TIME = "time";
   
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DATA + " TEXT, "
			+ NAME + " TEXT, "
			+ DURATION + " INTEGER,"
			+ POSITION + " INTEGER,"
			+ TIME + " INTEGER "
			+ ");";
	
}