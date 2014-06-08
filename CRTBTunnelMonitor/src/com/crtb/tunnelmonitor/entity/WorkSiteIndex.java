package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

@Table(TableName="WorkSiteIndex")
public class WorkSiteIndex implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;				// id
	
	@ColumnString(length = 256)
	private String siteName;	// 工点名称
	
	@ColumnString(length = 256)
	private String siteCode;	// 工点编码
	
	@ColumnString(length = 256)
	private String zoneName;	// 工区名称
	
	@ColumnString(length = 256)
	private String zoneCode;	// 工区编码
	
	@ColumnInt
	private int downloadFlag; 	// 下载标识：1 表示未下载，2表示已下载

	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
	
	public int getDownloadFlag() {
		return downloadFlag;
	}

	public void setDownloadFlag(int downloadFlag) {
		this.downloadFlag = downloadFlag;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

}
