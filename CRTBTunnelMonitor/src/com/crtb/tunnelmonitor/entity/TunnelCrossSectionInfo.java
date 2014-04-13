package com.crtb.tunnelmonitor.entity;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 隧道内断面隧道
 */
@Table(TableName = "TunnelCrossSectionInfo")
public class TunnelCrossSectionInfo {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;
	
	@ColumnString(length = 64)
	private String prefix; 			// 前缀

	@ColumnFloat
	private float chainage; 		// 断面里程值
	
	@ColumnString(length = 64)
	private String chainageName; 	// 断面名称

	@ColumnString(length = 64)
	private String excavateMethod;  // 施工方法

	@ColumnString(length = 32)
	private String inBuiltTime; 	// 埋设时间

	@ColumnFloat
	private float width; 			// 断面宽度

	@ColumnString(length = 64)
	private String surveyPntName; 	// 测点编号

	@ColumnString(length = 512)
	private String info; 			// 备注

	@ColumnString(length = 16)
	private String chainagePrefix; 	// 里程前缀

	@ColumnFloat
	private float GDU0; 			// 拱顶uo值

	@ColumnFloat
	private float GDVelocity; 		// 拱顶本次下沉速率

	@ColumnString(length = 32)
	private String GDU0Time; 		// 拱顶限差修改时间

	@ColumnString(length = 512)
	private String GDU0Description; // 拱顶极限备注

	@ColumnFloat
	private float SLU0; 			// 收敛uo值

	@ColumnFloat
	private float SLLimitVelocity; 	// 收敛本次下沉速率

	@ColumnString(length = 32)
	private String SLU0Time; 		// 收敛限差修改时间

	@ColumnString(length = 512)
	private String SLU0Description; // 收敛极限备注

	@ColumnString(length = 32)
	private String lithologic; 		// 岩性

	@ColumnFloat
	private float LAYVALUE; 		// 埋深值

	@ColumnString(length = 32)
	private String ROCKGRADE; 		// 围岩级别

	private boolean used;			// 是否使用

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getChainageName() {
		return chainageName;
	}

	public void setChainageName(String chainageName) {
		this.chainageName = chainageName;
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
		return lithologic;
	}

	public void setLithologic(String lithologic) {
		this.lithologic = lithologic;
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
