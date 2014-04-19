package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.Date;

import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.Table;
import org.zw.android.framework.db.core.ColumnPrimaryKey;
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;

@Table(TableName="CrtbProject")
public final class CrtbProject implements Serializable {
	
	@ColumnPrimaryKey(Type = PrimaryKeyType.AUTO)
	@ColumnInt
	private int Id; 					// 标识
	
	////////////////////////////////////////////////////////
	
	@ColumnString(length=256)
	private String username ;			// 用户
	
	@ColumnString(length=64)
	private String dbName ;				// 数据库名称
	
	@ColumnString(length=256)
	private String dbPath ;				// 数据库路径
	
	///////////////base info///////////////////////////////
	
	@ColumnInt
	private int ProjectId;						// id
	
	@ColumnString(length = 256)
	private String ProjectName;					// 项目名称
	
	@ColumnDate
	private Date CreateTime;					// 创建时间
	
	@ColumnDouble
	private double StartChainage;				// 开始里程
	
	@ColumnDouble
	private double EndChainage;					// 结束里程
	
	@ColumnDate
	private float LastOpenTime;					// 最后打开时间
	
	@ColumnText
	private String Info;							// 备注信息
	
	// /////////////////deflection info /////////////////////
	
	// vault
	@ColumnString(length = 256)
	private String ChainagePrefix;				// 里程前缀				DK
	
	@ColumnFloat
	private float GDLimitVelocity;				// 拱顶单次沉降速率		5
	
	// restrain
	@ColumnFloat
	private float GDLimitTotalSettlement;		// 拱顶累计沉降速率		100
	
	@ColumnFloat
	private float SLLimitVelocity;				// 收敛单次变形速率		5
	
	// surface
	@ColumnFloat
	private float SLLimitTotalSettlement;		// 收敛累计变形差			100
	
	@ColumnFloat
	private float DBLimitVelocity;				// 地表单次下沉速率		5
	
	@ColumnFloat
	private float DBLimitTotalSettlement;		// 地表累计下沉限差		100
	
	@ColumnString(length = 256)
	private String ConstructionFirm ;			// 施工单位
	
	@ColumnDate
	private Date LimitedTotalSubsidenceTime ;  	// 限差时间
	
	public CrtbProject(){
		
	}

	public int getId() {
		return Id;
	}

	public int getProjectId() {
		return ProjectId;
	}

	public void setProjectId(int projectId) {
		ProjectId = projectId;
	}

	public String getProjectName() {
		return ProjectName;
	}

	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}

	public Date getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}

	public double getStartChainage() {
		return StartChainage;
	}

	public void setStartChainage(double startChainage) {
		StartChainage = startChainage;
	}

	public double getEndChainage() {
		return EndChainage;
	}

	public void setEndChainage(double endChainage) {
		EndChainage = endChainage;
	}

	public float getLastOpenTime() {
		return LastOpenTime;
	}

	public void setLastOpenTime(float lastOpenTime) {
		LastOpenTime = lastOpenTime;
	}

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public String getChainagePrefix() {
		return ChainagePrefix;
	}

	public void setChainagePrefix(String chainagePrefix) {
		ChainagePrefix = chainagePrefix;
	}

	public float getGDLimitVelocity() {
		return GDLimitVelocity;
	}

	public void setGDLimitVelocity(float gDLimitVelocity) {
		GDLimitVelocity = gDLimitVelocity;
	}

	public float getGDLimitTotalSettlement() {
		return GDLimitTotalSettlement;
	}

	public void setGDLimitTotalSettlement(float gDLimitTotalSettlement) {
		GDLimitTotalSettlement = gDLimitTotalSettlement;
	}

	public float getSLLimitVelocity() {
		return SLLimitVelocity;
	}

	public void setSLLimitVelocity(float sLLimitVelocity) {
		SLLimitVelocity = sLLimitVelocity;
	}

	public float getSLLimitTotalSettlement() {
		return SLLimitTotalSettlement;
	}

	public void setSLLimitTotalSettlement(float sLLimitTotalSettlement) {
		SLLimitTotalSettlement = sLLimitTotalSettlement;
	}

	public float getDBLimitVelocity() {
		return DBLimitVelocity;
	}

	public void setDBLimitVelocity(float dBLimitVelocity) {
		DBLimitVelocity = dBLimitVelocity;
	}

	public float getDBLimitTotalSettlement() {
		return DBLimitTotalSettlement;
	}

	public void setDBLimitTotalSettlement(float dBLimitTotalSettlement) {
		DBLimitTotalSettlement = dBLimitTotalSettlement;
	}

	public String getConstructionFirm() {
		return ConstructionFirm;
	}

	public void setConstructionFirm(String constructionFirm) {
		ConstructionFirm = constructionFirm;
	}

	public Date getLimitedTotalSubsidenceTime() {
		return LimitedTotalSubsidenceTime;
	}

	public void setLimitedTotalSubsidenceTime(Date limitedTotalSubsidenceTime) {
		LimitedTotalSubsidenceTime = limitedTotalSubsidenceTime;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}
}
