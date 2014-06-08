package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 *测量人员信息实体
 */
@Table(TableName="SurveyerInformation")
public class SurveyerInformation implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;
	
	@ColumnString(length=100)
	private String SurveyerName;			//测量员名
	
	@ColumnString(length=20)
	private String CertificateID;			//身份证id
	
	@ColumnText
	private String Guid ;					// guid
	
	@ColumnString(length=64)
	private String Password;				// 密码
	
	@ColumnText
	private String Info;					// 备注
	
	@ColumnText
	private String ProjectID;				// 记录单guid
	
	public SurveyerInformation(){
		setGuid(CrtbUtils.generatorGUID());
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSurveyerName() {
		return SurveyerName;
	}

	public void setSurveyerName(String surveyerName) {
		SurveyerName = surveyerName;
	}

	public String getCertificateID() {
		return CertificateID;
	}

	public void setCertificateID(String certificateID) {
		CertificateID = certificateID;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public String getProjectID() {
		return ProjectID;
	}

	public void setProjectID(String projectID) {
		ProjectID = projectID;
	}

}
