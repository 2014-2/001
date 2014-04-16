package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 控制点实体
 */
@Table(TableName = "ControlPointsInfo")
public class ControlPointsInfo implements Serializable {

    @ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
    @ColumnInt
    private int id;				//标识

    @ColumnString(length=32)
    private String name;		//点名

    @ColumnFloat
    private float x;			//北坐标

    @ColumnFloat
    private float y;			//东坐标

    @ColumnFloat
    private float z;			//高程

    @ColumnString(length=512)
    private String Info;		//备注

    @ColumnString(length=8)
    private String 				bUse;

    @ColumnString(length=8)
    private String 				bCheck;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getbUse() {
        return bUse;
    }

    public void setbUse(String bUse) {
        this.bUse = bUse;
    }

    public String getbCheck() {
        return bCheck;
    }

    public void setbCheck(String bCheck) {
        this.bCheck = bCheck;
    }

    public boolean isbCheck() {
        return "true".equals(bCheck);
    }

    public void setbCheck(boolean isChecked) {
        if (isChecked) {
            setbCheck("true");
        } else {
            setbCheck("false");
        }
    }

    public boolean isbUse() {
        return "true".equals(bUse);
    }

    public void setbUse(boolean isUsed) {
        if (isUsed) {
            setbUse("true");
        } else {
            setbUse("false");
        }
    }
}
