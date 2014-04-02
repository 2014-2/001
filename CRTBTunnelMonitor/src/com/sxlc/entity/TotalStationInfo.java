package com.sxlc.entity;

import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 全站仪连接参数信息
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class TotalStationInfo implements Parcelable{
	private int id;						//id
	private String Name;				//仪器名称
	private String TotalstationType;	//全站仪品牌
	private int BaudRate;				//波特率
	private int Port;					//端口号
	private int Parity;					//奇偶校验
	private int Databits;				//数据位
	private int Stopbits;				//停止位
	private String Info;				//备注
	private String Cmd;                 //串口
	private boolean bUse;
	private boolean bCheck;
	
	public boolean isbCheck() {
		return bCheck;
	}
	public void setbCheck(boolean bCheck) {
		this.bCheck = bCheck;
	}
	public boolean isbUse() {
		return bUse;
	}
	public void setbUse(boolean bUse) {
		this.bUse = bUse;
	}
	public String getCmd() {
		return Cmd;
	}
	public void setCmd(String cmd) {
		Cmd = cmd;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getTotalstationType() {
		return TotalstationType;
	}
	public void setTotalstationType(String totalstationType) {
		TotalstationType = totalstationType;
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
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(Name);
		dest.writeString(TotalstationType);
		dest.writeInt(BaudRate);
		dest.writeInt(Port);
		dest.writeInt(Parity);
		dest.writeInt(Databits);
		dest.writeInt(Stopbits);
		dest.writeString(Info);
		dest.writeString(Cmd);
		dest.writeBooleanArray(new boolean[]{bUse,bCheck});
	}
	
    // 实现Parcelable接口的类型中，必须有一个实现了Parcelable.Creator接口的静态常量成员字段，
    // 并且它的名字必须为CREATOR的
    public static final Parcelable.Creator<TotalStationInfo> CREATOR 
            = new Parcelable.Creator<TotalStationInfo>()
    {
        // From Parcelable.Creator
        @Override
        public TotalStationInfo createFromParcel(Parcel in)
        {
        	TotalStationInfo brief = new TotalStationInfo();
            
            // 从包裹中读出数据
        	brief.id = in.readInt();
        	brief.Name = in.readString();
        	brief.TotalstationType = in.readString();
        	brief.BaudRate = in.readInt();
        	brief.Port = in.readInt();
        	brief.Parity = in.readInt();
        	brief.Databits = in.readInt();
        	brief.Stopbits = in.readInt();
        	brief.Info = in.readString();
        	brief.Cmd = in.readString();
        	boolean tmp[] = new boolean[2];
        	in.readBooleanArray(tmp);
   			brief.bUse = tmp[0];
   			brief.bCheck = tmp[1];
            
            return brief;
        }



        // From Parcelable.Creator
        @Override
        public TotalStationInfo[] newArray(int size)
        {
            return new TotalStationInfo[size];
        }
    };
	
}
