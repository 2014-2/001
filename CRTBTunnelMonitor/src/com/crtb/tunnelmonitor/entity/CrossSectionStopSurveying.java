package com.crtb.tunnelmonitor.entity;

import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

@Table(TableName = "CrossSectionStopSurveying")
public class CrossSectionStopSurveying {
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id; 						    // id

    @ColumnText
    private String Guid ;                       // 唯一标示
	
    @ColumnText
	private String CrossSectionId; 				// 断面唯一id
    
    @ColumnDouble
    private double CrossSectionChainage; 	    // 断面里程值
    
    @ColumnInt
	private int CrossSectionType; 				// 断面类型：1代表的是隧道内断面；2代表的是地表下沉断面；
    
    public enum CrossSectionEnum{
    	Tunnel,
    	Sub
    }
    
    @ColumnInt
	private int SurveyStatus ; 				    // 断面的测量状态0代表删除，1代表在测状态，2代表停测（封存）状态；
    

    @ColumnText
	private String CustomA; 				    // 自定义A
    
    @ColumnText
	private String CustomB; 				    // 自定义B
    
    @ColumnText
	private String Info; 				        // Info
        
	
	public CrossSectionStopSurveying() {
		setGuid(CrtbUtils.generatorGUID());
		SurveyStatus = 2;
	}


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


	public String getCrossSectionId() {
		return CrossSectionId;
	}


	public void setCrossSectionId(String crossSectionId) {
		CrossSectionId = crossSectionId;
	}


	public double getCrossSectionChainage() {
		return CrossSectionChainage;
	}


	public void setCrossSectionChainage(double crossSectionChainage) {
		CrossSectionChainage = crossSectionChainage;
	}


	public int getCrossSectionType() {
		return CrossSectionType;
	}


	public void setCrossSectionType(CrossSectionEnum type) {
		if(type == CrossSectionEnum.Tunnel){
			CrossSectionType = 1;
		} else if(type == CrossSectionEnum.Sub){
			CrossSectionType = 2;
		}
	}


	public int getSurveyStatus() {
		return SurveyStatus;
	}


	public void setSurveyStatus(int surveyStatus) {
		SurveyStatus = surveyStatus;
	}


	public String getCustomA() {
		return CustomA;
	}


	public void setCustomA(String customA) {
		CustomA = customA;
	}


	public String getCustomB() {
		return CustomB;
	}


	public void setCustomB(String customB) {
		CustomB = customB;
	}


	public String getInfo() {
		return Info;
	}


	public void setInfo(String info) {
		Info = info;
	}
}
