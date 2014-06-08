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
@Table(TableName = "ConvergenceSettlementArching")
public class ConvergenceSettlementArching implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;

	@ColumnText
	private String OriginalDataId_One;

	@ColumnText
	private String SheetId;

	@ColumnDouble
	private double CurrnetConvergence;

	@ColumnDouble
	private double TotalConvergence;

	@ColumnFloat
	private float CurrentVelocity;

	@ColumnFloat
	private float TotalVelocity;

	@ColumnFloat
	private float CurrnetTimeSpan;

	@ColumnFloat
	private float TotalTimeSpan;

	@ColumnDouble
	private double TunnelFaceDistance;

	@ColumnText
	private String Info;

	@ColumnText
	private String OriginalDataId_Two;

	@ColumnText
	private String ChainageId;

	@ColumnShort
	private short ManageLevel;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getOriginalDataId_One() {
		return OriginalDataId_One;
	}

	public void setOriginalDataId_One(String originalDataId_One) {
		OriginalDataId_One = originalDataId_One;
	}

	public String getSheetId() {
		return SheetId;
	}

	public void setSheetId(String sheetId) {
		SheetId = sheetId;
	}

	public double getCurrnetConvergence() {
		return CurrnetConvergence;
	}

	public void setCurrnetConvergence(double currnetConvergence) {
		CurrnetConvergence = currnetConvergence;
	}

	public double getTotalConvergence() {
		return TotalConvergence;
	}

	public void setTotalConvergence(double totalConvergence) {
		TotalConvergence = totalConvergence;
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

	public String getOriginalDataId_Two() {
		return OriginalDataId_Two;
	}

	public void setOriginalDataId_Two(String originalDataId_Two) {
		OriginalDataId_Two = originalDataId_Two;
	}

	public String getChainageId() {
		return ChainageId;
	}

	public void setChainageId(String chainageId) {
		ChainageId = chainageId;
	}

	public short getManageLevel() {
		return ManageLevel;
	}

	public void setManageLevel(short manageLevel) {
		ManageLevel = manageLevel;
	}

}
