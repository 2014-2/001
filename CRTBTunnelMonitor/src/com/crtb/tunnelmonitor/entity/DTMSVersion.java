package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

@Table(TableName = "DTMSVersion")
public class DTMSVersion implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id; 					// 标识
	
	@ColumnString(length=255)
	private String AppVer;				// 
	
	@ColumnInt
	private int DBVer ;					// 
	
	@ColumnText
	private String Info ;				//

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getAppVer() {
		return AppVer;
	}

	public void setAppVer(String appVer) {
		AppVer = appVer;
	}

	public int getDBVer() {
		return DBVer;
	}

	public void setDBVer(int dBVer) {
		DBVer = dBVer;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}
}
