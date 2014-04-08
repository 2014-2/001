package com.crtb.tunnelmonitor.entity;
/**
 * 新建断面隧道
 *创建时间：2014-3-21下午6:16:22
 *@author 张涛
 *@since JDK1.6
 *@version 1.0
 */
public class SectionInfo {

	
	private Double Chainage;
	private String InBuiltTime;
	private Float Width;
	private int ExcavateMethod;
	private String SurveyPntName;
	private String Info;
	private String ChainagePrefix;
	private Float GDU0;
	private Float GDVelocity;
	private String GDU0Time;
	private String GDU0Description;
	private Float SLU0;
	private Float SLLimitVelocity;
	private String SLU0Time;
	private String SLU0Description;
	private String Lithologic;
	private Float LAYVALUE;
	private String ROCKGRADE;
	
	public Double getChainage() {
		return Chainage;
	}
	public void setChainage(Double chainage) {
		Chainage = chainage;
	}
	public String getInBuiltTime() {
		return InBuiltTime;
	}
	public void setInBuiltTime(String inBuiltTime) {
		InBuiltTime = inBuiltTime;
	}
	public Float getWidth() {
		return Width;
	}
	public void setWidth(Float width) {
		Width = width;
	}
	public int getExcavateMethod() {
		return ExcavateMethod;
	}
	public void setExcavateMethod(int excavateMethod) {
		ExcavateMethod = excavateMethod;
	}
	public String getSurveyPntName() {
		return SurveyPntName;
	}
	public void setSurveyPntName(String surveyPntName) {
		SurveyPntName = surveyPntName;
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
	public Float getGDU0() {
		return GDU0;
	}
	public void setGDU0(Float gDU0) {
		GDU0 = gDU0;
	}
	public Float getGDVelocity() {
		return GDVelocity;
	}
	public void setGDVelocity(Float gDVelocity) {
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
	public Float getSLU0() {
		return SLU0;
	}
	public void setSLU0(Float sLU0) {
		SLU0 = sLU0;
	}
	public Float getSLLimitVelocity() {
		return SLLimitVelocity;
	}
	public void setSLLimitVelocity(Float sLLimitVelocity) {
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
	public Float getLAYVALUE() {
		return LAYVALUE;
	}
	public void setLAYVALUE(Float lAYVALUE) {
		LAYVALUE = lAYVALUE;
	}
	public String getROCKGRADE() {
		return ROCKGRADE;
	}
	public void setROCKGRADE(String rOCKGRADE) {
		ROCKGRADE = rOCKGRADE;
	}
	
	
}
