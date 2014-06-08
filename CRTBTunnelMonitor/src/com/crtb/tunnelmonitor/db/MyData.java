package com.crtb.tunnelmonitor.db;
//package com.crtb.tunnelmonitor.db;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
////数据库设计中根据单个project分别新建单个的数据库
//public class MyData extends SQLiteOpenHelper {
//	private String TAG = "MyData";
//	private static final int VERSION = 1; // 初始默认版本
//	Context mcontext;
//
//	public MyData(Context context, String name, CursorFactory factory,
//			int version) {
//		super(context, name, factory, version);
//		// TODO Auto-generated constructor stub
//		mcontext = context;
//	}
//
//	public MyData(Context context, String name, int version) {
//		this(context, name, null, version);
//	}
//
//	public MyData(Context context, String name) {
//		this(context, name, VERSION);
//	}
//
//	public void onCreate(SQLiteDatabase db) {
//
//		// 创建ProjectIndex表
//		db.execSQL("create table if not exists ProjectIndex(id integer primary key,ProjectName varchar(255),"
//				+ "CreateTime text,StartChainage double,EndChainage double,"
//				+ "LastOpenTime text,Info text, ChainagePrefix varchar(255),"
//				+ "GDLimitVelocity float,GDLimitTotalSettlement float,SLLimitVelocity float"
//				+ "SLLimitTotalSettlement float,DBLimitVelocity float,DBLimitTotalSettlement float"
//				+ ",ConstructionFirm varchar(255),LimitedTotalSubsidenceTime text)");
//		// 创建TunnelCrossSectionIndex表 所有隧道内断面基础信息表
//		db.execSQL("create table if not exists TunnelCrossSectionIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT,Chainage double,"
//				+ "InbuiltTime text,Width float,ExcavateMethod integer,"
//				+ "SurveyPntName VARCHAR(255),Info text,ChainagePrefix varchar(255),"
//				+ "GDU0 FLOAT,"
//				+ "GDVelocity FLOAT,"
//				+ "GDU0Time DateTime,"
//				+ "GDU0Description TEXT,"
//				+ "SLU0 FLOAT,"
//				+ "SLLimitVelocity FLOAT,"
//				+ "SLU0Time DateTime,"
//				+ "SLU0Description TEXT,"
//				+ "Lithologic varchar(255),"
//				+ "LAYVALUE FLOAT," + "ROCKGRADE varchar(255))");
//
//		// 创建SubsidenceCrossSectionIndex表 所有地表下沉断面基础信息表
//		db.execSQL("create table if not exists SubsidenceCrossSectionIndex("
//				+ "Id INTEGER PRIMARY KEY AUTOINCREMENT,Chainage DOUBLE,InbuiltTime DateTime,"
//				+ "Width DOUBLE,SurveyPnts INTEGER,SurveyPntName varchar(255),Info TEXT,"
//				+ "ChainagePrefix VARCHAR(255),DBU0 FLOAT,DBVelocity FLOAT,"
//				+ "DBU0Time DateTime,DBU0Description TEXT,Lithologic VARCHAR(255),"
//				+ "LAYVALUE FLOAT,ROCKGRADE VARCHAR(255))");
//
//		// 创建 TunnelCrossSectionExIndex表 满足铁科院上传接口要求的断面基础信息表
//		db.execSQL("create table if not exists TunnelCrossSectionExIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT"
//				+ ",ZONECODE  varchar(64),"
//				+ "SITECODE  varchar(64),"
//				+ "SECTNAME  varchar(64),"
//				+ "SECTCODE  varchar(64),"
//				+ "SECTKILO  varchar(64),"
//				+ "METHOD  varchar(10),"
//				+ "WIDTH  float,"
//				+ "MOVEVALUE_U0  float,"
//				+ "UPDATEDATE  DateTime,"
//				+ "REMARK_U0  varchar(255),"
//				+ "HOLENAME  varchar(64),"
//				+ "HOLESTARTKILO  varchar(64),"
//				+ "HOLEENDKILO  varchar(64),"
//				+ "INNERCODES  TEXT,"
//				+ "LAYTIME  DateTime,"
//				+ "UPLOAD  INTEGER,"
//				+ "DESCRIPTION  TEXT)");
//
//		// 创建RawSheetIndex表 隧道内断面记录单和地表下沉记录单的索引
//		db.execSQL("create table if not exists RawSheetIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT"
//				+ ",CrossSectionType  INTEGER,"
//				+ "CreateTime  DateTime,"
//				+ "Info  TEXT,"
//				+ "FACEDK  Double,"
//				+ "FACEDESCRIPTION  TEXT,"
//				+ "TEMPERATURE  Double," + "CrossSectionIDs  TEXT)");
//
//		// 创建SubsidenceTotalData表 地表下沉断面记录单的所有数据
//		db.execSQL("create table if not exists SubsidenceTotalData(id integer primary key,"
//				+ "StationId  INTEGER,"
//				+ "ChainageId  INTEGER,"
//				+ "SheetId  INTEGER,"
//				+ "Coordinate  TEXT,"
//				+ "PntType  TEXT,"
//				+ "SurveyTime  DateTime,"
//				+ "MEASNo  INTEGER,"
//				+ "SurveyorID  INTEGER,"
//				+ "DataStatus  INTEGER,"
//				+ "DataCorrection  FLOAT," + "Info  TEXT"+")");
//
//		// 创建 TunnelSettlementTotalData表 隧道内断面记录单的所有数据
//		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key,"
//				+ "StationId  INTEGER,"
//				+ "ChainageId  INTEGER,"
//				+ "SheetId  INTEGER,"
//				+ "Coordinate  TEXT,"
//				+ "PntType  TEXT,"
//				+ "SurveyTime  DateTime,"
//				+ "MEASNo  INTEGER,"
//				+ "SurveyorID  INTEGER,"
//				+ "DataStatus  INTEGER,"
//				+ "DataCorrection  FLOAT," + "Info  TEXT"+")");
//
//		// 创建 StationInfoIndex表 设站信息表
//		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key,StationPointId integer,"
//				+ "StationHeight double,BackSightPointIds text,BackeSightHeight text,"
//				+ "CreateTime text," + "Info text)");
//		// 创建 ControlPointsIndex表 控制点管理
//		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key,Name varchar(255),"
//				+ "x double,y double,z double,Info text)");
//		// 创建 TotalStationIndex表 全站仪连接参数信息
//		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key,Name varchar(255),"
//				+ "BaudRate integer,Port integer,Parity integer,Databits integer,Stopbits integer,Info text)");
//	}
//
//	@Override
//	/**
//	 * 当数据库需要被更新的时候执行，例如删除久表，创建新表。
//	 */
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("alter table user add age int");
//		System.out.println("更新");
//	}
//}
