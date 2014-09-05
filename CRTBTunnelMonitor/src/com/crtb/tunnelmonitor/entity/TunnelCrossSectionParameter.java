
package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

import android.text.TextUtils;

public class TunnelCrossSectionParameter implements Serializable {
  /*Id	ExcavateMethod	Guid	CrownPointNumber	SurveyLinePointNumber	SurveyLineNumber	SurveyLinePointName	MethodName	Type	Info
	INTEGER	INTEGER	varchar(64)	INTEGER	INTEGER	INTEGER	TEXT	TEXT	INTEGER	TEXT*/
	

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id; 						// id

    @ColumnText
    private String Guid ;                   // 唯一标示

    @ColumnInt
	private int ExcavateMethod; 					// 开挖方法

    @ColumnInt
	private int CrownPointNumber; 				// 拱顶个数

    @ColumnInt
	private int SurveyLinePointNumber; 			// 测线点对个数

    @ColumnInt
	private int SurveyLineNumber; 				// 测线条数

	@ColumnText
	private String SurveyLinePointName; 			// 测线及测线所对应的点名称序列
                                                    // 格式：S1,1,2/S2,3,4/S3,5,6/S4,7,8

	@ColumnText
	private String MethodName; 					//方法名 
	
	@ColumnInt
	private int Type; 						// 超限类型阈值

    @ColumnText
    private String Info;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public int getExcavateMethod() {
		return ExcavateMethod;
	}

	public void setExcavateMethod(int excavateMethod) {
		ExcavateMethod = excavateMethod;
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

	public void MethodName(String methodName) {
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
