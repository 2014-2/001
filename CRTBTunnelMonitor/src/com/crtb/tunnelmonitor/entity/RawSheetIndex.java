package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 隧道内断面记录单和地表下沉记录单的索引
 * 
 * @author zhouwei
 *
 */
@Table(TableName="RawSheetIndex")
public class RawSheetIndex implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;
	
	@ColumnInt
	private int CrossSectionType ;			// 断面类型 : 	1表示隧道内断面
											// 				2表示地表下沉断面
	
	@ColumnDate
	private Date CreateTime ;				// 创建类型
	
	@ColumnText
	private String Info ;					// 备注
	
	@ColumnDouble
	private double FACEDK ;					// 开挖面里程值
	
	@ColumnText
	private String FACEDESCRIPTION ;		// 施工工序
	
	@ColumnDouble
	private double	TEMPERATURE ;			// 温度
	
	@ColumnText
	private String CrossSectionIDs ;		// 断面ID序列

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getCrossSectionType() {
		return CrossSectionType;
	}

	public void setCrossSectionType(int crossSectionType) {
		CrossSectionType = crossSectionType;
	}

	public Date getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public double getFACEDK() {
		return FACEDK;
	}

	public void setFACEDK(double fACEDK) {
		FACEDK = fACEDK;
	}

	public String getFACEDESCRIPTION() {
		return FACEDESCRIPTION;
	}

	public void setFACEDESCRIPTION(String fACEDESCRIPTION) {
		FACEDESCRIPTION = fACEDESCRIPTION;
	}

	public double getTEMPERATURE() {
		return TEMPERATURE;
	}

	public void setTEMPERATURE(double tEMPERATURE) {
		TEMPERATURE = tEMPERATURE;
	}

	public String getCrossSectionIDs() {
		return CrossSectionIDs;
	}

	public void setCrossSectionIDs(String crossSectionIDs) {
		CrossSectionIDs = crossSectionIDs;
	}
}
