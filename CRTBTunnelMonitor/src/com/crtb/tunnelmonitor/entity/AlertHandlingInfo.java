package com.crtb.tunnelmonitor.entity;

import java.sql.Timestamp;

/**
 * 预警日志实体
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class AlertHandlingInfo {
	private int id;						//id
	private int AlertID;				//预警id
	private int Handling;				//处理内容
	private Timestamp HandlingTime;		//处理时间
	private String DuePerson;			//处理人
	private int AlertStatus;			//预警信息状态
	private int HandlingInfo;		//备注
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAlertID() {
		return AlertID;
	}
	public void setAlertID(int alertID) {
		AlertID = alertID;
	}
	public int getHandling() {
		return Handling;
	}
	public void setHandling(int handling) {
		Handling = handling;
	}
	public Timestamp getHandlingTime() {
		return HandlingTime;
	}
	public void setHandlingTime(Timestamp handlingTime) {
		HandlingTime = handlingTime;
	}
	public String getDuePerson() {
		return DuePerson;
	}
	public void setDuePerson(String duePerson) {
		DuePerson = duePerson;
	}
	public int getAlertStatus() {
		return AlertStatus;
	}
	public void setAlertStatus(int alertStatus) {
		AlertStatus = alertStatus;
	}
	public int getHandlingInfo() {
		return HandlingInfo;
	}
	public void setHandlingInfo(int handlingInfo) {
		HandlingInfo = handlingInfo;
	}
	
}
