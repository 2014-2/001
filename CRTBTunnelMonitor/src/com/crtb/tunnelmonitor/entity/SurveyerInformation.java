package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 *测量人员信息实体
 */
public class SurveyerInformation implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;
	
	@ColumnString(length=16)
	private String surveyerName;			//测量员名
	
	@ColumnString(length=20)
	private String certificateID;			//身份证id
	
	@ColumnString(length=16)
	private String password;				//密码
	
	@ColumnString(length=512)
	private String info;					//备注
	
	@ColumnInt
	private int projectID;					//工作面id

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSurveyerName() {
		return surveyerName;
	}

	public void setSurveyerName(String surveyerName) {
		this.surveyerName = surveyerName;
	}

	public String getCertificateID() {
		return certificateID;
	}

	public void setCertificateID(String certificateID) {
		this.certificateID = certificateID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	
}
