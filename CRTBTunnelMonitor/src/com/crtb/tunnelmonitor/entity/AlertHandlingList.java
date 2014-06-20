package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnBoolean;
import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 预警日志实体
 */
@Table(TableName="AlertHandlingList")
public class AlertHandlingList implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;						//id

	@ColumnString(length=255)
	private String AlertID;				//预警id

	@ColumnInt
	private int Handling;				//Handling method: 添加修正值...

	@ColumnDate
	private Date HandlingTime;			//处理时间

	@ColumnText
	private String DuePerson;			//处理人
	
	/*
	 *  0表示已处理销警；1表示报警；2表示处理中但未销警
	 */
	@ColumnInt
	private int AlertStatus;			//预警信息状态
	
	/*
	 * true表示（2.1）的处理方式；false表示（2.2）的处理方式下填写“人工处理”
	 */
	@ColumnBoolean
	private boolean HandlingInfo;			// Default 1, not use now.

	@ColumnInt
    private int UploadStatus; // 上传类型 , 0表示全部状态；1表示未上传，2表示已上传，3表示部分上传

	@ColumnText
    private String Info;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getAlertID() {
		return AlertID;
	}

	public void setAlertID(String alertID) {
		AlertID = alertID;
	}

    public int getHandling() {
        return Handling;
    }

    public void setHandling(int handling) {
        Handling = handling;
    }

	public Date getHandlingTime() {
		return HandlingTime;
	}

	public void setHandlingTime(Date handlingTime) {
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

    public boolean isHandlingInfo() {
		return HandlingInfo;
	}

	public void setHandlingInfo(boolean handlingInfo) {
		HandlingInfo = handlingInfo;
	}

	public int getUploadStatus() {
        return UploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        UploadStatus = uploadStatus;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
        setHandling(3);// default 3 for 添加修正值
    }

    public AlertHandlingList() {
        setUploadStatus(1);
    }
}
