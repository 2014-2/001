package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnBoolean;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 控制点实体
 */
@Table(TableName = "ControlPointsIndex")
public class ControlPointsIndex implements Serializable {

    @ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
    @ColumnInt
    private int Id;				// 标识

    @ColumnString(length=255)
    private String Name;		// 点名

    @ColumnDouble
    private double x;			// 北坐标

    @ColumnDouble
    private double y;			// 东坐标

    @ColumnDouble
    private double z;			// 高程

    @ColumnText
    private String Info;		// 备注
    
    @ColumnText
    private String Guid ;		// guid

    @ColumnBoolean
    private boolean used; 		// 使用

    @ColumnBoolean
    private boolean checked; 	// 选中

    public ControlPointsIndex(){
    	setGuid(CrtbUtils.generatorGUID());
        setChecked(false);
        setUsed(false);
    }

	public int getId() {
		return Id;
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
