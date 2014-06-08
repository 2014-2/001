package com.crtb.tunnelmonitor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DTMSDB extends SQLiteOpenHelper {
	private static final String DB_NAME = "DTMSDB.db";
	private static final int DB_VERSION = 1;

	public DTMSDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建ProjectIndex表 --本机所有工程信息列表（工程管理、导入、导出使用）
		db.execSQL("create table if not exists ProjectIndex(id INTEGER PRIMARY KEY AUTOINCREMENT ,ProjectName varchar(255),"
				+ "CreateTime text,StartChainage double,EndChainage double,"
				+ "LastOpenTime text,Info text, ChainagePrefix varchar(255),"
				+ "GDLimitVelocity float,GDLimitTotalSettlement float,SLLimitVelocity float,"
				+ "SLLimitTotalSettlement float,DBLimitVelocity float,DBLimitTotalSettlement float"
				+ ",ConstructionFirm varchar(255),LimitedTotalSubsidenceTime text)");
		// 创建SurveyerIndex表 --本机上所有测量人员信息
		db.execSQL("create table if not exists SurveyerIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT,SurveyerName VARCHAR(100),"
				+ "CertificateID VARCHAR(20),Password VARCHAR(64),Info text,"
				+ "ProjectID INTEGER)");
		db.execSQL("create table if not exists ProjectSettingIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT,ProjectName VARCHAR(255),"
				+ "YMDFormat INTEGER,HMSFormat INTEGER,ChainagePrefix VARCHAR(255),MaxDeformation INTEGER,"
				+ "Info TEXT)");
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO: drop the tables
	}

}
