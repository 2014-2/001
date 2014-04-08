package com.crtb.tunnelmonitor.entity;

import java.sql.Timestamp;

/**
 * 预警内容实体
 */
public class AlertInfo {
	private int id;						//id
	private Timestamp SheetID;			//记录单id
	private String CrossSectionID;		//断面唯一id
	private int PntType;				//测点类型
	private Timestamp AlertTime;		//预警时间
	private int AlertLeverl;			//预警等级
	private int Utype;					//超限类型
	private double UValue;				//超限数值
	private double UMax;				//超限类型阈值
	private String OriginalDataID;		//原始数据id
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Timestamp getSheetID() {
		return SheetID;
	}
	public void setSheetID(Timestamp sheetID) {
		SheetID = sheetID;
	}
	public String getCrossSectionID() {
		return CrossSectionID;
	}
	public void setCrossSectionID(String crossSectionID) {
		CrossSectionID = crossSectionID;
	}
	public int getPntType() {
		return PntType;
	}
	public void setPntType(int pntType) {
		PntType = pntType;
	}
	public Timestamp getAlertTime() {
		return AlertTime;
	}
	public void setAlertTime(Timestamp alertTime) {
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
