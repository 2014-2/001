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
	private double startMileage;

	@ColumnFloat
	private double endMileage;

	// /////////////////deflection info /////////////////////

	// vault
	@ColumnFloat
	private double vaultLimitVelocity;

	@ColumnFloat
	private double vaultLimitTotalSettlement;

	// restrain
	@ColumnFloat
	private double restrainLimitVelocity;

	@ColumnFloat
	private double restrainLimitTotalSettlement;

	// surface
	@ColumnFloat
	private double surfaceLimitVelocity;

	@ColumnFloat
	private double surfaceLimitTotalSettlement;

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

	public double getStartMileage() {
		return startMileage;
	}

	public void setStartMileage(double startMileage) {
		this.startMileage = startMileage;
	}

	public double getEndMileage() {
		return endMileage;
	}

	public void setEndMileage(double endMileage) {
		this.endMileage = endMileage;
	}

	public double getVaultLimitVelocity() {
		return vaultLimitVelocity;
	}

	public void setVaultLimitVelocity(double vaultLimitVelocity) {
		this.vaultLimitVelocity = vaultLimitVelocity;
	}

	public double getVaultLimitTotalSettlement() {
		return vaultLimitTotalSettlement;
	}

	public void setVaultLimitTotalSettlement(double vaultLimitTotalSettlement) {
		this.vaultLimitTotalSettlement = vaultLimitTotalSettlement;
	}

	public double getRestrainLimitVelocity() {
		return restrainLimitVelocity;
	}

	public void setRestrainLimitVelocity(double restrainLimitVelocity) {
		this.restrainLimitVelocity = restrainLimitVelocity;
	}

	public double getRestrainLimitTotalSettlement() {
		return restrainLimitTotalSettlement;
	}

	public void setRestrainLimitTotalSettlement(
			double restrainLimitTotalSettlement) {
		this.restrainLimitTotalSettlement = restrainLimitTotalSettlement;
	}

	public double getSurfaceLimitVelocity() {
		return surfaceLimitVelocity;
	}

	public void setSurfaceLimitVelocity(double surfaceLimitVelocity) {
		this.surfaceLimitVelocity = surfaceLimitVelocity;
	}

	public double getSurfaceLimitTotalSettlement() {
		return surfaceLimitTotalSettlement;
	}

	public void setSurfaceLimitTotalSettlement(
			double surfaceLimitTotalSettlement) {
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
