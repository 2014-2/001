package com.crtb.tunnelmonitor.entity;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 设站信息实体
 */
@Table(TableName = "StationInfo")
public class StationInfo {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;						//id
	
	@ColumnInt
	private int stationPointId;			// 测站点id
	
	@ColumnFloat
	private float stationHeight;		// 测站高度值
	
	@ColumnString(length=32)
	private String backSightPointIds;	// 后视点id
	
	@ColumnFloat
	private float backeSightHeight;		// 后视高度值
	
	@ColumnString(length=32)
	private String createTime;			// 设站仪器
	
	@ColumnString(length=512)
	private String info;				// 备注

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStationPointId() {
		return stationPointId;
	}

	public void setStationPointId(int stationPointId) {
		this.stationPointId = stationPointId;
	}

	public float getStationHeight() {
		return stationHeight;
	}

	public void setStationHeight(float stationHeight) {
		this.stationHeight = stationHeight;
	}

	public String getBackSightPointIds() {
		return backSightPointIds;
	}

	public void setBackSightPointIds(String backSightPointIds) {
		this.backSightPointIds = backSightPointIds;
	}

	public float getBackeSightHeight() {
		return backeSightHeight;
	}

	public void setBackeSightHeight(float backeSightHeight) {
		this.backeSightHeight = backeSightHeight;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
