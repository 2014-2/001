package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnBoolean;
import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 隧道内断面记录单
 * 
 * @author zhouwei
 *
 */
@Table(TableName="TunnelSettlementTotalData")
public class TunnelSettlementTotalData implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;				// id
	
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
	
	@ColumnString(length = 16)
	private String prefix; 			// 前缀--扩充
	
	@ColumnFloat
	private float Facedk;			// 撑子面距离--扩充
	
	@ColumnString(length = 20)
	private String facedescription;	// 施工序号--扩充
	
	@ColumnBoolean
	private boolean checked ;		// 是否选中--扩充
	
	@ColumnDate
	private Date CreateTime;		// 创建时间--扩充
	
	@ColumnFloat
	private float Temperature;   	// 温度--扩充
	
	@ColumnString(length=255)
	private String Surveyor;		// 测量员名称
	
	@ColumnString(length=255)
	private String SectionName;		// 断面名称

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public float getTemperature() {
		return Temperature;
	}

	public String getSectionName() {
		return SectionName;
	}

	public void setSectionName(String sectionName) {
		SectionName = sectionName;
	}

	public String getSurveyor() {
		return Surveyor;
	}

	public void setSurveyor(String surveyor) {
		Surveyor = surveyor;
	}

	public void setTemperature(float temperature) {
		Temperature = temperature;
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public float getFacedk() {
		return Facedk;
	}

	public void setFacedk(float facedk) {
		Facedk = facedk;
	}

	public String getFacedescription() {
		return facedescription;
	}

	public void setFacedescription(String facedescription) {
		this.facedescription = facedescription;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void setDataCorrection(float dataCorrection) {
		DataCorrection = dataCorrection;
	}

	public Date getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}
	
}
