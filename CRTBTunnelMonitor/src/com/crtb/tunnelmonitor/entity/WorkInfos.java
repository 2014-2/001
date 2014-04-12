package com.crtb.tunnelmonitor.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.crtb.tunnelmonitor.dao.impl.ControlPointsDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.RecordDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.SubsidenceCrossSectionDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.TotalStationDaoImpl;
import com.crtb.tunnelmonitor.dao.impl.TunnelCrossSectionDaoImpl;

/**
 * 创建工作面实体类
 */
public class WorkInfos {

	/** 名称 */
	private String ProjectName;
	/** 时间 */
	private String CreateTime;
	/** 里程前缀 */
	private String ChainagePrefix;
	/** 起始里程 */
	private Double StartChainage;
	/** 结束里程 */
	private Double EndChainage;
	/** 施工单位名称 */
	private String ConstructionFirm;
	
	
	/** 拱顶单次速率限差 */
	private Float GDLimitVelocity;
	/** 拱顶 累计沉降量限差*/
	private Float GDLimitTotalSettlement;
	
	/** 收敛 单次变形速率*/
	private Float SLLimitVelocity;
	/** 收敛 累计变形限差*/
	private Float SLLimitTotalSettlement;
	
	/** 地表下沉 单次速率*/
	private Float DBLimitVelocity;
	/**地表下沉 累计下沉限差*/
	private String DBLimitTotalSettlement;
	
	/** 备注 */
	private String info;
	private String LastOpenTime;

	private List<TunnelCrossSectionInfo> tcsiList = null;
	private List<SubsidenceCrossSectionInfo> scsiList = null;
	private List<RecordInfo> tcsirecordList = null,scsirecordList = null;	
	private List<TotalStationInfo> tsList = null;
	private List<ControlPointsInfo> cpList = null;
	
	public void InitData(Context c) {
		if(tcsiList == null){
			tcsiList = new ArrayList<TunnelCrossSectionInfo>();
		}
		else {
			tcsiList.clear();
		}
		TunnelCrossSectionDaoImpl impl = new TunnelCrossSectionDaoImpl(c,ProjectName);
		impl.GetTunnelCrossSectionList(tcsiList);
		
		if(scsiList == null){
			scsiList = new ArrayList<SubsidenceCrossSectionInfo>();
		}
		else {
			scsiList.clear();
		}
		SubsidenceCrossSectionDaoImpl impl1 = new SubsidenceCrossSectionDaoImpl(c,ProjectName);
		impl1.GetSubsidenceCrossSectionList(scsiList);
		

		if(tcsirecordList == null){
			tcsirecordList = new ArrayList<RecordInfo>();
		}
		else {
			tcsirecordList.clear();
		}
		RecordDaoImpl imp2 = new RecordDaoImpl(c,ProjectName);
		imp2.GetRecordList(1,this, tcsirecordList);
		if(scsirecordList == null){
			scsirecordList = new ArrayList<RecordInfo>();
		}
		else {
			scsirecordList.clear();
		}
		imp2.GetRecordList(2,this, scsirecordList);

		if(tsList == null){
			tsList = new ArrayList<TotalStationInfo>();
		}
		else {
			tsList.clear();
		}
		TotalStationDaoImpl impl3 = new TotalStationDaoImpl(c,ProjectName);
		impl3.GetTotalStationList(tsList);

		if(cpList == null){
			cpList = new ArrayList<ControlPointsInfo>();
		}
		else {
			cpList.clear();
		}
		ControlPointsDaoImpl impl4 = new ControlPointsDaoImpl(c,ProjectName);
		impl4.GetControlPointsList(cpList);
	}
	
	public List<ControlPointsInfo> getCpList() {
		return cpList;
	}
	public void setCpList(List<ControlPointsInfo> cpList) {
		this.cpList = cpList;
	}
	public List<TotalStationInfo> getStaionList() {
		return tsList;
	}
	public void setStationList(List<TotalStationInfo> tsList) {
		this.tsList = tsList;
	}
	public List<RecordInfo> getTcsirecordList() {
		return tcsirecordList;
	}
	public void setTcsirecordList(List<RecordInfo> tcsirecordList) {
		this.tcsirecordList = tcsirecordList;
	}
	public List<RecordInfo> getScsirecordList() {
		return scsirecordList;
	}
	public void setScsirecordList(List<RecordInfo> scsirecordList) {
		this.scsirecordList = scsirecordList;
	}
	public List<SubsidenceCrossSectionInfo> getScsiList() {
		return scsiList;
	}
	public void setScsiList(List<SubsidenceCrossSectionInfo> scsiList) {
		this.scsiList = scsiList;
	}
	public String getLastOpenTime() {
		return LastOpenTime;
	}
	public void setLastOpenTime(String lastOpenTime) {
		LastOpenTime = lastOpenTime;
	}
	/**限差时间*/
	private String LimitedTotalSubsidenceTime;
	
	
	public String getLimitedTotalSubsidenceTime() {
		return LimitedTotalSubsidenceTime;
	}
	public void setLimitedTotalSubsidenceTime(String limitedTotalSubsidenceTime) {
		LimitedTotalSubsidenceTime = limitedTotalSubsidenceTime;
	}
	
