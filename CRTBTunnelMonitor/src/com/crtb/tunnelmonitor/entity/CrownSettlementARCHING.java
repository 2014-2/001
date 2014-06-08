package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnShort;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 新增加表
 * 
 * @author zhouwei
 *
 */
@Table(TableName="CrownSettlementARCHING")
public class CrownSettlementARCHING implements Serializable {

	@ColumnPrimaryKey(Type=PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID ;
	
	@ColumnText
	private String OriginalDataId ;
	
	@ColumnText
	private String SheetId ;
	
	@ColumnDouble
	private double CurrentSettlement ;
	
	@ColumnDouble
	private double TotalSettlement ;
	
	@ColumnFloat
	private float CurrentVelocity ;
	
	@ColumnFloat
	private float TotalVelocity ;
	
	@ColumnFloat
	private float CurrnetTimeSpan ;
	
	@ColumnFloat
	private float TotalTimeSpan ;
	
	@ColumnDouble
	private double TunnelFaceDistance ;
	
	@ColumnText
	private String Info ;
	
	@ColumnShort
	private short ManageLevel ;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getOriginalDataId() {
		return OriginalDataId;
	}

	public void setOriginalDataId(String originalDataId) {
		OriginalDataId = originalDataId;
	}

	public String getSheetId() {
		return SheetId;
	}

	public void setSheetId(String sheetId) {
		SheetId = sheetId;
	}

	public double getCurrentSettlement() {
		return CurrentSettlement;
	}

	public void setCurrentSettlement(double currentSettlement) {
		CurrentSettlement = currentSettlement;
	}

	public double getTotalSettlement() {
		return TotalSettlement;
	}

	public void setTotalSettlement(double totalSettlement) {
		TotalSettlement = totalSettlement;
	}

	public float getCurrentVelocity() {
		return CurrentVelocity;
	}

	public void setCurrentVelocity(float currentVelocity) {
		CurrentVelocity = currentVelocity;
	}

	public float getTotalVelocity() {
		return TotalVelocity;
	}

	public void setTotalVelocity(float totalVelocity) {
		TotalVelocity = totalVelocity;
	}

	public float getCurrnetTimeSpan() {
		return CurrnetTimeSpan;
	}

	public void setCurrnetTimeSpan(float currnetTimeSpan) {
		CurrnetTimeSpan = currnetTimeSpan;
	}

	public float getTotalTimeSpan() {
		return TotalTimeSpan;
	}

	public void setTotalTimeSpan(float totalTimeSpan) {
		TotalTimeSpan = totalTimeSpan;
	}

	public double getTunnelFaceDistance() {
		return TunnelFaceDistance;
	}

	public void setTunnelFaceDistance(double tunnelFaceDistance) {
		TunnelFaceDistance = tunnelFaceDistance;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public short getManageLevel() {
		return ManageLevel;
	}

	public void setManageLevel(short manageLevel) {
		ManageLevel = manageLevel;
	}
}
