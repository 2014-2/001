package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

@Table(TableName="CrtbUser")
public final class CrtbUser implements Serializable {

    public static final int LICENSE_TYPE_DEFAULT				= 0;
	public static final int LICENSE_TYPE_TRIAL					= 1;
	public static final int LICENSE_TYPE_REGISTERED				= 2;

    public static final String USER_LICENSE_TYPE_STR_DEFAULT                = "00";
    public static final String USER_LICENSE_TYPE_STR_TRIAL                  = "10";
    public static final String USER_LICENSE_TYPE_STR_REGISTERED             = "20";

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id; 					// 标识
	
	@ColumnString(length=255)
	private String username ;			// 用户
	
	@ColumnInt
	private int usertype ;				// 用户类型

    @ColumnInt
    private int versionLowLimit ;              // 授权可用版本下限

    @ColumnInt
    private int versionHighLimit ;              // 授权可用版本上限

	@ColumnString(length=256)
	private String license ; 			// 授权码 

	public CrtbUser(){
		setUsertype(LICENSE_TYPE_DEFAULT);
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

	public int getUsertype() {
		return usertype;
	}

	public int getVersionLowLimit() {
        return versionLowLimit;
    }

    public void setVersionLowLimit(int versionLowLimit) {
        this.versionLowLimit = versionLowLimit;
    }

    public int getVersionHighLimit() {
        return versionHighLimit;
    }

    public void setVersionHighLimit(int versionHighLimit) {
        this.versionHighLimit = versionHighLimit;
    }

    public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}
}
