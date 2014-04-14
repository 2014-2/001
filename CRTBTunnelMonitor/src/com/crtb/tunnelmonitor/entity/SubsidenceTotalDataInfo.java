package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 断面测量记录单实体
 */

public class SubsidenceTotalDataInfo implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;				//id
	
	@ColumnInt
	private int type;			//类型 0：断面记录单  1：地表下沉记录单
	
	@ColumnInt
	private int stationId;		//设站id
	
	@ColumnInt
	private int chainageId;		//断面里程id
	
	@ColumnInt
	private int sheetId;		//记录单id
	
	@ColumnString(length=16)
	private String coordinate;	//测点坐标
	
	@ColumnString(length=16)
	private String pntType;		//测点类型
	
	@ColumnString(length=32)
	private String surveyTime;	//测量时间
	
	@ColumnString(length=512)
	private String info;		//备注
	
	@ColumnInt
	private int MEASNo;			//第几次测量
	
	@ColumnInt
	private int surveyorID;		//测量人员id
	
	@ColumnInt
	private int dataStatus;		//异常数据标识
	
	@ColumnFloat
	private float dataCorrection;	//异常数据修正值

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public int getChainageId() {
		return chainageId;
	}

	public void setChainageId(int chainageId) {
		this.chainageId = chainageId;
	}

	public int getSheetId() {
		return sheetId;
	}

	public void setSheetId(int sheetId) {
		this.sheetId = sheetId;
	}

	public String getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	public String getPntType() {
		return pntType;
	}

	public void setPntType(String pntType) {
		this.pntType = pntType;
	}

	public String getSurveyTime() {
		return surveyTime;
	}

	public void setSurveyTime(String surveyTime) {
		this.surveyTime = surveyTime;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getMEASNo() {
		return MEASNo;
	}

	public void setMEASNo(int mEASNo) {
		MEASNo = mEASNo;
	}

	public int getSurveyorID() {
		return surveyorID;
	}

	public void setSurveyorID(int surveyorID) {
		this.surveyorID = surveyorID;
	}

	public int getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(int dataStatus) {
		this.dataStatus = dataStatus;
	}

	public float getDataCorrection() {
		return dataCorrection;
	}

	public void setDataCorrection(float dataCorrection) {
		this.dataCorrection = dataCorrection;
	}
}
