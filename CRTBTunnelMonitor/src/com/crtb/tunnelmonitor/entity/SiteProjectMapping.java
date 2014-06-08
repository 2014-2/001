package com.crtb.tunnelmonitor.entity;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

@Table(TableName="SiteProjectMapping")
public class SiteProjectMapping {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;				// id
	
	@ColumnInt
	private int projectId;	    // 工作面id
	
	@ColumnInt
	private int workSiteId;	    // 工点id

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getWorkSiteId() {
		return workSiteId;
	}

	public void setWorkSiteId(int workSiteId) {
		this.workSiteId = workSiteId;
	}
	
}
