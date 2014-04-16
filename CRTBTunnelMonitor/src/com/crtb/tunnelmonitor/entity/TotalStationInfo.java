package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * 全站仪连接参数信息
 */
@Table(TableName="TotalStationInfo")
public class TotalStationInfo implements Serializable {

    @ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
    @ColumnInt
    private int id;						//id

    @ColumnString(length=32)
    private String name;				//仪器名称

    @ColumnString(length=32)
    private String totalstationType;	//全站仪品牌

    @ColumnInt
    private int baudRate;				//波特率

    @ColumnInt
    private int port;					//端口号

    @ColumnInt
    private int darity;					//奇偶校验

    @ColumnInt
    private int databits;				//数据位

    @ColumnInt
    private int stopbits;				//停止位

    @ColumnString(length=512)
    private String info;				//备注

    @ColumnString(length=32)
    private String cmd;                 //串口

    @ColumnString(length=8)
    private String bUse;

    @ColumnString(length=8)
    private String bCheck;

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

    public String getTotalstationType() {
        return totalstationType;
    }

    public void setTotalstationType(String totalstationType) {
        this.totalstationType = totalstationType;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDarity() {
        return darity;
    }

    public void setDarity(int darity) {
        this.darity = darity;
    }

    public int getDatabits() {
        return databits;
    }

    public void setDatabits(int databits) {
        this.databits = databits;
    }

    public int getStopbits() {
        return stopbits;
    }

    public void setStopbits(int stopbits) {
        this.stopbits = stopbits;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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
}