	public String getProjectName() {
		return ProjectName;
	}
	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getChainagePrefix() {
		return ChainagePrefix;
	}
	public void setChainagePrefix(String chainagePrefix) {
		ChainagePrefix = chainagePrefix;
	}
	public Double getStartChainage() {
		return StartChainage;
	}
	public void setStartChainage(Double startChainage) {
		StartChainage = startChainage;
	}
	public Double getEndChainage() {
		return EndChainage;
	}
	public void setEndChainage(Double endChainage) {
		EndChainage = endChainage;
	}
	public String getConstructionFirm() {
		return ConstructionFirm;
	}
	public void setConstructionFirm(String constructionFirm) {
		ConstructionFirm = constructionFirm;
	}
	public Float getGDLimitVelocity() {
		return GDLimitVelocity;
	}
	public void setGDLimitVelocity(Float gDLimitVelocity) {
		GDLimitVelocity = gDLimitVelocity;
	}
	public Float getGDLimitTotalSettlement() {
		return GDLimitTotalSettlement;
	}
	public void setGDLimitTotalSettlement(Float gDLimitTotalSettlement) {
		GDLimitTotalSettlement = gDLimitTotalSettlement;
	}
	public Float getSLLimitVelocity() {
		return SLLimitVelocity;
	}
	public void setSLLimitVelocity(Float sLLimitVelocity) {
		SLLimitVelocity = sLLimitVelocity;
	}
	public Float getSLLimitTotalSettlement() {
		return SLLimitTotalSettlement;
	}
	public void setSLLimitTotalSettlement(Float sLLimitTotalSettlement) {
		SLLimitTotalSettlement = sLLimitTotalSettlement;
	}
	public Float getDBLimitVelocity() {
		return DBLimitVelocity;
	}
	public void setDBLimitVelocity(Float dBLimitVelocity) {
		DBLimitVelocity = dBLimitVelocity;
	}
	public String getDBLimitTotalSettlement() {
		return DBLimitTotalSettlement;
	}
	public void setDBLimitTotalSettlement(String dBLimitTotalSettlement) {
		DBLimitTotalSettlement = dBLimitTotalSettlement;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public void SetTunnelCrossSectionInfoList(List<TunnelCrossSectionInfo> Value)
	{
		tcsiList = Value;
	}
	public List<TunnelCrossSectionInfo> GetTunnelCrossSectionInfoList()
	{
		return tcsiList;
	}
	public void UpdateTunnelCrossSectionInfo(TunnelCrossSectionInfo Value)
	{
		if(tcsiList == null)
		{
			return;
		}
		for(int i=0;i<tcsiList.size();i++)
		{
			TunnelCrossSectionInfo tmp = tcsiList.get(i);
			if(tmp.getChainage().equals(Value.getChainage()))
			{
				tcsiList.set(i, Value);
				break;
			}
		}
	}
	public void DelTunnelCrossSectionInfo(TunnelCrossSectionInfo Value)
	{
		if(tcsiList == null)
		{
			return;
		}
		for(int i=0;i<tcsiList.size();i++)
		{
			TunnelCrossSectionInfo tmp = tcsiList.get(i);
			if(tmp.getChainage().equals(Value.getChainage()))
			{
				tcsiList.remove(i);
				break;
			}
		}
	}
	public void UpdateSubsidenceCrossSectionInfo(SubsidenceCrossSectionInfo Value)
	{
		if(scsiList == null)
		{
			return;
		}
		for(int i=0;i<scsiList.size();i++)
		{
			SubsidenceCrossSectionInfo tmp = scsiList.get(i);
			if(tmp.getChainage().equals(Value.getChainage()))
			{
				scsiList.set(i, Value);
				break;
			}
		}
	}
	public void DelSubsidenceCrossSectionInfo(SubsidenceCrossSectionInfo Value)
	{
		if(scsiList == null)
		{
			return;
		}
		for(int i=0;i<scsiList.size();i++)
		{
			SubsidenceCrossSectionInfo tmp = scsiList.get(i);
			if(tmp.getChainage().equals(Value.getChainage()))
			{
				scsiList.remove(i);
				break;
			}
		}
	}
	public void UpdateRecordInfo(int iType, RecordInfo Value)
	{
		List<RecordInfo> tmpList = null;
		if (iType == 1) {
			tmpList = tcsirecordList;
		}
		else {
			tmpList = scsirecordList;
		}
		if(tmpList == null)
		{
			return;
		}
		for(int i=0;i<tmpList.size();i++)
		{
			if(tmpList.get(i).getId() == Value.getId())
			{
				tmpList.set(i, Value);
				break;
			}
		}
	}
	public void UpdateTotalStationInfo(TotalStationInfo Value)
	{
		if(tsList == null)
		{
			return;
		}
		for(int i=0;i<tsList.size();i++)
		{
			if(tsList.get(i).getId() == Value.getId())
			{
				tsList.set(i, Value);
				break;
			}
		}
	}
	public void UpdateContrlPointsInfo(ControlPointsInfo Value)
	{
		if(cpList == null)
		{
			return;
		}
		for(int i=0;i<cpList.size();i++)
		{
			if(cpList.get(i).getId() == Value.getId())
			{
				cpList.set(i, Value);
				break;
			}
		}
	}
}
