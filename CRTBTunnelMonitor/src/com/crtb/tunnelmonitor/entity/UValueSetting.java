package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 该表格存储的超限的阈值的表格
 * 
 * @author zhouwei
 *
 */
@Table(TableName="UValueSetting")
public class UValueSetting implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;				// id
	
	
}
