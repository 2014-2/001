package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 
 * @author zhouwei
 * 
 */
@Table(TableName = "SectionInfo")
public class SectionInfo implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;

	@ColumnFloat
	private float chainage;

	@ColumnString(length = 32)
	private String inBuiltTime;

	@ColumnFloat
	private float width;

	@ColumnString(length = 16)
	private String excavateMethod;

	@ColumnString(length = 32)
	private String surveyPntName;

	@ColumnString(length = 32)
	private String info;

	@ColumnString(length = 16)
	private String chainagePrefix;

	@ColumnFloat
	private float GDU0;

	@ColumnFloat
	private float GDVelocity;

	@ColumnString(length = 32)
	private String GDU0Time;

	@ColumnString(length = 32)
	private String GDU0Description;

	@ColumnFloat
	private float SLU0;

	@ColumnFloat
	private float SLLimitVelocity;

	@ColumnString(length = 32)
	private String SLU0Time;

	@ColumnString(length = 32)
	private String SLU0Description;

	@ColumnString(length = 32)
	private String Lithologic;

	@ColumnFloat
	private float LAYVALUE;

	@ColumnString(length = 32)
	private String ROCKGRADE;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getChainage() {
		return chainage;
	}

	public void setChainage(float chainage) {
		this.chainage = chainage;
	}

	public String getInBuiltTime() {
		return inBuiltTime;
	}

	public void setInBuiltTime(String inBuiltTime) {
		this.inBuiltTime = inBuiltTime;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public String getExcavateMethod() {
		return excavateMethod;
	}

	public void setExcavateMethod(String excavateMethod) {
		this.excavateMethod = excavateMethod;
	}

	public String getSurveyPntName() {
		return surveyPntName;
	}

	public void setSurveyPntName(String surveyPntName) {
		this.surveyPntName = surveyPntName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getChainagePrefix() {
		return chainagePrefix;
	}

	public void setChainagePrefix(String chainagePrefix) {
		this.chainagePrefix = chainagePrefix;
	}

	public float getGDU0() {
		return GDU0;
	}

	public void setGDU0(float gDU0) {
		GDU0 = gDU0;
	}

	public float getGDVelocity() {
		return GDVelocity;
	}

	public void setGDVelocity(float gDVelocity) {
		GDVelocity = gDVelocity;
	}

	public String getGDU0Time() {
		return GDU0Time;
	}

	public void setGDU0Time(String gDU0Time) {
		GDU0Time = gDU0Time;
	}

	public String getGDU0Description() {
		return GDU0Description;
	}

	public void setGDU0Description(String gDU0Description) {
		GDU0Description = gDU0Description;
	}

	public float getSLU0() {
		return SLU0;
	}

	public void setSLU0(float sLU0) {
		SLU0 = sLU0;
	}

	public float getSLLimitVelocity() {
		return SLLimitVelocity;
	}

	public void setSLLimitVelocity(float sLLimitVelocity) {
		SLLimitVelocity = sLLimitVelocity;
	}

	public String getSLU0Time() {
		return SLU0Time;
	}

	public void setSLU0Time(String sLU0Time) {
		SLU0Time = sLU0Time;
	}

	public String getSLU0Description() {
		return SLU0Description;
	}

	public void setSLU0Description(String sLU0Description) {
		SLU0Description = sLU0Description;
	}

	public String getLithologic() {
		return Lithologic;
	}

	public void setLithologic(String lithologic) {
		Lithologic = lithologic;
	}

	public float getLAYVALUE() {
		return LAYVALUE;
	}

	public void setLAYVALUE(float lAYVALUE) {
		LAYVALUE = lAYVALUE;
	}

	public String getROCKGRADE() {
		return ROCKGRADE;
	}

	public void setROCKGRADE(String rOCKGRADE) {
		ROCKGRADE = rOCKGRADE;
	}

}
