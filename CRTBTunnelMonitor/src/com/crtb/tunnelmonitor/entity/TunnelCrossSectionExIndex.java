package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 满足铁科院上传接口要求的断面基础信息表
 * 
 * @author zhouwei
 *
 */
@Table(TableName="TunnelCrossSectionExIndex")
public class TunnelCrossSectionExIndex implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int ID;
	
	@ColumnString(length = 64)
	private String guid ;			// guid ---- 扩展
	
	@ColumnString(length = 64)
	private String sectionGuid ;	// 断面guid ---- 扩展
	
	@ColumnInt
	private int SECT_ID;		    // SECT_ID 断面数据库id
	
	@ColumnString(length=64)
	private String ZONECODE;		// ZONECODE 工区代码
	
	@ColumnString(length=64)
	private String SITECODE;		// SITECODE 工点代码
	
	@ColumnString(length=64)
	private String SECTNAME;		// SECTNAME 断面名
	
	@ColumnString(length=64)
	private String SECTCODE;		// SECTCODE 断面代码
	
	@ColumnString(length=64)
	private String SECTKILO;		// SECTKILO 断面里程值
	
	@ColumnString(length=10)
	private String METHOD;			// METHOD 开挖方法
	
	@ColumnFloat
	private float WIDTH;			// 宽度
	
	@ColumnFloat
	private float MOVEVALUE_U0;		// MOVEVALUE_U0 uo值
	
	@ColumnDate
	private Date UPDATEDATE;		// UPDATEDATE uo更新时间
	
	@ColumnString(length=255)
	private String REMARK_U0;		// REMARK_U0 uo备注
	
	@ColumnString(length=64)
	private String HOLENAME;		// HOLENAME 工作面名
	
	@ColumnString(length=64)
	private String HOLESTARTKILO;	// HOLESTARTKILO 工作面里程
	
	@ColumnText
	private String INNERCODES;		// INNERCODES 测点集合
	
	@ColumnDate
	private Date LAYTIME;			// LAYTIME 埋设时间
	
	@ColumnInt
	private int UPLOAD;				// UPLOAD 上传标识
	
	@ColumnText
	private String DESCRIPTION;		// DESCRIPTION 备注

	public TunnelCrossSectionExIndex(){
		setGuid(CrtbUtils.generatorGUID());
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

	public String getSectionGuid() {
		return sectionGuid;
	}

	public void setSectionGuid(String sectionGuid) {
		this.sectionGuid = sectionGuid;
	}

	public String getZONECODE() {
		return ZONECODE;
	}

	public void setZONECODE(String zONECODE) {
		ZONECODE = zONECODE;
	}

	public String getSITECODE() {
		return SITECODE;
	}

	public void setSITECODE(String sITECODE) {
		SITECODE = sITECODE;
	}

	public String getSECTNAME() {
		return SECTNAME;
	}

	public void setSECTNAME(String sECTNAME) {
		SECTNAME = sECTNAME;
	}

	public String getSECTCODE() {
		return SECTCODE;
	}

	public void setSECTCODE(String sECTCODE) {
		SECTCODE = sECTCODE;
	}

	public String getSECTKILO() {
		return SECTKILO;
	}

	public void setSECTKILO(String sECTKILO) {
		SECTKILO = sECTKILO;
	}

	public String getMETHOD() {
		return METHOD;
	}

	public void setMETHOD(String mETHOD) {
		METHOD = mETHOD;
	}

	public float getWIDTH() {
		return WIDTH;
	}

	public void setWIDTH(float wIDTH) {
		WIDTH = wIDTH;
	}

	public float getMOVEVALUE_U0() {
		return MOVEVALUE_U0;
	}

	public void setMOVEVALUE_U0(float mOVEVALUE_U0) {
		MOVEVALUE_U0 = mOVEVALUE_U0;
	}

	public Date getUPDATEDATE() {
		return UPDATEDATE;
	}

	public void setUPDATEDATE(Date uPDATEDATE) {
		UPDATEDATE = uPDATEDATE;
	}

	public String getREMARK_U0() {
		return REMARK_U0;
	}

	public void setREMARK_U0(String rEMARK_U0) {
		REMARK_U0 = rEMARK_U0;
	}

	public String getHOLENAME() {
		return HOLENAME;
	}

	public void setHOLENAME(String hOLENAME) {
		HOLENAME = hOLENAME;
	}

	public String getHOLESTARTKILO() {
		return HOLESTARTKILO;
	}

	public void setHOLESTARTKILO(String hOLESTARTKILO) {
		HOLESTARTKILO = hOLESTARTKILO;
	}

	public String getINNERCODES() {
		return INNERCODES;
	}

	public void setINNERCODES(String iNNERCODES) {
		INNERCODES = iNNERCODES;
	}

	public Date getLAYTIME() {
		return LAYTIME;
	}

	public void setLAYTIME(Date lAYTIME) {
		LAYTIME = lAYTIME;
	}

	public int getUPLOAD() {
		return UPLOAD;
	}

	public void setUPLOAD(int uPLOAD) {
		UPLOAD = uPLOAD;
	}

	public String getDESCRIPTION() {
		return DESCRIPTION;
	}

	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}

	public int getSECT_ID() {
		return SECT_ID;
	}

	public void setSECT_ID(int sECT_ID) {
		SECT_ID = sECT_ID;
	}
}
