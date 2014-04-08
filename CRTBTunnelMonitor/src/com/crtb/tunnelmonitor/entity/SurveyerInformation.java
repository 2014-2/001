package com.crtb.tunnelmonitor.entity;
/**
 *测量人员信息实体
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class SurveyerInformation {
	private int id;
	private String SurveyerName;			//测量员名
	private String CertificateID;			//身份证id
	private String Password;				//密码
	private String Info;					//备注
	private int ProjectID;					//工作面id
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
	public int getProjectID() {
		return ProjectID;
	}
	public void setProjectID(int projectID) {
		ProjectID = projectID;
	}
	
}
