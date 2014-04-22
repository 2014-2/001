/**
 * 
 */
package com.crtb.tunnelmonitor;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.IFrameworkFacade;
import org.zw.android.framework.impl.FrameworkConfig;
import org.zw.android.framework.impl.FrameworkFacade;

import android.app.Application;
import android.content.Context;

import com.crtb.tunnelmonitor.dao.impl.v2.CrtbLicenseDao;
import com.crtb.tunnelmonitor.entity.ControlPointsIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.entity.TotalStationIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

public class AppCRTBApplication extends Application {
	
	private static AppCRTBApplication instance;
	
	private Object CurTotalStation = null;//正在使用的全站仪
	
	private String verify;//13位随机码
	private String publickey;//加密公钥
	private List<SurveyerInformation> personList = null;
	private SurveyerInformation CurPerson = null;

	private boolean bLocaUser;
	
	/** framework */ 
	private IFrameworkFacade mFramework ;
	
	public boolean isbLocaUser() {
		return bLocaUser;
	}

	public void setbLocaUser(boolean bLocaUser) {
		this.bLocaUser = bLocaUser;
	}

	public static AppCRTBApplication getInstance() {
	   return instance;
	}

	public static Context getAppContext() {
		Context appContext = null;
		if (instance != null) {
			appContext = instance.getApplicationContext();
		}
		return appContext;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
		
		// init framework. add by wei.zhou 2014.04.08
		FrameworkConfig config = FrameworkConfig.defaultConfig(this) ;
		config.setCachePercent(0.3f);// image cache size
		config.setDatabaseVersion(AppConfig.DB_VERSION); // db version
		FrameworkFacade.create(config);
		mFramework	= FrameworkFacade.getFrameworkFacade() ;
		
		// preferences
		AppPreferences.initCrtbPreferences(this);
		
		// default user
		CrtbLicenseDao.registDefaultUser(this);
	}
	
	public IFrameworkFacade getFrameworkFacade(){
		return mFramework ;
	}
	
	public List<SurveyerInformation> getPersonList() {
		return personList;
	}

	public void setPersonList(List<SurveyerInformation> personList) {
		this.personList = personList;
	}

	public SurveyerInformation getCurPerson() {
		return CurPerson;
	}

	public void setCurPerson(SurveyerInformation curPerson) {
		CurPerson = curPerson;
	}

//	public boolean IsValidWork(WorkInfos Value)
//	{
//		if(Value.getProjectName().trim().length() <= 0)
//		{
//			return false;
//		}
//		if(Value.getConstructionFirm().trim().length() <= 0)
//		{
//			return false;
//		}
//		if(Value.getChainagePrefix().trim().length() <= 0)
//		{
//			return false;
//		}
//		if(Value.getStartChainage() <= 0)
//		{
//			return false;
//		}
//		if(Value.getEndChainage() <= 0)
//		{
//			return false;
//		}
//		if(Value.getEndChainage() <= Value.getStartChainage())
//		{
//			return false;
//		}
//		
//		return true;
//	}
	public boolean IsValidTunnelCrossSectionInfo(TunnelCrossSectionIndex Value)
	{
//		if(Value.getChainage().toString().trim().length() <= 0)
//		{
//			return false;
//		}
//		if(Value.getInBuiltTime().trim().length() <= 0)
//		{
//			return false;
//		}
//		if(Value.getWidth().toString().trim().length() <= 0)
//		{
//			return false;
//		}
		
		return true;
	}
	
