package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

@Table(TableName="CrtbProject")
public final class CrtbProject implements Serializable {
	
	public static final int CRTB_DB_STATUS_IDLE		= 1 ;
	public static final int CRTB_DB_STATUS_EDIT		= 2 ;

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id; 					// 标识
	
	@ColumnString(length=256)
	private String username ;			// 用户
	
	@ColumnString(length=64)
	private String dbName ;				// 数据库名称
	
	@ColumnString(length=256)
	private String dbPath ;				// 数据库路径
	
	@ColumnInt
	private int dbStatus ;				// 数据库状态
	
	public CrtbProject(){
		setDbStatus(CRTB_DB_STATUS_IDLE);
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public int getDbStatus() {
		return dbStatus;
	}

	public void setDbStatus(int dbStatus) {
		this.dbStatus = dbStatus;
	}
}
