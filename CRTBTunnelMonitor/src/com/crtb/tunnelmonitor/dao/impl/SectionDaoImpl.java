package com.crtb.tunnelmonitor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.dao.SectionDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.SectionInfo;
/**
 * 断面数据库实现
 *创建时间：2014-3-21下午6:43:15
 *@author 张涛
 *@since JDK1.6
 *@version 1.0
 */
public class SectionDaoImpl implements SectionDao{

	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;

	public SectionDaoImpl(Context c,String name) {
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();

	}
	
	@Override
	public List<SectionInfo> SectionAll() {
		List<SectionInfo> list = new ArrayList<SectionInfo>();
		Cursor c = db.rawQuery("select Chainage,ChainagePrefix from TunnelCrossSectionIndex", null);
		while (c.moveToNext()) {
			SectionInfo s =new SectionInfo();
			s.setChainage(Double.valueOf(c.getString(c.getColumnIndex("Chainage"))));
			s.setChainagePrefix(c.getString(c.getColumnIndex("ChainagePrefix")));
			list.add(s);
		}
		return list;
	}

	@Override
	public Boolean InsertSection(SectionInfo s) {
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
	public List<SectionInfo> SectiondibiaoAll() {
		List<SectionInfo> list = new ArrayList<SectionInfo>();
		Cursor c = db.rawQuery("select Chainage from SubsidenceCrossSectionIndex", null);
		while (c.moveToNext()) {
			SectionInfo s =new SectionInfo();
			s.setChainage(Double.valueOf(c.getString(c.getColumnIndex("Chainage"))));
			list.add(s);
		}
		return list;
	}

	@Override
	public Boolean InsertSectiondibiao(SectionInfo s) {
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

	@Override
	public SectionInfo SelectSection() {
		return null;
	}

	@Override
	public void DeleteSection(Double d) {
		db.execSQL("delete from TunnelCrossSectionIndex where Chainage = ?",
				new Object[] { d });
		
	}

	@Override
	public void UpdateSection(SectionInfo s) {
		
	}

	@Override
	public SectionInfo SelectSectiondibiao(SectionInfo s) {
		return null;
	}

	@Override
	public void DeleteSectiondibiao(Double d) {
		db.execSQL("delete from SubsidenceCrossSectionIndex where Chainage = ?",
				new Object[] { d });
		
	}

	@Override
	public void UpdateSectiondibiao(SectionInfo s) {
		
	}

	

	


}
