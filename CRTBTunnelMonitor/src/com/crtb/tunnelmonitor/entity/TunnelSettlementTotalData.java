package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 隧道内断面记录单,更新时间: 2014-05-27
 * 
 * @author zhouwei
 *
 */
@Table(TableName="TunnelSettlementTotalData")
public class TunnelSettlementTotalData implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;				// id
	
	@ColumnText
	private String Guid ;		// 唯一标示
	
	@ColumnText
	private String StationId;	// 设站ID
	
	@ColumnText
	private String ChainageId;	// 断面guid
	
	@ColumnText
	private String SheetId;		// 记录单guid
	
	@ColumnText
	private String Coordinate;	// 测点坐标
	
	@ColumnText
	private String PntType;		// 测点类型
	
	@ColumnDate
	private Date SurveyTime;	// 测量时间
	
	@ColumnInt
	private int UploadStatus ;  // 上传类型 0表示全部状态；1表示未上传，2表示不上传，3表示部分上传
	
	@ColumnText
	private String Info;		// 备注
	
	@ColumnInt
	private int MEASNo;			// 第几次测量
	
	@ColumnText
	private String SurveyorID;	// 测量人员id
	
	@ColumnInt
	private int DataStatus;		// 异常数据标识 
								// 0:正常
								// 1:数据不参与计算
								// 2:数据作为首行
								// 3:改正值

	@ColumnFloat
	private float DataCorrection;	// 异常数据修正值
	
	public TunnelSettlementTotalData(){
		setGuid(CrtbUtils.generatorGUID());
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public int getUploadStatus() {
		return UploadStatus;
	}

	public void setUploadStatus(int uploadStatus) {
		UploadStatus = uploadStatus;
	}

	public float getDataCorrection() {
		return DataCorrection;
	}

	public void setDataCorrection(float dataCorrection) {
		DataCorrection = dataCorrection;
	}
	
	public String getStationId() {
		return StationId;
	}

	public void setStationId(String stationId) {
		StationId = stationId;
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

	public String getChainageId() {
		return ChainageId;
	}

	public void setChainageId(String chainageId) {
		ChainageId = chainageId;
	}

	public String getSheetId() {
		return SheetId;
	}

	public void setSheetId(String sheetId) {
		SheetId = sheetId;
	}

	public String getSurveyorID() {
		return SurveyorID;
	}

	public void setSurveyorID(String surveyorID) {
		SurveyorID = surveyorID;
	}

	public int getDataStatus() {
		return DataStatus;
	}

	public void setDataStatus(int dataStatus) {
		DataStatus = dataStatus;
	}
}
