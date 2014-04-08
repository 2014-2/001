package com.crtb.tunnelmonitor.entity;
/**
 * 隧道内断面隧道
 */
public class TunnelCrossSectionInfo {

	private String ChainageName;		//断面名称
	private String sExcavateMethod;			//施工方法
	private int id;						//索引
	private Double Chainage;			//断面里程值
	private String InBuiltTime;			//埋设时间
	private Float Width;				//断面宽度
	private int ExcavateMethod;			//施工方法
	private String SurveyPntName;		//测点编号
	private String Info;				//备注
	private String ChainagePrefix;		//里程前缀
	private Float GDU0;					//拱顶uo值
	private Float GDVelocity;			//拱顶本次下沉速率
	private String GDU0Time;			//拱顶限差修改时间
	private String GDU0Description;		//拱顶极限备注
	private Float SLU0;					//收敛uo值
	private Float SLLimitVelocity;		//收敛本次下沉速率
	private String SLU0Time;			//收敛限差修改时间
	private String SLU0Description;		//收敛极限备注
	private String Lithologic;			//岩性
	private Float LAYVALUE;				//埋深值
	private String ROCKGRADE;			//围岩级别
	private boolean bUse = false;
	public boolean isbUse() {
		return bUse;
	}
	public void setbUse(boolean bUse) {
		this.bUse = bUse;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String getChainageName() {
		return ChainageName;
	}
	public void setChainageName(String chainageName) {
		ChainageName = chainageName;
	}
	public String getsExcavateMethod() {
		return sExcavateMethod;
	}
	public void setsExcavateMethod(String sExcavateMethod) {
		this.sExcavateMethod = sExcavateMethod;
	}
	
}
