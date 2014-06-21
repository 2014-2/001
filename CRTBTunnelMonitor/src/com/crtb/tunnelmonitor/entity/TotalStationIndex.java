package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.zw.android.framework.db.ColumnBoolean;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.common.Constant;

/**
 * 全站仪连接参数信息
 */
@Table(TableName="TotalStationIndex")
public class TotalStationIndex implements Serializable {

    @ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
    @ColumnInt
    private int ID;						//id

    @ColumnString(length=255)
    private String Name;				//仪器名称

    @ColumnText
    private String TotalstationType;	//全站仪品牌

    @ColumnInt
    private int BaudRate;				//波特率

    @ColumnInt
    private int Port;					//端口号

    @ColumnInt
    private int Parity;					//奇偶校验

    @ColumnInt
    private int Databits;				//数据位

    @ColumnInt
    private int Stopbits;				//停止位

    @ColumnText
    private String Info;				//备注

//    @ColumnString(length=32)
//    private String cmd;                 //串口

//    @ColumnBoolean
//    private boolean used;				// 是否选中

//    @ColumnBoolean
//    private boolean checked;				// 是否使用

    public TotalStationIndex(){
//    	setUsed(false);
//    	setChecked(false);
        setBaudRate(9600);
        setDatabits(8);
        setStopbits(1);
    }

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

    public String getTotalstationType() {
        Set set = Constant.TotalStationIndex.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(TotalstationType)) {
                return (String) entry.getKey();
            }
        }
        return TotalstationType;
    }

	public void setTotalstationType(String totalstationType) {
		TotalstationType = (String) Constant.TotalStationIndex.get(totalstationType);
	}

	public int getBaudRate() {
		return BaudRate;
	}

	public void setBaudRate(int baudRate) {
		BaudRate = baudRate;
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		Port = port;
	}

	public int getParity() {
		return Parity;
	}

	public void setParity(int parity) {
		Parity = parity;
	}

	public int getDatabits() {
		return Databits;
	}

	public void setDatabits(int databits) {
		Databits = databits;
	}

	public int getStopbits() {
		return Stopbits;
	}

	public void setStopbits(int stopbits) {
		Stopbits = stopbits;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

//	public String getCmd() {
//		return cmd;
//	}
//
//	public void setCmd(String cmd) {
//		this.cmd = cmd;
//	}
//
//	public boolean isUsed() {
//		return used;
//	}
//
//	public void setUsed(boolean used) {
//		this.used = used;
//	}
//
//	public boolean isChecked() {
//		return checked;
//	}
//
//	public void setChecked(boolean checked) {
//		this.checked = checked;
//	}
}
