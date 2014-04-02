package com.sxlc.entity;

import java.sql.Timestamp;


/**
 * 地表下沉断面
 *创建时间：2014-3-24上午11:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class SubsidenceCrossSectionInfo {
	private String ChainageName;		//断面名称
	private int id;				//记录id
	private Double Chainage;	//断面历程值
	private Timestamp InbuiltTime;		//埋设时间
	private int Width;					//宽度
	private String SurveyPnts;			//测点编号
	private String Info;				//备注
	private String ChainagePrefix;		//历程前缀
	private float DBU0;					//地表uo值
	private Timestamp DBU0Time;			//限差修改时间
	private String DBU0Description;		//拱顶极限备注
	private float DBLimitVelocity;		//地表下沉速率
	private String Lithologic;			//岩性
	private float Layvalue;				//埋深值
	private String Rockgrade;			//围岩级别
	private boolean bUse;
	
	public boolean isbUse() {
		return bUse;
	}
	public void setbUse(boolean bUse) {
		this.bUse = bUse;
	}
	public int getId() {
		return id;
	}
	public String getChainageName() {
		return ChainageName;
	}
	public void setChainageName(String chainageName) {
		ChainageName = chainageName;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Double getChainage() {
		return Chainage;
	}
	public void setChainage(Double chainage) {
		Chainage = chainage;
	}
	public Timestamp getInbuiltTime() {
		return InbuiltTime;
	}
	public void setInbuiltTime(Timestamp inbuiltTime) {
		InbuiltTime = inbuiltTime;
	}
	public int getWidth() {
		return Width;
	}
	public void setWidth(int width) {
		Width = width;
	}
	public String getSurveyPnts() {
		return SurveyPnts;
	}
	public void setSurveyPnts(String surveyPnts) {
		SurveyPnts = surveyPnts;
	}
	public String getInfo() {
		return Info;
	}
	public void setInfo(String info) {
		Info = info;
	}
	public String getChainagePrefix() {
		return ChainagePrefix;
	}
	public void setChainagePrefix(String chainagePrefix) {
		ChainagePrefix = chainagePrefix;
	}
	public float getDBU0() {
		return DBU0;
	}
	public void setDBU0(float dBU0) {
		DBU0 = dBU0;
	}
	public Timestamp getDBU0Time() {
		return DBU0Time;
	}
	public void setDBU0Time(Timestamp dBU0Time) {
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
		return Lithologic;
	}
	public void setLithologic(String lithologic) {
		Lithologic = lithologic;
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
}
