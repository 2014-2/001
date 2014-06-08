package com.crtb.tunnelmonitor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelperDTMS extends SQLiteOpenHelper {

	/** 版本号 */
	private static final int VERSION = 1;
	
	private static final String DB_NAME="tunnel.db";

	/**
	 * 带参构造方法
	 * 
	 * @param context
	 *            上下文
	 * @param factory
	 *            工厂
	 * @param version
	 *            版本号
	 */
	public SqliteHelperDTMS(Context context, CursorFactory factory,
			int version) {
		super(context, DB_NAME, null, VERSION);
	}
	
	public SqliteHelperDTMS(Context context){
		this(context,null,VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// 创建ProjectIndex表
		db.execSQL("create table if not exists ProjectIndex(id INTEGER PRIMARY KEY AUTOINCREMENT ,ProjectName varchar(255),"
				+ "CreateTime text,StartChainage double,EndChainage double,"
				+ "LastOpenTime text,Info text, ChainagePrefix varchar(255),"
				+ "GDLimitVelocity float,GDLimitTotalSettlement float,SLLimitVelocity float,"
				+ "SLLimitTotalSettlement float,DBLimitVelocity float,DBLimitTotalSettlement float"
				+ ",ConstructionFirm varchar(255),LimitedTotalSubsidenceTime text)");
		//创建ProjectManage表
		String sql = "create table if not exists ProjectManage(Id INTEGER PRIMARY KEY AUTOINCREMENT ," +
				"CurrentProject integer," +
				"LastOpenProject integer," +
				"Info text)";
		db.execSQL(sql);
		
		//创建 ProjectSettingIndex 表
		sql = "create table if not exists ProjectSettingIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT ," +
				"ProjectName VARCHAR(255)," +
				"ProjectID INTEGER," +
				"YMDFormat INTEGER," +
				"HMSFormat INTEGER," +
				"ChainagePrefix varchar(255)," +
				"MaxDeformation DOUBLE," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建TotalStationSettingIndex表
		sql = "create table if not exists TotalStationSettingIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT ," +
				"Name varchar(255)," +
				"Port INTEGER," +
				"Baudrate INTEGER," +
				"ReconnectTimes INTEGER," +
				"DataBits INTEGER," +
				"DataStops INTEGER," +
				"Parity varchar(255)," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建DTMSVersion表
		sql = "create table if not exists DTMSVersion(Id INTEGER PRIMARY KEY AUTOINCREMENT ," +
				"AppVer varchar(255)," +
				"DBVer INTEGER," +
				"Info TEXT)";
		db.execSQL(sql);
		
		// 创建TunnelCrossSectionIndex表 所有隧道内断面基础信息表		
		db.execSQL("create table if not exists TunnelCrossSectionIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT,Chainage double,"
				+ "InbuiltTime text,Width float,ExcavateMethod integer,"
				+ "SurveyPntName VARCHAR(255),Info text,ChainagePrefix varchar(255),"
				+ "GDU0 FLOAT,"
				+ "GDVelocity FLOAT,"
				+ "GDU0Time DateTime,"
				+ "GDU0Description TEXT,"
				+ "SLU0 FLOAT,"
				+ "SLLimitVelocity FLOAT,"
				+ "SLU0Time DateTime,"
				+ "SLU0Description TEXT,"
				+ "Lithologic varchar(255),"
				+ "LAYVALUE FLOAT," + "ROCKGRADE varchar(255))");

		// 创建SubsidenceCrossSectionIndex表 所有地表下沉断面基础信息表
		sql = "create table if not exists SubsidenceCrossSectionIndex("
				+ "Id INTEGER PRIMARY KEY AUTOINCREMENT,Chainage DOUBLE,InbuiltTime DateTime,"
				+ "Width DOUBLE,SurveyPnts varchar(255),Info TEXT,"
				+ "ChainagePrefix VARCHAR(255),DBU0 FLOAT,DBLimitVelocity FLOAT,"
				+ "DBU0Time DateTime,DBU0Description TEXT,Lithologic VARCHAR(255),"
				+ "LAYVALUE FLOAT,ROCKGRADE VARCHAR(255))";
		db.execSQL(sql);

		// 创建 TunnelCrossSectionExIndex表 满足铁科院上传接口要求的断面基础信息表
		db.execSQL("create table if not exists TunnelCrossSectionExIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ",ZONECODE  varchar(64),"
				+ "SITECODE  varchar(64),"
				+ "SECTNAME  varchar(64),"
				+ "SECTCODE  varchar(64),"
				+ "SECTKILO  varchar(64),"
				+ "METHOD  varchar(10),"
				+ "WIDTH  float,"
				+ "MOVEVALUE_U0  float,"
				+ "UPDATEDATE  DateTime,"
				+ "REMARK_U0  varchar(255),"
				+ "HOLENAME  varchar(64),"
				+ "HOLESTARTKILO  varchar(64),"
				+ "HOLEENDKILO  varchar(64),"
				+ "INNERCODES  TEXT,"
				+ "LAYTIME  DateTime,"
				+ "UPLOAD  INTEGER,"
				+ "DESCRIPTION  TEXT)");

		// 创建RawSheetIndex表 隧道内断面记录单和地表下沉记录单的索引
		db.execSQL("create table if not exists RawSheetIndex(Id INTEGER PRIMARY KEY AUTOINCREMENT"
				+ ",CrossSectionType  INTEGER,"
				+ "CreateTime  DateTime,"
				+ "Info  TEXT,"
				+ "FACEDK  Double,"
				+ "FACEDESCRIPTION  TEXT,"
				+ "TEMPERATURE  Double," + "CrossSectionIDs  TEXT)");

		// 创建SubsidenceTotalData表 地表下沉断面记录单的所有数据
		db.execSQL("create table if not exists SubsidenceTotalData(id integer primary key AUTOINCREMENT,"
				+ "StationId  INTEGER,"
				+ "ChainageId  INTEGER,"
				+ "SheetId  INTEGER,"
				+ "Coordinate  TEXT,"
				+ "PntType  TEXT,"
				+ "SurveyTime  DateTime,"
				+ "MEASNo  INTEGER,"
				+ "SurveyorID  INTEGER,"
				+ "DataStatus  INTEGER,"
				+ "DataCorrection  FLOAT," + "Info  TEXT" + ")");

		// 创建 TunnelSettlementTotalData表 隧道内断面记录单的所有数据
		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key AUTOINCREMENT,"
				+ "StationId  INTEGER,"
				+ "ChainageId  INTEGER,"
				+ "SheetId  INTEGER,"
				+ "Coordinate  TEXT,"
				+ "PntType  TEXT,"
				+ "SurveyTime  DateTime,"
				+ "MEASNo  INTEGER,"
				+ "SurveyorID  INTEGER,"
				+ "DataStatus  INTEGER,"
				+ "DataCorrection  FLOAT," + "Info  TEXT" + ")");
		
		//创建SubsidenceRecord 表
		sql = "create table if not exists SubsidenceRecord(Id integer primary key AUTOINCREMENT," +
				"StationId INTEGER," +
				"ChainageId INTEGER," +
				"SheetId INTEGER," +
				"Coordinate TEXT," +
				"PntType TEXT," +
				"SurveyTime DateTime," +
				"ConstructionFirm varchar(255)," +
				"Surveyer varchar(255)," +
				"Recorder varchar(255)," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建TunnelCrossSectionRecord表
		sql = "create table if not exists TunnelCrossSectionRecord(Id integer primary key AUTOINCREMENT," +
				"StationId INTEGER," +
				"ChainageId INTEGER," +
				"SheetId INTEGER," +
				"Coordinate TEXT," +
				"PntType TEXT," +
				"SurveyTime DateTime," +
				"ConstructionFirm varchar(255)," +
				"Surveyer varchar(255)," +
				"Recorder varchar(255)," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建TotalStationIndex表
		sql = "create table if not exists TotalStationIndex(Id integer primary key AUTOINCREMENT," +
				"Name varchar(255)," +
				"TotalstationType TEXT," +
				"BaudRate INTEGER," +
				"Port INTEGER," +
				"Parity INTEGER," +
				"Databits INTEGER," +
				"Stopbits INTEGER," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建StationInfoIndex表
		sql = "create table if not exists StationInfoIndex(Id integer primary key AUTOINCREMENT," +
				"StationPointIndex INTEGER," +
				"StationHeight DOUBLE," +
				"BackSightPointIds TEXT," +
				"BackeSightHeight DOUBLE," +
				"CreateTime DateTime," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建 ControlPointsIndex 表
		sql = "create table if not exists ControlPointsIndex(Id integer primary key AUTOINCREMENT," +
				"Name varchar(255)," +
				"X double," +
				"Y double," +
				"Z double," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建 DTMSProjectVersion 表
		sql = "create table if not exists DTMSProjectVersion(Id integer primary key AUTOINCREMENT," +
				"AppVer varchar(255)," +
				"DBVer INTEGER," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建 CrownSettlementArching 表
		sql = "create table if not exists CrownSettlementArching(Id integer primary key AUTOINCREMENT," +
				"OriginalDataId INTEGER," +
				"SheetId INTEGER," +
				"CurrentSettlement DOUBLE," +
				"TotalSettlement DOUBLE," +
				"CurrentVelocity FLOAT," +
				"TotalVelocity FLOAT," +
				"CurrnetTimeSpan FLOAT," +
				"TotalTimeSpan FLOAT," +
				"TunnelFaceDistance DOUBLE," +
				"ManageLevel USHORT," +
				"Info TEXT)";
		db.execSQL(sql);
		
		// 创建 SubsidenceSettlementArching 表
		sql = "create table if not exists SubsidenceSettlementArching(Id integer primary key AUTOINCREMENT," +
				"OriginalDataId INTEGER," +
				"SheetId INTEGER," +
				"CurrentSettlement DOUBLE," +
				"TotalSettlement DOUBLE," +
				"CurrentVelocity FLOAT," +
				"TotalVelocity FLOAT," +
				"CurrnetTimeSpan FLOAT," +
				"TotalTimeSpan FLOAT," +
				"TunnelFaceDistance DOUBLE," +
				"ManageLevel USHORT," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建 ConvergenceSettlementArching 表
		sql = "create table if not exists ConvergenceSettlementArching(Id integer primary key AUTOINCREMENT," +
				"OriginalDataId_One INTEGER," +
				"OriginalDataId_Two INTEGER," +
				"ChainageId INTEGER," +
				"SheetId INTEGER," +
				"CurrnetConvergence DOUBLE," +
				"TotalConvergence DOUBLE," +
				"CurrentVelocity FLOAT," +
				"TotalVelocity FLOAT," +
				"CurrnetTimeSpan FLOAT," +
				"TotalTimeSpan FLOAT," +
				"TunnelFaceDistance DOUBLE," +
				"ManageLevel USHORT," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建 SurveyerInformation 表
		sql = "create table if not exists SurveyerInformation(Id integer primary key AUTOINCREMENT," +
				"SurveyerName varchar(100)," +
				"CertificateID varchar(20)," +
				"Password varchar(64)," +
				"ProjectID INTEGER," +
				"Info TEXT)";
		db.execSQL(sql);
		
		//创建RawSheetIndex_Index表
		sql = "create table if not exists RawSheetIndex_Index(Id integer primary key AUTOINCREMENT," +
				"CrossSectionType INTEGER," +
				"CreateTime DateTime)";
		db.execSQL(sql);
		
		//创建 TunnelCrossSectionIndex_Index 表
		sql = "create table if not exists TunnelCrossSectionIndex_Index(Id integer primary key AUTOINCREMENT," +
				"Chainage DOUBLE)";
		db.execSQL(sql);
		
		//创建SubsidenceCrossSectionIndex_Index表
		sql = "create table if not exists SubsidenceCrossSectionIndex_Index(Id integer primary key AUTOINCREMENT," +
		"Chainage DOUBLE)";
		db.execSQL(sql);
		
		//创建TunnelSettlementTotalData_Index表
		sql = "create table if not exists TunnelSettlementTotalData_Index(Id INTEGER primary key AUTOINCREMENT," +
				"StationId INTEGER," +
				"ChainageId INTEGER," +
				"SheetId INTEGER," +
				"Coordinate TEXT)";
		db.execSQL(sql);
		
		//创建SubsidenceTotalData_Index表
		sql = "create table if not exists SubsidenceTotalData_Index(Id INTEGER primary key AUTOINCREMENT," +
				"StationId INTEGER," +
				"ChainageId INTEGER," +
				"SheetId INTEGER)";
		db.execSQL(sql);
		
		//创建StationInfoIndex_Index表
		sql = "create table if not exists StationInfoIndex_Index(Id INTEGER primary key AUTOINCREMENT," +
				"CreateTime DateTime)";
		db.execSQL(sql);
		
		//创建 ControlPointsIndex_Index表
		sql = "create table if not exists ControlPointsIndex_Index(Id INTEGER primary key AUTOINCREMENT," +
				"Name varchar(255))";
		db.execSQL(sql);
		
		//创建 CrownSettlementArching_Index 表
		sql = "create table if not exists CrownSettlementArching_Index(Id INTEGER primary key AUTOINCREMENT," +
				"OriginalDataId INTEGER," +
				"SheetId INTEGER)";
		db.execSQL(sql);
		
		//创建 SubsidenceSettlementArching_Index 表
		sql = "create table if not exists SubsidenceSettlementArching_Index(Id INTEGER primary key AUTOINCREMENT," +
				"OriginalDataId INTEGER," +
				"SheetId INTEGER)";
		db.execSQL(sql);
		
		//创建 ConvergenceSettlementArching_Index 表
		sql = "create table if not exists ConvergenceSettlementArching_Index(Id INTEGER primary key AUTOINCREMENT," +
				"SheetId INTEGER," +
				"OriginalDataId_One INTEGER," +
				"OriginalDataId_Two INTEGER," +
				"ChainageId INTEGER)";
		db.execSQL(sql);
		
		// 创建 StationInfoIndex表 设站信息表
		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key,StationPointId integer,"
				+ "StationHeight double,BackSightPointIds text,BackeSightHeight text,"
				+ "CreateTime text," + "Info text)");
		// 创建 ControlPointsIndex表 控制点管理
		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key,Name varchar(255),"
				+ "x double,y double,z double,Info text)");
		// 创建 TotalStationIndex表 全站仪连接参数信息
		db.execSQL("create table if not exists TunnelSettlementTotalData(id integer primary key,Name varchar(255),"
				+ "BaudRate integer,Port integer,Parity integer,Databits integer,Stopbits integer,Info text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 数据库更新是做得操作
	}
}
