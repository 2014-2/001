package com.crtb.tunnelmonitor.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 控制点实体
 */
public class ControlPointsInfo implements Parcelable{
	private int id;				//标识
	private String Name;		//点名
	private double x;			//北坐标
	private double y;			//东坐标
	private double z;			//高程
	private String Info;		//备注
	private boolean bUse;
	private boolean bCheck;
	
	public boolean isbUse() {
		return bUse;
	}
	public void setbUse(boolean bUse) {
		this.bUse = bUse;
	}
	public boolean isbCheck() {
		return bCheck;
	}
	public void setbCheck(boolean bCheck) {
		this.bCheck = bCheck;
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
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeDouble(z);
		dest.writeString(Info);
		dest.writeBooleanArray(new boolean[]{bUse,bCheck});
	}
    // 实现Parcelable接口的类型中，必须有一个实现了Parcelable.Creator接口的静态常量成员字段，
    // 并且它的名字必须为CREATOR的
    public static final Parcelable.Creator<ControlPointsInfo> CREATOR 
            = new Parcelable.Creator<ControlPointsInfo>()
    {
        // From Parcelable.Creator
        @Override
        public ControlPointsInfo createFromParcel(Parcel in)
        {
        	ControlPointsInfo brief = new ControlPointsInfo();
            
            // 从包裹中读出数据
        	brief.id = in.readInt();
        	brief.Name = in.readString();
        	brief.x = in.readDouble();
        	brief.y = in.readDouble();
        	brief.z = in.readDouble();
        	brief.Info = in.readString();
        	boolean tmp[] = new boolean[2];
        	in.readBooleanArray(tmp);
   			brief.bUse = tmp[0];
   			brief.bCheck = tmp[1];
            
            return brief;
        }



        // From Parcelable.Creator
        @Override
        public ControlPointsInfo[] newArray(int size)
        {
            return new ControlPointsInfo[size];
        }
    };
}
