package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 预警内容实体
 */
@Table(TableName = "AlertInfo")
public class AlertInfo implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id; // id

	@ColumnString(length = 32)
	private String SheetID; // 记录单id

	@ColumnString(length = 32)
	private String crossSectionID; // 断面唯一id

	@ColumnInt
	private int pntType; // 测点类型

	@ColumnString(length = 32)
	private String alertTime; // 预警时间

	@ColumnInt
	private int alertLeverl; // 预警等级

	@ColumnInt
	private int utype; // 超限类型

	@ColumnFloat
	private float uValue; // 超限数值

	@ColumnFloat
	private float uMax; // 超限类型阈值

	@ColumnString(length = 32)
	private String originalDataID; // 原始数据id

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSheetID() {
		return SheetID;
	}

	public void setSheetID(String sheetID) {
		SheetID = sheetID;
	}

	public String getCrossSectionID() {
		return crossSectionID;
	}

	public void setCrossSectionID(String crossSectionID) {
		this.crossSectionID = crossSectionID;
	}

	public int getPntType() {
		return pntType;
	}

	public void setPntType(int pntType) {
		this.pntType = pntType;
	}

	public String getAlertTime() {
		return alertTime;
	}

	public void setAlertTime(String alertTime) {
		this.alertTime = alertTime;
	}

	public int getAlertLeverl() {
		return alertLeverl;
	}

	public void setAlertLeverl(int alertLeverl) {
		this.alertLeverl = alertLeverl;
	}

	public int getUtype() {
		return utype;
	}

	public void setUtype(int utype) {
		this.utype = utype;
	}

	public float getuValue() {
		return uValue;
	}

	public void setuValue(float uValue) {
		this.uValue = uValue;
	}

	public float getuMax() {
		return uMax;
	}

	public void setuMax(float uMax) {
		this.uMax = uMax;
	}

	public String getOriginalDataID() {
		return originalDataID;
	}

	public void setOriginalDataID(String originalDataID) {
		this.originalDataID = originalDataID;
	}

}
