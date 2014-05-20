package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnBoolean;
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
 * 隧道内断面记录单和地表下沉记录单的索引
 * 
 * @author zhouwei
 *
 */
@Table(TableName="RawSheetIndex")
public class RawSheetIndex implements Serializable {
	
	public static final int CROSS_SECTION_TYPE_TUNNEL			= 1 ;
	public static final int CROSS_SECTION_TYPE_SUBSIDENCES		= 2 ;
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;
	
	@ColumnString(length = 64)
	private String guid ;					// 唯一标示 -----------扩展
	
	@ColumnInt
	private int CrossSectionType ;			// 断面类型 : 	1表示隧道内断面
											// 				2表示地表下沉断面
	
	@ColumnDate
	private Date CreateTime ;				// 创建类型
	
	@ColumnText
	private String Info ;					// 备注
	
	@ColumnDouble
	private double FACEDK ;					// 开挖面里程值
	
	@ColumnString(length=64)
	private String prefix ;					// 前缀  ------扩展
	
	@ColumnText
	private String FACEDESCRIPTION ;		// 施工工序
	
	@ColumnString(length=100)
	private String Surveyer ;				// 测量人员  ------扩展
	
	@ColumnString(length=20)
	private String CertificateID;			// 测量人员id------扩展
	
	@ColumnDouble
	private double	TEMPERATURE ;			// 温度
	
	@ColumnText
	private String CrossSectionIDs ;		// 断面ID序列: 断面ID以逗号分隔
											// TunnelCrossSectionIndex
											// SubsidenceCrossSectionIndex

	@ColumnBoolean
	private boolean checked ;				// 是否选择
	
	public RawSheetIndex(){
		setGuid(CrtbUtils.generatorGUID());
		setInfo(getGuid());
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getSurveyer() {
		return Surveyer;
	}

	public void setSurveyer(String surveyer) {
		Surveyer = surveyer;
	}

	public String getCertificateID() {
		return CertificateID;
	}

	public void setCertificateID(String certificateID) {
		CertificateID = certificateID;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getCrossSectionType() {
		return CrossSectionType;
	}

	public void setCrossSectionType(int crossSectionType) {
		CrossSectionType = crossSectionType;
	}

	public Date getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public double getFACEDK() {
		return FACEDK;
	}

	public void setFACEDK(double fACEDK) {
		FACEDK = fACEDK;
	}

	public String getFACEDESCRIPTION() {
		return FACEDESCRIPTION;
	}

	public void setFACEDESCRIPTION(String fACEDESCRIPTION) {
		FACEDESCRIPTION = fACEDESCRIPTION;
	}

	public double getTEMPERATURE() {
		return TEMPERATURE;
	}

	public void setTEMPERATURE(double tEMPERATURE) {
		TEMPERATURE = tEMPERATURE;
	}

	public String getCrossSectionIDs() {
		return CrossSectionIDs;
	}

	public void setCrossSectionIDs(String crossSectionIDs) {
		CrossSectionIDs = crossSectionIDs;
	}
}
