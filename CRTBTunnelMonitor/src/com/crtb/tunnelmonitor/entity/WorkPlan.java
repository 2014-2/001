package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;

import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

/**
 * Work Plan
 * 
 * @author zhouwei
 * 
 */
@Table(TableName = "WorkPlan")
public class WorkPlan implements Serializable {
	
	public static final int STATUS_IDLE			= 1 ;
	public static final int STATUS_EDIT			= 2 ;

	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int id;

	// /////////////base info////////////////

	@ColumnString(length = 32)
	private String workPlanName;

	@ColumnString(length = 32)
	private String creationTime;

	@ColumnString(length = 128)
	private String constructionOrganization;

	@ColumnString(length = 16)
	private String mileagePrefix;

	@ColumnFloat
	private float startMileage;

	@ColumnFloat
	private float endMileage;

	// /////////////////deflection info /////////////////////

	// vault
	@ColumnFloat
	private float vaultLimitVelocity;

	@ColumnFloat
	private float vaultLimitTotalSettlement;

	// restrain
	@ColumnFloat
	private float restrainLimitVelocity;

	@ColumnFloat
	private float restrainLimitTotalSettlement;

	// surface
	@ColumnFloat
	private float surfaceLimitVelocity;

	@ColumnFloat
	private float surfaceLimitTotalSettlement;

	@ColumnString(length = 1024)
	private String remark;
	
	@ColumnInt
	private int workPalnStatus ;
	
	public WorkPlan(){
		setWorkPalnStatus(STATUS_IDLE);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWorkPlanName() {
		return workPlanName;
	}

	public void setWorkPlanName(String workPlanName) {
		this.workPlanName = workPlanName;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getConstructionOrganization() {
		return constructionOrganization;
	}

	public void setConstructionOrganization(String constructionOrganization) {
		this.constructionOrganization = constructionOrganization;
	}

	public String getMileagePrefix() {
		return mileagePrefix;
	}

	public void setMileagePrefix(String mileagePrefix) {
		this.mileagePrefix = mileagePrefix;
	}

	public float getStartMileage() {
		return startMileage;
	}

	public void setStartMileage(float startMileage) {
		this.startMileage = startMileage;
	}

	public float getEndMileage() {
		return endMileage;
	}

	public void setEndMileage(float endMileage) {
		this.endMileage = endMileage;
	}

	public float getVaultLimitVelocity() {
		return vaultLimitVelocity;
	}

	public void setVaultLimitVelocity(float vaultLimitVelocity) {
		this.vaultLimitVelocity = vaultLimitVelocity;
	}

	public float getVaultLimitTotalSettlement() {
		return vaultLimitTotalSettlement;
	}

	public void setVaultLimitTotalSettlement(float vaultLimitTotalSettlement) {
		this.vaultLimitTotalSettlement = vaultLimitTotalSettlement;
	}

	public float getRestrainLimitVelocity() {
		return restrainLimitVelocity;
	}

	public void setRestrainLimitVelocity(float restrainLimitVelocity) {
		this.restrainLimitVelocity = restrainLimitVelocity;
	}

	public float getRestrainLimitTotalSettlement() {
		return restrainLimitTotalSettlement;
	}

	public void setRestrainLimitTotalSettlement(float restrainLimitTotalSettlement) {
		this.restrainLimitTotalSettlement = restrainLimitTotalSettlement;
	}

	public float getSurfaceLimitVelocity() {
		return surfaceLimitVelocity;
	}

	public void setSurfaceLimitVelocity(float surfaceLimitVelocity) {
		this.surfaceLimitVelocity = surfaceLimitVelocity;
	}

	public float getSurfaceLimitTotalSettlement() {
		return surfaceLimitTotalSettlement;
	}

	public void setSurfaceLimitTotalSettlement(float surfaceLimitTotalSettlement) {
		this.surfaceLimitTotalSettlement = surfaceLimitTotalSettlement;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getWorkPalnStatus() {
		return workPalnStatus;
	}

	public void setWorkPalnStatus(int workPalnStatus) {
		this.workPalnStatus = workPalnStatus;
	}

}
