package com.crtb.tunnelmonitor.model;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * Project
 * 
 * @author zhouwei
 *
 */
@Table(TableName="crtb_project")
public class CrtbProject implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id ;
	
	@ColumnString(length=64)
	private String projectName ;
	
	
}
