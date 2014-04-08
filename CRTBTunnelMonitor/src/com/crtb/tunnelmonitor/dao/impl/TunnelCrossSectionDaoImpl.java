package com.crtb.tunnelmonitor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.CRTBTunnelMonitor;
import com.crtb.tunnelmonitor.dao.TunnelCrossSectionDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.RecordInfo;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionInfo;
import com.crtb.tunnelmonitor.entity.WorkInfos;
/**
 * 断面数据库实现
 */
public class TunnelCrossSectionDaoImpl implements TunnelCrossSectionDao{

	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;

	public TunnelCrossSectionDaoImpl(Context c,String name) {
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();

	}
	
	@Override
	public List<TunnelCrossSectionInfo> SectionAll() {
		List<TunnelCrossSectionInfo> list = new ArrayList<TunnelCrossSectionInfo>();
		Cursor c = db.rawQuery("select Chainage,ChainagePrefix from TunnelCrossSectionIndex", null);
		while (c.moveToNext()) {
			TunnelCrossSectionInfo s =new TunnelCrossSectionInfo();
			s.setChainage(Double.valueOf(c.getString(c.getColumnIndex("Chainage"))));
			s.setChainagePrefix(c.getString(c.getColumnIndex("ChainagePrefix")));
			list.add(s);
		}
		return list;
	}
	
	public String GetSectionName(TunnelCrossSectionInfo Value) {
		
		String sName = "";
		int iChainage = (int)(Value.getChainage().doubleValue()); 
		int iDiv = 0,iMod = 0;
		iDiv = iChainage / 1000;
		iMod = iChainage % 1000;
		double dMod = (double)iMod+(Value.getChainage().doubleValue()-(double)iChainage);
		sName = Value.getChainagePrefix();
		if (iDiv > 0) {
			sName += Integer.toString(iDiv);
		}
		sName += '+';
		sName += Double.toString(dMod);

		return sName;
	}
	
	public static String GetExcavateMethod(TunnelCrossSectionInfo Value) {
		String sRet = "";
		switch (Value.getExcavateMethod()) {
		case 0:
		{
			sRet = "全断面法";
		}
			break;

		case 1:
		{
			sRet = "双侧壁导坑法";
		}
			break;

		case 2:
		{
			sRet = "台阶法";
		}
			break;

		default:
		{
			sRet = "台阶法";
		}
			break;
		} 
		
		return sRet;
	}
	
	public static int GetExcavateMethodInt(TunnelCrossSectionInfo Value) {
		int iRet = -1;
		switch (Value.getExcavateMethod()) {
		case 0:
		{
			iRet = 1;
		}
			break;

		case 1:
		{
			iRet = 2;
		}
			break;

		case 2:
		{
			iRet = 0;
		}
			break;

		default:
		{
			iRet = 0;
		}
			break;
		} 
		
		return iRet;
	}
	public static int GetExcavateMethodUi(int Value) {
		int iRet = 0;
		switch (Value) {
		case 0:
		{
			iRet = 1;
		}
			break;

		case 1:
		{
			iRet = 2;
		}
			break;

		case 2:
		{
			iRet = 0;
		}
			break;

		default:
		{
		}
			break;
		} 
		
		return iRet;
	}

	@Override
	public void GetTunnelCrossSectionList(List<TunnelCrossSectionInfo> lt)
	{
		if(lt == null)
		{
			lt = new ArrayList<TunnelCrossSectionInfo>();
		}
		else
		{
			lt.clear();
		}
		Cursor c = db.rawQuery("select * from TunnelCrossSectionIndex", null);
		int iIndex = 0;
		String sInfo = "";
		while (c.moveToNext()) {
			TunnelCrossSectionInfo s =new TunnelCrossSectionInfo();
			s.setId(c.getInt(c.getColumnIndex("Id")));
			s.setChainage(Double.valueOf(c.getString(c.getColumnIndex("Chainage"))));
			iIndex = c.getColumnIndex("InbuiltTime");
			sInfo = c.getString(iIndex);
			s.setInBuiltTime(sInfo);
			s.setWidth(Float.valueOf(c.getString(c.getColumnIndex("Width"))));
			s.setExcavateMethod(c.getInt(c.getColumnIndex("ExcavateMethod")));
			s.setSurveyPntName(c.getString(c.getColumnIndex("SurveyPntName")));
			s.setInfo(c.getString(c.getColumnIndex("Info")));
			s.setChainagePrefix(c.getString(c.getColumnIndex("ChainagePrefix")));
//			s.setGDU0(Float.valueOf(c.getString(c.getColumnIndex("GDU0"))));
//			s.setGDVelocity(Float.valueOf(c.getString(c.getColumnIndex("GDVelocity"))));
//			s.setGDU0Time(c.getString(c.getColumnIndex("GDU0Time")));
//			s.setGDU0Description(c.getString(c.getColumnIndex("GDU0Description")));
//			s.setSLU0(Float.valueOf(c.getString(c.getColumnIndex("SLU0"))));
//			s.setSLLimitVelocity(Float.valueOf(c.getString(c.getColumnIndex("SLLimitVelocity"))));
//			s.setSLU0Time(c.getString(c.getColumnIndex("SLU0Time")));
//			s.setSLU0Description(c.getString(c.getColumnIndex("SLU0Description")));
//			s.setLithologic(c.getString(c.getColumnIndex("Lithologic")));
//			s.setLAYVALUE(Float.valueOf(c.getString(c.getColumnIndex("LAYVALUE"))));
//			s.setROCKGRADE(c.getString(c.getColumnIndex("ROCKGRADE")));
			s.setChainageName(GetSectionName(s));
			s.setsExcavateMethod(GetExcavateMethod(s));
			s.setbUse(false);
			
			lt.add(s);
		}
	}
	
