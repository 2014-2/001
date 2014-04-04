package com.sxlc.infors;

/*
 * 记录单实体类
 */
public class RecordBase {
	
	public int StationId;// ;设站ID
	public int ChainageId;// ;断面里程ID
	public int SheetId;// 记录单ID
	public String Coordinate;// 测点坐标
	public String PntType;// 测点类型
	public String SurveyTime;// 测量时间
	public String Info;// 备注
	public int MEASNo;// 第几次
	public int SurveyorID;// 测量人员ID
	public int DataStatus;// 异常数据标识
	public float DataCorrection;// 异常数据修正值
	
	public int CrossSectionType;//断面类型
	public String CreateTime;//创表时间
	//String Info;//备注
	public Double FACEDK;//开挖面里程值
	public String FACEDESCRIPTION;//施工工序
	public Double TEMPERATURE;//温度值
	public String CrossSectionIDs;//断面ID序列
	public  int  Width ;//断面宽度
	public  String SurveyPnts = null;//测点编号
	public  String InbuiltTime = null;//埋设时间
	public String ChainagePrefix;//里程前缀
	public String DBU0;//地表U0值
	public String DBU0Description;//拱顶极限备注
	public float Lithologic=5;//地表下层速率
	public float LAYVALUE;//埋深值
	public String ROCKGRADE;//围岩级别
	
}
