package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 预警内容实体
 */
@Table(TableName = "AlertList")
public class AlertList implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID; 						// id

	@ColumnString(length = 255)
	private String SheetID; 					// 记录单id

	@ColumnInt
	private int CrossSectionID; 			// 断面唯一id

	@ColumnString(length = 10)
	private String PntType; 					// 测点类型

	@ColumnDate
	private Date AlertTime; 				// 预警时间

	@ColumnInt
	private int AlertLeverl; 				// 预警等级

	@ColumnInt
	private int Utype; 						// 超限类型

	@ColumnDouble
	private double UValue; 					// 超限数值

	@ColumnFloat
	private double UMax; 					// 超限类型阈值

	@ColumnString(length = 255)
	private String OriginalDataID; 			// 原始数据id

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getSheetID() {
		return SheetID;
	}

	public void setSheetID(String sheetID) {
		SheetID = sheetID;
	}

	public int getCrossSectionID() {
		return CrossSectionID;
	}

	public void setCrossSectionID(int crossSectionID) {
		CrossSectionID = crossSectionID;
	}

	public String getPntType() {
		return PntType;
	}

	public void setPntType(String pntType) {
		PntType = pntType;
	}

	public Date getAlertTime() {
		return AlertTime;
	}

	public void setAlertTime(Date alertTime) {
		AlertTime = alertTime;
	}

	public int getAlertLeverl() {
		return AlertLeverl;
	}

	public void setAlertLeverl(int alertLeverl) {
		AlertLeverl = alertLeverl;
	}

	public int getUtype() {
		return Utype;
	}

	public void setUtype(int utype) {
		Utype = utype;
	}

	public double getUValue() {
		return UValue;
	}

	public void setUValue(double uValue) {
		UValue = uValue;
	}

	public double getUMax() {
		return UMax;
	}

	public void setUMax(double uMax) {
		UMax = uMax;
	}

	public String getOriginalDataID() {
		return OriginalDataID;
	}

	public void setOriginalDataID(String originalDataID) {
		OriginalDataID = originalDataID;
	}

}
