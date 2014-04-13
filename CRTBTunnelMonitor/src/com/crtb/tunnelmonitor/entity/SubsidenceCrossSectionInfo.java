package com.crtb.tunnelmonitor.entity;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 地表下沉断面
 */
@Table(TableName="SubsidenceCrossSectionInfo")
public class SubsidenceCrossSectionInfo {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;						//记录id
	
	@ColumnString(length = 64)
	private String prefix; 				// 前缀
	
	@ColumnFloat
	private float chainage;				//断面历程值
	
	@ColumnString(length = 32)
	private String chainageName;		//断面名称
	
	@ColumnString(length = 32)
	private String InbuiltTime;			//埋设时间
	
	@ColumnInt
	private int Width;					//宽度
	
	@ColumnInt
	private int points;					//监测点
	
	@ColumnString(length = 32)
	private String surveyPnts;			//测点编号
	
	@ColumnString(length = 512)
	private String info;				//备注
	
	@ColumnFloat
	private float DBU0;					//地表uo值
	
	@ColumnString(length = 32)
	private String DBU0Time;			//限差修改时间
	
	@ColumnString(length = 512)
	private String DBU0Description;		//拱顶极限备注
	
	@ColumnFloat
	private float DBLimitVelocity;		//地表下沉速率
	
	@ColumnString(length = 32)
	private String lithologic;			//岩性
	
	@ColumnFloat
	private float Layvalue;				//埋深值
	
	@ColumnString(length = 32)
	private String Rockgrade;			//围岩级别
	
	private boolean bUse;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public float getChainage() {
		return chainage;
	}

	public void setChainage(float chainage) {
		this.chainage = chainage;
	}

	public String getChainageName() {
		return chainageName;
	}

	public void setChainageName(String chainageName) {
		this.chainageName = chainageName;
	}

	public String getInbuiltTime() {
		return InbuiltTime;
	}

	public void setInbuiltTime(String inbuiltTime) {
		InbuiltTime = inbuiltTime;
	}

	public int getWidth() {
		return Width;
	}

	public void setWidth(int width) {
		Width = width;
	}

	public String getSurveyPnts() {
		return surveyPnts;
	}

	public void setSurveyPnts(String surveyPnts) {
		this.surveyPnts = surveyPnts;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public float getDBU0() {
		return DBU0;
	}

	public void setDBU0(float dBU0) {
		DBU0 = dBU0;
	}

	public String getDBU0Time() {
		return DBU0Time;
	}

	public void setDBU0Time(String dBU0Time) {
		DBU0Time = dBU0Time;
	}

	public String getDBU0Description() {
		return DBU0Description;
	}

	public void setDBU0Description(String dBU0Description) {
		DBU0Description = dBU0Description;
	}

	public float getDBLimitVelocity() {
		return DBLimitVelocity;
	}

	public void setDBLimitVelocity(float dBLimitVelocity) {
		DBLimitVelocity = dBLimitVelocity;
	}

	public String getLithologic() {
		return lithologic;
	}

	public void setLithologic(String lithologic) {
		this.lithologic = lithologic;
	}

	public float getLayvalue() {
		return Layvalue;
	}

	public void setLayvalue(float layvalue) {
		Layvalue = layvalue;
	}

	public String getRockgrade() {
		return Rockgrade;
	}

	public void setRockgrade(String rockgrade) {
		Rockgrade = rockgrade;
	}

	public boolean isbUse() {
		return bUse;
	}

	public void setbUse(boolean bUse) {
		this.bUse = bUse;
	}
	
	
}
