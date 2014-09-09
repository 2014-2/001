package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 开挖方法
 * @author zhouwei		: 2014.09.05
 *
 */
@Table(TableName = "TunnelCrossSectionParameter")
public class TunnelCrossSectionParameter implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id;						//id
	
	@ColumnInt
	private int ExcavateMethod ;		// 开挖方法: 自定义从9开始
	
	@ColumnText
	private String Guid ;				// guid
	
	@ColumnInt
	private int CrownPointNumber ;		// 定义模板里面的拱顶点个数；（一般情况下，CD法及CRD法为2，双侧壁导航法为3，但是可以由用户自行进行选择）

	@ColumnInt
	private int SurveyLinePointNumber ; // 测线点对个数：CD法及CRD法一搬情况下是4，也可以是8，12，16；而双侧壁导坑法则为可以2,4,6,8。
	
	@ColumnInt
	private int SurveyLineNumber ;		// 测线条数，如果用户定义了3条测线，则这里为3.
	
	@ColumnText
	private String SurveyLinePointName ;//	测线及测线所对应的点名称序列，字符串类型。数据储存格式为 ：测线名称+”，”+测线起点+“，”+测线终点 ，不同测线之间用“/”进行分割，示列如下：
										// “S1,1,2/S2,3,4/S3,5,6/S4,7,8…”
	
	@ColumnText
	private String MethodName ;			// 存储用户定义的开挖方法的名称。
	
	@ColumnInt
	private int Type ;					// 用来存储用户自定义的模板类型数值。CD法为5，CRD法为6，双侧壁导坑法为7。
	
	
	@ColumnText
	private String Info ;				// 该列目前预留，现在版本值不填信息。
	
	public TunnelCrossSectionParameter(){
		setGuid(CrtbUtils.generatorGUID());
	}

	public int getId() {
		return Id;
	}
	
	public void setId(int id) {
		Id = id;
	}

	public int getExcavateMethod() {
		return ExcavateMethod;
	}

	public void setExcavateMethod(int excavateMethod) {
		ExcavateMethod = excavateMethod;
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public int getCrownPointNumber() {
		return CrownPointNumber;
	}

	public void setCrownPointNumber(int crownPointNumber) {
		CrownPointNumber = crownPointNumber;
	}

	public int getSurveyLinePointNumber() {
		return SurveyLinePointNumber;
	}

	public void setSurveyLinePointNumber(int surveyLinePointNumber) {
		SurveyLinePointNumber = surveyLinePointNumber;
	}

	public int getSurveyLineNumber() {
		return SurveyLineNumber;
	}

	public void setSurveyLineNumber(int surveyLineNumber) {
		SurveyLineNumber = surveyLineNumber;
	}

	public String getSurveyLinePointName() {
		return SurveyLinePointName;
	}

	public void setSurveyLinePointName(String surveyLinePointName) {
		SurveyLinePointName = surveyLinePointName;
	}

	public String getMethodName() {
		return MethodName;
	}

	public void setMethodName(String methodName) {
		MethodName = methodName;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}
}
