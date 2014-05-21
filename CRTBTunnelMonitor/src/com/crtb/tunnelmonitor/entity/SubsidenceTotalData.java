package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 地表下沉断面记录单
 * 
 * @author zhouwei
 *
 */
@Table(TableName="SubsidenceTotalData")
public class SubsidenceTotalData implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;				// id
	
	@ColumnString(length = 64)
	private String guid ;		// 唯一标示 -----------扩展
	
	@ColumnInt
	private int StationId;		// 设站ID
	
	@ColumnInt
	private int ChainageId;		// 断面里程ID
	
	@ColumnInt
	private int SheetId;		// 记录单id
	
	@ColumnText
	private String Coordinate;	// 测点坐标
	
	@ColumnString(length=10)
	private String PntType;		// 测点类型
	
	@ColumnDate
	private Date SurveyTime;	// 测量时间
	
	@ColumnText
	private String Info;		// 备注
	
	@ColumnInt
	private int MEASNo;			// 第几次测量
	
	@ColumnInt
	private int SurveyorID;		// 测量人员id
	
	@ColumnInt
	private int DataStatus;		// 异常数据标识 
								// 0:正常
								// 1:数据不参与计算
								// 2:数据作为首行
								// 3:改正值
	
	@ColumnFloat
	private float DataCorrection;	// 异常数据修正值
	
	public SubsidenceTotalData(){
		setGuid(CrtbUtils.generatorGUID());
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
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

	public Date getSurveyTime() {
		return SurveyTime;
	}

	public void setSurveyTime(Date surveyTime) {
		SurveyTime = surveyTime;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public int getMEASNo() {
		return MEASNo;
	}

	public void setMEASNo(int mEASNo) {
		MEASNo = mEASNo;
	}

	public int getSurveyorID() {
		return SurveyorID;
	}

	public void setSurveyorID(int surveyorID) {
		SurveyorID = surveyorID;
	}

	public int getDataStatus() {
		return DataStatus;
	}

	public void setDataStatus(int dataStatus) {
		DataStatus = dataStatus;
	}

	public float getDataCorrection() {
		return DataCorrection;
	}

	public void setDataCorrection(float dataCorrection) {
		DataCorrection = dataCorrection;
	}

}
