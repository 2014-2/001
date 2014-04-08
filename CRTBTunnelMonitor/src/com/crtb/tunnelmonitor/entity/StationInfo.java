package com.crtb.tunnelmonitor.entity;

import java.sql.Timestamp;

/**
 * 设站信息实体
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class StationInfo {
	private int id;						//id
	private int StationPointId;			// 测站点id
	private double StationHeight;		// 测站高度值
	private String BackSightPointIds;	// 后视点id
	private double BackeSightHeight;	// 后视高度值
	private Timestamp CreateTime;		// 设站仪器
	private String Info;				// 备注
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStationPointId() {
		return StationPointId;
	}
	public void setStationPointId(int stationPointId) {
		StationPointId = stationPointId;
	}
	public double getStationHeight() {
		return StationHeight;
	}
	public void setStationHeight(double stationHeight) {
		StationHeight = stationHeight;
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
	public Timestamp getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Timestamp createTime) {
		CreateTime = createTime;
	}
	public String getInfo() {
		return Info;
	}
	public void setInfo(String info) {
		Info = info;
	}
}
