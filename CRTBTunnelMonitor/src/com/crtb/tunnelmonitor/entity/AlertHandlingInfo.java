package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 预警日志实体
 */
@Table(TableName="AlertHandlingInfo")
public class AlertHandlingInfo implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;						//id
	
	@ColumnInt
	private int alertID;				//预警id
	
	@ColumnInt
	private int handling;				//处理内容
	
	@ColumnString(length=32)
	private String handlingTime;		//处理时间
	
	@ColumnString(length=16)
	private String duePerson;			//处理人
	
	@ColumnInt
	private int alertStatus;			//预警信息状态
	
	@ColumnInt
	private int handlingInfo;		//备注

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAlertID() {
		return alertID;
	}

	public void setAlertID(int alertID) {
		this.alertID = alertID;
	}

	public int getHandling() {
		return handling;
	}

	public void setHandling(int handling) {
		this.handling = handling;
	}

	public String getHandlingTime() {
		return handlingTime;
	}

	public void setHandlingTime(String handlingTime) {
		this.handlingTime = handlingTime;
	}

	public String getDuePerson() {
		return duePerson;
	}

	public void setDuePerson(String duePerson) {
		this.duePerson = duePerson;
	}

	public int getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(int alertStatus) {
		this.alertStatus = alertStatus;
	}

	public int getHandlingInfo() {
		return handlingInfo;
	}

	public void setHandlingInfo(int handlingInfo) {
		this.handlingInfo = handlingInfo;
	}
	
	
}
