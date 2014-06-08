package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 每个工程的配置文件
 * 
 * @author zhouwei
 *
 */
@Table(TableName = "ProjectSettingIndex")
public class ProjectSettingIndex implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;

	@ColumnString(length = 255)
	private String ProjectName;

	@ColumnInt
	private int ProjectID;

	@ColumnInt
	private int YMDFormat;		// 0（YYYY-MM-DD）；                     
								// 1（YYYY-M-D）；                
								// 2 (YY-MM-DD）；                
								// 3 (YY-M-D)

	@ColumnInt
	private int HMSFormat;		// 0（HH:MM:SS）；                     
								// 1（HH:MM）；                
								// 2（HH）；

	@ColumnString(length = 255)
	private String ChainagePrefix;

	@ColumnDouble
	private double MaxDeformation;

	@ColumnText
	private String Info;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getProjectName() {
		return ProjectName;
	}

	public int getYMDFormat() {
		return YMDFormat;
	}

	public void setYMDFormat(int yMDFormat) {
		YMDFormat = yMDFormat;
	}

	public int getHMSFormat() {
		return HMSFormat;
	}

	public void setHMSFormat(int hMSFormat) {
		HMSFormat = hMSFormat;
	}

	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}

	public int getProjectID() {
		return ProjectID;
	}

	public void setProjectID(int projectID) {
		ProjectID = projectID;
	}

	public String getChainagePrefix() {
		return ChainagePrefix;
	}

	public void setChainagePrefix(String chainagePrefix) {
		ChainagePrefix = chainagePrefix;
	}

	public double getMaxDeformation() {
		return MaxDeformation;
	}

	public void setMaxDeformation(double maxDeformation) {
		MaxDeformation = maxDeformation;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

}
