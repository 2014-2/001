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

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 预警内容实体
 */
@Table(TableName = "AlertList")
public class AlertList implements Serializable {

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID; 						// id

    @ColumnText
    private String GUID ;                   // 唯一标示

	@ColumnString(length = 255)
	private String SheetID; 					// 记录单id

	@ColumnInt
	private String CrossSectionID; 			// 断面唯一id

	@ColumnText
	private String PntType; 					// 测点类型

	@ColumnDate
	private Date AlertTime; 				// 预警时间

	@ColumnInt
	private int AlertLeverl; 				// 预警等级

	@ColumnInt
	private int Utype; 						// 超限类型

	@ColumnDouble
	private double UValue; 					// 超限数值

	@ColumnInt
	private int UMax; 					// 超限类型阈值

	@ColumnString(length = 255)
	private String OriginalDataID; 			// 原始数据id

    @ColumnText
    private String Info;

    @ColumnInt
    private int UploadStatus; // 上传类型 , 0表示全部状态；1表示未上传，2表示不上传，3表示部分上传

	public AlertList() {
	    setGUID(CrtbUtils.generatorGUID());
    }

    public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String gUID) {
        GUID = gUID;
    }

    public String getSheetID() {
        return SheetID;
    }

	public void setSheetID(String sheetID) {
		SheetID = sheetID;
	}

	public String getCrossSectionID() {
		return CrossSectionID;
	}

	public void setCrossSectionID(String crossSectionID) {
		CrossSectionID = crossSectionID;
	}

	public String getPntType() {
		return PntType;
	}

	public void setPntType(String pntType) {
		PntType = pntType;
	}

	public Date getAlertTime() {
		return AlertTime;
	}

	public void setAlertTime(Date alertTime) {
		AlertTime = alertTime;
	}

	public int getAlertLeverl() {
		return AlertLeverl;
	}

	public void setAlertLeverl(int alertLeverl) {
		AlertLeverl = alertLeverl;
	}

	public int getUtype() {
		return Utype;
	}

	public void setUtype(int utype) {
		Utype = utype;
	}

	public double getUValue() {
		return UValue;
	}

	public void setUValue(double uValue) {
		UValue = uValue;
	}

	public int getUMax() {
		return UMax;
	}

	public void setUMax(int uMax) {
		UMax = uMax;
	}

	public String getOriginalDataID() {
		return OriginalDataID;
	}

	public void setOriginalDataID(String originalDataID) {
		OriginalDataID = originalDataID;
	}

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public int getUploadStatus() {
        return UploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        UploadStatus = uploadStatus;
    }

}
