package com.crtb.tunnelmonitor.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.dao.SubsidenceCrossSectionDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionInfo;
/**
 * 地表下沉断面数据库实现
 */
public class SubsidenceCrossSectionDaoImpl implements SubsidenceCrossSectionDao{
	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;
	
	public SubsidenceCrossSectionDaoImpl(Context c,String name){
		helper = new SqliteHelperDTMS(c, null,0);
		db = helper.getReadableDatabase();
	}
	
	@Override
	//查找所有的地表下沉断面
	public List<SubsidenceCrossSectionInfo> SelectAllSection() {
		List<SubsidenceCrossSectionInfo> list = null;
		String sql = "select * from SubsidenceCrossSectionIndex";
		SubsidenceCrossSectionInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			while(c.moveToNext()){
				entity = new SubsidenceCrossSectionInfo();
				iIndex = c.getColumnIndex("ID");
				entity.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("Chainage");
//				entity.setChainage(c.getDouble(iIndex));
				iIndex = c.getColumnIndex("InbuiltTime");
//				entity.setInbuiltTime(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("Width");
				entity.setWidth(c.getInt(iIndex));
				iIndex = c.getColumnIndex("SurveyPnts");
				entity.setSurveyPnts(c.getString(iIndex));
				iIndex = c.getColumnIndex("Info");
				entity.setInfo(c.getString(iIndex));
				iIndex = c.getColumnIndex("ChainagePrefix");
//				entity.setChainagePrefix(c.getString(iIndex));
				iIndex = c.getColumnIndex("DBU0");
				entity.setDBU0(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("DBU0Time");
//				entity.setDBU0Time(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("DBU0Description");
				entity.setDBU0Description(c.getString(iIndex));
				iIndex = c.getColumnIndex("DBLimitVelocity");
				entity.setDBLimitVelocity(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("Lithologic");
				entity.setLithologic(c.getString(iIndex));
				iIndex = c.getColumnIndex("LAYVALUE");
				entity.setLayvalue(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("ROCKGRADE");
				entity.setRockgrade(c.getString(iIndex));
				if(list == null){
					list = new ArrayList<SubsidenceCrossSectionInfo>();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAllSection:"+e.getLocalizedMessage());
		}
		
		return list;
	}

	@Override
	//添加地表下沉断面
	public Boolean InsertSubsidenceCrossSection(SubsidenceCrossSectionInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String sql = "insert into SubsidenceCrossSectionIndex(Chainage,"
			+ "InbuiltTime,Width, SurveyPnts ,Info ,ChainagePrefix ,"
			+ "DBU0 ,DBU0Time ,DBU0Description ,DBLimitVelocity,"
			+ "Lithologic ,LAYVALUE ,ROCKGRADE)" +
			" values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			Object[] obj = new Object[13];
			obj[0] = s.getChainage();
			obj[1] = s.getInbuiltTime();
			obj[2] = s.getWidth();
			obj[3] = s.getSurveyPnts();
			obj[4] = s.getInfo();
//			obj[5] = s.getChainagePrefix();
			obj[6] = s.getDBU0();
			obj[7] = s.getDBU0Time();
			obj[8] = s.getDBU0Description();
			obj[9] = s.getDBLimitVelocity();
			obj[10] = s.getLithologic();
			obj[11] = s.getLayvalue();
			obj[12] = s.getRockgrade();
			
			db.execSQL(sql, obj);
			result = true;
			obj = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertSubsidenceCrossSection:" + e.getLocalizedMessage());
		}
		return result;
	}

	@Override
	//修改地表下沉断面
	public SubsidenceCrossSectionInfo SelectSubsidenceCrossSection(int id) {
		SubsidenceCrossSectionInfo entity = null;
		String sql = "select * from SubsidenceCrossSectionIndex where ID=" + Integer.toString(id);
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			while(c.moveToNext()){
				if(entity == null){
					entity = new SubsidenceCrossSectionInfo();
				}
				iIndex = c.getColumnIndex("ID");
				entity.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("Chainage");
//				entity.setChainage(c.getDouble(iIndex));
				iIndex = c.getColumnIndex("InbuiltTime");
//				entity.setInbuiltTime(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("Width");
				entity.setWidth(c.getInt(iIndex));
				iIndex = c.getColumnIndex("SurveyPnts");
				entity.setSurveyPnts(c.getString(iIndex));
				iIndex = c.getColumnIndex("Info");
				entity.setInfo(c.getString(iIndex));
				iIndex = c.getColumnIndex("ChainagePrefix");
//				entity.setChainagePrefix(c.getString(iIndex));
				iIndex = c.getColumnIndex("DBU0");
				entity.setDBU0(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("DBU0Time");
//				entity.setDBU0Time(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("DBU0Description");
				entity.setDBU0Description(c.getString(iIndex));
				iIndex = c.getColumnIndex("DBLimitVelocity");
				entity.setDBLimitVelocity(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("Lithologic");
				entity.setLithologic(c.getString(iIndex));
				iIndex = c.getColumnIndex("LAYVALUE");
				entity.setLayvalue(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("ROCKGRADE");
				entity.setRockgrade(c.getString(iIndex));
			}
		} catch (Exception e) {
			System.out.println("SelectSubsidenceCrossSection:" + e.getLocalizedMessage());
		}
		
		return entity;
	}

	@Override
	//删除地表下沉断面
	// 0:失败 1：成功 -1：存在测量数据不能删除
	public int DeleteSubsidenceCrossSection(int id) {
		int iResult = 0;
		String sql = "select count(*) as count from RawSheetIndex where CrossSectionType=2" +
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
			sql = "delete from SubsidenceCrossSectionIndex where id =?" ;
			Object[] obj = {id};
			db.execSQL(sql,obj);
			iResult = 1;
		} catch (Exception e) {
			iResult = 0;
			System.out.println("DeleteSection:" + e.getLocalizedMessage());
		}
		return iResult;
//		String sql = "delete from SubsidenceCrossSectionIndex where id =" + Integer.toString(id) ;
//		try {
//			db.execSQL(sql);
//		} catch (Exception e) {
//			System.out.println("DeleteSubsidenceCrossSection:" + e.getLocalizedMessage());
//		}
	}

	@Override
	//获取单个地表下沉断面
	public Boolean UpdateSubsidenceCrossSection(SubsidenceCrossSectionInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String sql = "update SubsidenceCrossSectionIndex set Chainage=?,InbuiltTime=?,Width=?," +
				" SurveyPnts=?,Info=?,ChainagePrefix=?,DBU0=?,DBU0Time=?,DBU0Description=?,DBLimitVelocity=?," +
				" Lithologic=?,LAYVALUE=?,ROCKGRADE=? where ID=?";
		try {
			Object[] obj = new Object[14];
			obj[0] = s.getChainage();
			obj[1] = s.getInbuiltTime();
			obj[2] = s.getWidth();
			obj[3] = s.getSurveyPnts();
			obj[4] = s.getInfo();
//			obj[5] = s.getChainagePrefix();
			obj[6] = s.getDBU0();
			obj[7] = s.getDBU0Time();
			obj[8] = s.getDBU0Description();
			obj[9] = s.getDBLimitVelocity();
			obj[10] = s.getLithologic();
			obj[11] = s.getLayvalue();
			obj[12] = s.getRockgrade();
			obj[13] = s.getId();
			db.execSQL(sql, obj);
			result = true;
			obj = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("UpdateSubsidenceCrossSection:" + e.getLocalizedMessage());
		}
		return result;
	}
	public String GetSectionName(SubsidenceCrossSectionInfo Value) {
		
		String sName = "";
//		int iChainage = (int)(Value.getChainage().doubleValue()); 
//		int iDiv = 0,iMod = 0;
//		iDiv = iChainage / 1000;
//		iMod = iChainage % 1000;
//		double dMod = (double)iMod+(Value.getChainage().doubleValue()-(double)iChainage);
//		sName = Value.getChainagePrefix();
//		if (iDiv > 0) {
//			sName += Integer.toString(iDiv);
//		}
//		sName += '+';
//		sName += Double.toString(dMod);
		
		return sName;
	}

	@Override
	public void GetSubsidenceCrossSectionList(List<SubsidenceCrossSectionInfo> lt)
	{
		if(lt == null)
		{
			lt = new ArrayList<SubsidenceCrossSectionInfo>();
		}
		else
		{
			lt.clear();
		}
		String sql = "select * from SubsidenceCrossSectionIndex";
		SubsidenceCrossSectionInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			while(c.moveToNext()){
				entity = new SubsidenceCrossSectionInfo();
				iIndex = c.getColumnIndex("Id");
				entity.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("Chainage");
//				entity.setChainage(c.getDouble(iIndex));
				iIndex = c.getColumnIndex("InbuiltTime");
//				entity.setInbuiltTime(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("Width");
				entity.setWidth(c.getInt(iIndex));
				iIndex = c.getColumnIndex("SurveyPnts");
				entity.setSurveyPnts(c.getString(iIndex));
				iIndex = c.getColumnIndex("Info");
				entity.setInfo(c.getString(iIndex));
				iIndex = c.getColumnIndex("ChainagePrefix");
//				entity.setChainagePrefix(c.getString(iIndex));
//				iIndex = c.getColumnIndex("DBU0");
//				entity.setDBU0(c.getFloat(iIndex));
//				iIndex = c.getColumnIndex("DBU0Time");
//				entity.setDBU0Time(Timestamp.valueOf(c.getString(iIndex)));
//				iIndex = c.getColumnIndex("DBU0Description");
//				entity.setDBU0Description(c.getString(iIndex));
//				iIndex = c.getColumnIndex("DBLimitVelocity");
//				entity.setDBLimitVelocity(c.getFloat(iIndex));
//				iIndex = c.getColumnIndex("Lithologic");
//				entity.setLithologic(c.getString(iIndex));
//				iIndex = c.getColumnIndex("LAYVALUE");
//				entity.setLayvalue(c.getFloat(iIndex));
//				iIndex = c.getColumnIndex("ROCKGRADE");
				entity.setRockgrade(c.getString(iIndex));
				entity.setChainageName(GetSectionName(entity));
				
				lt.add(entity);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAllSection:"+e.getLocalizedMessage());
		}
		
	}

}
