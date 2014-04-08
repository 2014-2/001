package com.crtb.tunnelmonitor.entity;

import java.sql.Timestamp;

/**
 * 断面测量信息
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */

public class TunnelCrossSectionExInfo {
	private int id;
	private String zonecode;		// ZONECODE 工区代码
	private String sitecode;		// SITECODE 工点代码
	private String sectname;		// SECTNAME 断面名
	private String sectcode;		// SECTCODE 断面代码
	private String sectkilo;		// SECTKILO 断面里程值
	private String method;			// METHOD 开挖方法
	private float width;			// 宽度
	private float movevalue_uo;		// MOVEVALUE_U0 uo值
	private Timestamp updateDate;	// UPDATEDATE uo更新时间
	private String remark_uo;		// REMARK_U0 uo备注
	private String holename;		// HOLENAME 工作面名
	private String holestartkilo;	// HOLESTARTKILO 工作面里程
	private String innercode;		// INNERCODES 测点集合
	private Timestamp layDate;		// LAYTIME 埋设时间
	private int upload;				// UPLOAD 上传标识
	private String description;		// DESCRIPTION 备注
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getZonecode() {
		return zonecode;
	}
	public void setZonecode(String zonecode) {
		this.zonecode = zonecode;
	}
	public String getSitecode() {
		return sitecode;
	}
	public void setSitecode(String sitecode) {
		this.sitecode = sitecode;
	}
	public String getSectname() {
		return sectname;
	}
	public void setSectname(String sectname) {
		this.sectname = sectname;
	}
	public String getSectcode() {
		return sectcode;
	}
	public void setSectcode(String sectcode) {
		this.sectcode = sectcode;
	}
	public String getSectkilo() {
		return sectkilo;
	}
	public void setSectkilo(String sectkilo) {
		this.sectkilo = sectkilo;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getMovevalue_uo() {
		return movevalue_uo;
	}
	public void setMovevalue_uo(float movevalue_uo) {
		this.movevalue_uo = movevalue_uo;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getRemark_uo() {
		return remark_uo;
	}
	public void setRemark_uo(String remark_uo) {
		this.remark_uo = remark_uo;
	}
	public String getHolename() {
		return holename;
	}
	public void setHolename(String holename) {
		this.holename = holename;
	}
	public String getHolestartkilo() {
		return holestartkilo;
	}
	public void setHolestartkilo(String holestartkilo) {
		this.holestartkilo = holestartkilo;
	}
	public String getInnercode() {
		return innercode;
	}
	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}
	public Timestamp getLayDate() {
		return layDate;
	}
	public void setLayDate(Timestamp layDate) {
		this.layDate = layDate;
	}
	public int getUpload() {
		return upload;
	}
	public void setUpload(int upload) {
		this.upload = upload;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
