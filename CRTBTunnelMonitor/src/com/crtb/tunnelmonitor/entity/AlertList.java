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
	private int Id; 						// id

    @ColumnText
    private String Guid ;                   // 唯一标示

	@ColumnString(length = 255)
	private String SheetId; 					// 记录单id

	@ColumnText
	private String CrossSectionId; 				// 断面唯一id

	@ColumnText
	private String PntType; 					// 测点类型

	@ColumnDate
	private Date AlertTime; 				// 预警时间

	@ColumnInt
	private int AlertLevel; 				// 预警等级

	@ColumnInt
	private int UType; 						// 超限类型

	@ColumnDouble
	private double UValue; 					// 超限数值

	@ColumnInt
	private int UMax; 						// 超限类型阈值

	@ColumnText
	private String OriginalDataId; 			// 原始数据的guid,guid,guid

    @ColumnText
    private String Info;

    @ColumnInt
    private int UploadStatus; // 上传类型 , 0表示全部状态；1表示未上传，2表示不上传，3表示部分上传

	public AlertList() {
	    setGuid(CrtbUtils.generatorGUID());
	    setUploadStatus(1);
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

	public int getAlertLevel() {
		return AlertLevel;
	}

	public void setAlertLevel(int alertLeverl) {
		AlertLevel = alertLeverl;
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

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getGuid() {
		return Guid;
	}

	public void setGuid(String guid) {
		Guid = guid;
	}

	public String getSheetId() {
		return SheetId;
	}

	public void setSheetId(String sheetId) {
		SheetId = sheetId;
	}

	public String getCrossSectionId() {
		return CrossSectionId;
	}

	public void setCrossSectionId(String crossSectionId) {
		CrossSectionId = crossSectionId;
	}

	public int getUType() {
		return UType;
	}

	public void setUType(int uType) {
		UType = uType;
	}

	public String getOriginalDataId() {
		return OriginalDataId;
	}

	public void setOriginalDataId(String originalDataId) {
		OriginalDataId = originalDataId;
	}

}