	public boolean IsValidSubsidenceTunnelCrossSectionInfo(SubsidenceCrossSectionIndex Value)
	{
//		if(Value.getChainage().toString().trim().length() <= 0)
//		{
//			return false;
//		}
		if(Value.getInbuiltTime() == null)
		{
			return false;
		}
//		if(Value.getWidth().toString().trim().length() <= 0)
//		{
//			return false;
//		}
		if(Value.getSurveyPnts().toString().trim().length() <= 0)
		{
			return false;
		}
		
		return true;
	}
//	public boolean IsValidRecordInfo(RecordInfo Value)
//	{
//		
//		return true;
//	}
	public boolean IsValidTotalStationInfo(TotalStationIndex Value)
	{
		
		return true;
	}
	public boolean IsValidControlPointInfo(ControlPointsIndex Value)
	{
		
		return true;
	}
//	
//	public DTMSDBDaoImpl getDatabase()
//	{
//		return mDaoImpl;
//	}
	
	
//	public void setCurrentWorkingFace(Context c, WorkInfos workingFace)
//	{
//		mCurrentWorkingFace = workingFace;
//		mCurrentWorkingFace.InitData(c);
//	}
//	
//	public WorkInfos getCurrentWorkingFace()
//	{
//		return mCurrentWorkingFace;
//	}
	public void SetCurTotalStation(Object Value)
	{
		CurTotalStation = Value;
	}
	public Object GetCurTotalStation()
	{
		return CurTotalStation;
	}
//	public void SetWorkList(List<WorkInfos> Value)
//	{
//		WorkList = Value;
//	}
//	public List<WorkInfos> GetWorkList()
//	{
//		return WorkList;
//	}


	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	public static int StrToInt(String Value,int idefault)
	{
		int iRet = idefault;
		if(Value.trim().length() > 0)
		{
			iRet = Integer.valueOf(Value.trim()).intValue();
		}
		return iRet;
	}
	public static double StrToDouble(String Value,double ddefault) {
		double dRet = ddefault;
		if(Value.trim().length() > 0)
		{
			dRet = Double.valueOf(Value.trim()).doubleValue();
		}
		return dRet;
	}
//	public String GetSectionName(double Value) {
//		
////		if (mCurrentWorkingFace == null) {
////			return "";			
////		}
////		int iDiv = 0,iMod = 0;
////	    int iChainage = (int)Value;
////		iDiv = iChainage / 1000;
////		iMod = iChainage % 1000;
////		double dMod = (double)iMod+(Value-(double)iChainage);
////
////		String sName = mCurrentWorkingFace.getChainagePrefix();
////		if (iDiv > 0) {
////			sName += Integer.toString(iDiv);
////		}
////		sName += '+';
////		sName += Double.toString(dMod);
////	
////		return sName;
//	}

//	public void UpdateWork(WorkInfos Value)
//	{
//		if(WorkList == null)
//		{
//			return;
//		}
//		for(int i=0;i<WorkList.size();i++)
//		{
//			if(WorkList.get(i).getProjectName().equals(Value.getProjectName()))
//			{
//				WorkList.set(i, Value);
//				break;
//			}
//		}
//	}
//	public void DelWork(WorkInfos Value)
//	{
//		if(WorkList == null)
//		{
//			return;
//		}
//		for(int i=0;i<WorkList.size();i++)
//		{
//			if(WorkList.get(i).getProjectName().equals(Value.getProjectName()))
//			{
//				WorkList.remove(i);
//				break;
//			}
//		}
//	}
	public static List<Integer> GetSectionIDArray(String Value)
	{
		List<Integer> lRet = new ArrayList<Integer>();
		String sFind = Value,sLeft;
		char c = ',';
		int iPos;
		while (sFind.length() > 0) {
			iPos = sFind.indexOf(c);
			if (iPos < 0) {
				lRet.add(Integer.valueOf(sFind));
				break;
			}
			sLeft = sFind.substring(0,iPos);
			if (sLeft.length() > 0) {
				lRet.add(Integer.valueOf(sLeft));
			}
			sFind = sFind.substring(iPos+1, sFind.length());
		}
		
		return lRet;
	}
	public static String GetSectionIDArrayForTunnelCrossArray(List<TunnelCrossSectionIndex> Value)
	{
		String sRet = "";
		if (Value == null) {
			return sRet;
		}
//		List<TunnelCrossSectionInfo> tmpList = new ArrayList<TunnelCrossSectionInfo>();
//		for (int i = 0; i < Value.size(); i++) {
//			if (Value.get(i).isbUse()) {
//				tmpList.add(Value.get(i));
//			}
//		}
//		
//		for (int i = 0; i < tmpList.size(); i++) {
//			sRet += Integer.toString(tmpList.get(i).getId());
//			if (i < (tmpList.size()-1)) {
//				sRet += ',';
//			}
//		}
		
		return sRet;
	}
	public static String GetSectionIDArrayForSubCrossArray(List<SubsidenceCrossSectionIndex> Value)
	{
		String sRet = "";
		if (Value == null) {
			return sRet;
		}
		List<SubsidenceCrossSectionIndex> tmpList = new ArrayList<SubsidenceCrossSectionIndex>();
		for (int i = 0; i < Value.size(); i++) {
//			if (Value.get(i).isbUse()) {
//				tmpList.add(Value.get(i));
//			}
		}
		
//		for (int i = 0; i < tmpList.size(); i++) {
//			sRet += Integer.toString(tmpList.get(i).getId());
//			if (i < (tmpList.size()-1)) {
//				sRet += ',';
//			}
//		}
		
		return sRet;
	}
	public static String GetExcavateMethodPoint(List<String> Value)
	{
		String sRet = "";
		if (Value == null) {
			return sRet;
		}
		for (int i = 0; i < Value.size(); i++) {
			sRet += Value.get(i);
			if (i < (Value.size()-1)) {
				sRet += ',';
			}
		}
		
		return sRet;
	}
	public static List<String> GetExcavateMethodPointArray(String Value)
	{
		List<String> sRet = new ArrayList<String>();
		if (Value == null) {
			sRet.add("");
			sRet.add("");
			sRet.add("");
			sRet.add("");
			return sRet;
		}
		char c = ',';
		int iPos;
		String sFind = Value;
		String sLeft;
		while (sFind.length() > 0) {
			iPos = sFind.indexOf(c);
			if (iPos < 0) {
				break;
			}
			sLeft = sFind.substring(0,iPos);
			if (sLeft.length() > 0) {
				sRet.add(sLeft);
			}
			else {
				sRet.add("");
			}
			sFind = sFind.substring(iPos+1, sFind.length());
		}
		for (int i = sRet.size(); i < 4; i++) {
			sRet.add("");
		}
		
		return sRet;
	}
}