	@Override
	public Boolean InsertSection(TunnelCrossSectionInfo s) {
		try {
			db.execSQL(
					"insert into TunnelCrossSectionIndex(Chainage,"
				+ "InbuiltTime,Width,ExcavateMethod, SurveyPntName ,Info ,ChainagePrefix ,"
				+ "GDU0 ,GDVelocity,GDU0Time ,GDU0Description ,SLU0 ,SLLimitVelocity ,"
				+ "SLU0Time ,SLU0Description ,Lithologic ,LAYVALUE ,ROCKGRADE)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] {
					s.getChainage(),s.getInBuiltTime(),s.getWidth(),s.getExcavateMethod(),
					s.getSurveyPntName(),s.getInfo(),s.getChainagePrefix(),s.getGDU0(),
					s.getGDVelocity(),s.getGDU0Time(),s.getGDU0Description(),s.getSLU0(),
					s.getSLLimitVelocity(),s.getSLU0Time(),s.getSLU0Description(),s.getLithologic(),
					s.getLAYVALUE(),s.getROCKGRADE(),
					});
			//db.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	
	
	
	@Override
	public List<TunnelCrossSectionInfo> SectiondibiaoAll() {
		List<TunnelCrossSectionInfo> list = new ArrayList<TunnelCrossSectionInfo>();
		Cursor c = db.rawQuery("select Chainage from SubsidenceCrossSectionIndex", null);
		while (c.moveToNext()) {
			TunnelCrossSectionInfo s =new TunnelCrossSectionInfo();
			s.setChainage(Double.valueOf(c.getString(c.getColumnIndex("Chainage"))));
			list.add(s);
		}
		return list;
	}

//添加隧道内断面，注：试用版本最多添加10个断面	
@Override
public Boolean InsertSectiondibiao(TunnelCrossSectionInfo s) {
		try {
			db.execSQL(
					"insert into SubsidenceCrossSectionIndex(Chainage,"
				+ "InbuiltTime,Width, SurveyPnts ,Info ,ChainagePrefix ,"
				+ "GDU0 ,GDU0Time ,GDU0Description ,SLU0,"
				+ "SLU0Time ,SLU0Description ,Lithologic ,LAYVALUE ,ROCKGRADE)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] {
					s.getChainage(),s.getInBuiltTime(),s.getWidth(),
					s.getSurveyPntName(),s.getInfo(),s.getChainagePrefix(),s.getGDU0(),
					s.getGDU0Time(),s.getGDU0Description(),s.getSLU0(),
					s.getSLU0Time(),s.getSLU0Description(),s.getLithologic(),
					s.getLAYVALUE(),s.getROCKGRADE(),
					});
			//db.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	
	/**查询隧道内断面个数*/
	public int GetTunnelCrossSectionCount(){
		int iResult = 0;
		String sql = "select count(*) as count from TunnelCrossSectionIndex";
		int iIndex = -1;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					iIndex = c.getColumnIndex("count");
					iResult = c.getInt(iIndex);
				}
			}
		} catch (Exception e) {
			iResult = 0;
			System.out.println("GetTunnelCrossSectionCount:"+e.getLocalizedMessage());
		}
		return iResult;
	}
		@Override
	public TunnelCrossSectionInfo SelectSection() {
		return null;
	}

	@Override
	// 0:失败 1：成功 -1：存在记录数据不能删除
	public int DeleteSection(int id) {
		int iResult = 0;
		String sql = "select count(*) as count from RawSheetIndex where CrossSectionType=1" +
				" and ','+CrossSectionIDs+',' like '%," + Integer.toString(id) + ",%";
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					iIndex = c.getColumnIndex("count");
					iIndex = c.getInt(iIndex);
				}
			}
			if(iIndex >0){
				iResult = -1;
				return iResult;
			}
			sql = "delete from TunnelCrossSectionIndex where id = ?";
			Object[] obj = {id};
			db.execSQL(sql,obj);
			iResult = 1;
		} catch (Exception e) {
			// TODO: handle exception
			iResult = 0;
			System.out.println("DeleteSection:" + e.getLocalizedMessage());
		}
		return iResult;
	}

	@Override
	public void UpdateSection(TunnelCrossSectionInfo s) {
		
	}

	@Override
	public TunnelCrossSectionInfo SelectSectiondibiao(TunnelCrossSectionInfo s) {
		return null;
	}

	@Override
	public void DeleteSectiondibiao(Double d) {
		db.execSQL("delete from SubsidenceCrossSectionIndex where Chainage = ?",
				new Object[] { d });
		
	}

	@Override
	public void UpdateSectiondibiao(TunnelCrossSectionInfo s) {
		
	}
}
