package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 地表下沉断面
 * 
 * @author zhouwei
 *
 */
@Table(TableName="SubsidenceCrossSectionIndex")
public class SubsidenceCrossSectionIndex implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;						//记录id
	
	@ColumnDouble
	private double Chainage; 			// 断面里程值
	
	@ColumnDate
	private Date InbuiltTime;			// 埋设时间
	
	@ColumnInt
	private int Width;					// 断面宽度
	
	@ColumnString(length=255)
	private String sectionName ;		// 断面名称
	
	@ColumnString(length = 255)
	private String SurveyPnts;			// 测点编号
	
	@ColumnText
	private String Info;				// 备注
	
	@ColumnString(length=255)
	private String ChainagePrefix;		// 前缀
	
	@ColumnFloat
	private float DBU0;					//地表uo值
	
	@ColumnDate
	private Date DBU0Time;				// 限差修改时间
	
	@ColumnText
	private String DBU0Description;		// 拱顶极限备注
	
	@ColumnFloat
	private float DBLimitVelocity;		// 地表下沉速率
	
	@ColumnString(length = 255)
	private String Lithologic;			// 岩性
	
	@ColumnFloat
	private float LAYVALUE;				// 埋深值
	
	@ColumnString(length = 255)
	private String ROCKGRADE;			// 围岩级别
	
	private boolean used;				// 是否使用 ---------扩展
	
	private boolean hasTestData;		// 是否存在测量数据----扩展

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public double getChainage() {
		return Chainage;
	}

	public boolean isHasTestData() {
		return hasTestData;
	}

	public void setHasTestData(boolean hasTestData) {
		this.hasTestData = hasTestData;
	}

	public void setChainage(double chainage) {
		Chainage = chainage;
	}

	public Date getInbuiltTime() {
		return InbuiltTime;
	}

	public void setInbuiltTime(Date inbuiltTime) {
		InbuiltTime = inbuiltTime;
	}

	public int getWidth() {
		return Width;
	}

	public void setWidth(int width) {
		Width = width;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSurveyPnts() {
		return SurveyPnts;
	}

	public void setSurveyPnts(String surveyPnts) {
		SurveyPnts = surveyPnts;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public String getChainagePrefix() {
		return ChainagePrefix;
	}

	public void setChainagePrefix(String chainagePrefix) {
		ChainagePrefix = chainagePrefix;
	}

	public float getDBU0() {
		return DBU0;
	}

	public void setDBU0(float dBU0) {
		DBU0 = dBU0;
	}

	public Date getDBU0Time() {
		return DBU0Time;
	}

	public void setDBU0Time(Date dBU0Time) {
		DBU0Time = dBU0Time;
	}

	public String getDBU0Description() {
		return DBU0Description;
	}

	public void setDBU0Description(String dBU0Description) {
		DBU0Description = dBU0Description;
	}

	public float getDBLimitVelocity() {
		return DBLimitVelocity;
	}

	public void setDBLimitVelocity(float dBLimitVelocity) {
		DBLimitVelocity = dBLimitVelocity;
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

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
}
