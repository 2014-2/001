package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 设站信息实体
 */
@Table(TableName = "StationInfoIndex")
public class StationInfoIndex implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;						// id
	
	@ColumnText
	private String StationPointIndex;	// 测站点id
	
	@ColumnDouble
	private double StationHeight;		// 测站高度值
	
	/*
	 * 一个或多个后视点的ID,大于1个后视点采用逗号分隔
	 */
	@ColumnText
	private String BackSightPointIds;	// 后视点id
	
	@ColumnDouble
	private double BackeSightHeight;	// 后视高度值
	
	@ColumnDate
	private Date CreateTime;			// 设站仪器
	
	@ColumnText
	private String Info;				// 备注
	
	@ColumnText
	private String Guid ;				// guid
	
	private StationInfoIndex(){
		setGuid(CrtbUtils.generatorGUID());
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getStationPointIndex() {
		return StationPointIndex;
	}

	public void setStationPointIndex(String stationPointIndex) {
		StationPointIndex = stationPointIndex;
	}

	public double getStationHeight() {
		return StationHeight;
	}

	public void setStationHeight(double stationHeight) {
		StationHeight = stationHeight;
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public String getBackSightPointIds() {
		return BackSightPointIds;
	}

	public void setBackSightPointIds(String backSightPointIds) {
		BackSightPointIds = backSightPointIds;
	}

	public double getBackeSightHeight() {
		return BackeSightHeight;
	}

	public void setBackeSightHeight(double backeSightHeight) {
		BackeSightHeight = backeSightHeight;
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
}
