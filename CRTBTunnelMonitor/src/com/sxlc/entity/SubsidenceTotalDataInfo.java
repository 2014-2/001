package com.sxlc.entity;

import java.sql.Timestamp;

import android.R.string;

/**
 * 断面测量记录单实体
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class SubsidenceTotalDataInfo {
	private int type;			//类型 0：断面记录单  1：地表下沉记录单
	private int id;				//id
	private int StationId;		//设站id
	private int ChainageId;		//断面里程id
	private int SheetId;		//记录单id
	private String Coordinate;	//测点坐标
	private String PntType;		//测点类型
	private Timestamp SurveyTime;	//测量时间
	private String Info;		//备注
	private short MEASNo;		//第几次测量
	private int SurveyorID;		//测量人员id
	private short DataStatus;	//异常数据标识
	private float DataCorrection;	//异常数据修正值
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStationId() {
		return StationId;
	}
	public void setStationId(int stationId) {
		StationId = stationId;
	}
	public int getChainageId() {
		return ChainageId;
	}
	public void setChainageId(int chainageId) {
		ChainageId = chainageId;
	}
	public int getSheetId() {
		return SheetId;
	}
	public void setSheetId(int sheetId) {
		SheetId = sheetId;
	}
	public String getCoordinate() {
		return Coordinate;
	}
	public void setCoordinate(String coordinate) {
		Coordinate = coordinate;
	}
	public String getPntType() {
		return PntType;
	}
	public void setPntType(String pntType) {
		PntType = pntType;
	}
	public Timestamp getSurveyTime() {
		return SurveyTime;
	}
	public void setSurveyTime(Timestamp surveyTime) {
		SurveyTime = surveyTime;
	}
	public String getInfo() {
		return Info;
	}
	public void setInfo(String info) {
		Info = info;
	}
	public short getMEASNo() {
		return MEASNo;
	}
	public void setMEASNo(short mEASNo) {
		MEASNo = mEASNo;
	}
	public int getSurveyorID() {
		return SurveyorID;
	}
	public void setSurveyorID(int surveyorID) {
		SurveyorID = surveyorID;
	}
	public short getDataStatus() {
		return DataStatus;
	}
	public void setDataStatus(short dataStatus) {
		DataStatus = dataStatus;
	}
	public float getDataCorrection() {
		return DataCorrection;
	}
	public void setDataCorrection(float dataCorrection) {
		DataCorrection = dataCorrection;
	}
	
}
