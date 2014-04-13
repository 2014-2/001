package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 地表下沉测量单信息
 */
@Table(TableName="RecordSubsidenceInfo")
public class RecordSubsidenceInfo implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;									//记录id
	
	@ColumnString(length = 16)
	private String prefix; 							// 前缀
	
	@ColumnFloat
	private float facedk;							// 撑子面距离
	
	@ColumnString(length = 32)
	private String chainageName;					//断面名称
	
	@ColumnString(length = 32)
	private String measure ;						// 测量员
	
	@ColumnString(length = 20)
	private String identityCard;					// 身份证
	
	@ColumnString(length = 8)
	private String temperature;						// 温度值
	
	@ColumnString(length = 20)
	private String facedescription;					// 施工序号
	
	@ColumnString(length = 20)
	private String createTime;						// 创建时间
	
	@ColumnString(length = 20)
	private String sectionID;						// 断面id序列
	
	@ColumnInt
	private int testStatus ;						// 测量状态(是否选中)
	
	private boolean used ;

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

	public float getFacedk() {
		return facedk;
	}

	public int getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(int testStatus) {
		this.testStatus = testStatus;
	}

	public void setFacedk(float facedk) {
		this.facedk = facedk;
	}

	public String getChainageName() {
		return chainageName;
	}

	public void setChainageName(String chainageName) {
		this.chainageName = chainageName;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getFacedescription() {
		return facedescription;
	}

	public void setFacedescription(String facedescription) {
		this.facedescription = facedescription;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getSectionID() {
		return sectionID;
	}

	public void setSectionID(String sectionID) {
		this.sectionID = sectionID;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	
}
