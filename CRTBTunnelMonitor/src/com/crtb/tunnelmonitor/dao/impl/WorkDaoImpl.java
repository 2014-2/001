package com.crtb.tunnelmonitor.dao.impl;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.dao.WorkDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.WorkInfos;
import com.crtb.tunnelmonitor.entity.list_infos;
/**
 * 工作面数据库实现
 */
public class WorkDaoImpl implements WorkDao {

	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;

	public WorkDaoImpl(Context c, String name) {
		helper = new SqliteHelperDTMS(c, null, 0);
		db = helper.getReadableDatabase();
	}

	@Override
	public List<list_infos> SelectWork() {

		List<list_infos> list = new ArrayList<list_infos>();
		Cursor cursor = db
				.rawQuery(
						"select ProjectName,StartChainage,EndChainage from ProjectIndex",
						null);
		while (cursor.moveToNext()) {
			list_infos w = new list_infos();
			w.setWorkpagename(cursor.getString(cursor
					.getColumnIndex("ProjectName")));
			System.out.println(w.getWorkpagename() + "bb");
			w.setStart(cursor.getString(cursor.getColumnIndex("StartChainage")));
			w.setEnd(cursor.getString(cursor.getColumnIndex("EndChainage")));
			list.add(w);
		}
		return list;

	}

	@Override
	public void GetWorkList(List<WorkInfos> lt) {

		if(lt == null)
		{
			lt = new ArrayList<WorkInfos>();
		}
		else
		{
			lt.clear();
		}
		//List<list_infos> list = new ArrayList<list_infos>();
		Cursor cursor = db
				.rawQuery(
						"select * from ProjectIndex",
						null);
		while (cursor.moveToNext()) {
			WorkInfos w = new WorkInfos();
			w.setProjectName(cursor.getString(cursor.getColumnIndex("ProjectName")));
			w.setCreateTime(cursor.getString(cursor.getColumnIndex("CreateTime")));
			w.setStartChainage(Double.valueOf(cursor.getString(cursor
					.getColumnIndex("StartChainage"))));
			w.setEndChainage(Double.valueOf(cursor.getString(cursor
					.getColumnIndex("EndChainage"))));
			w.setChainagePrefix(cursor.getString(cursor
					.getColumnIndex("ChainagePrefix")));
			w.setLastOpenTime(cursor.getString(cursor
					.getColumnIndex("LastOpenTime")));
			w.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
			w.setGDLimitVelocity(Float.valueOf(cursor.getString(cursor
					.getColumnIndex("GDLimitVelocity"))));
			w.setGDLimitTotalSettlement(Float.valueOf(cursor.getString(cursor
					.getColumnIndex("GDLimitTotalSettlement"))));
			w.setSLLimitVelocity(Float.valueOf(cursor.getString(cursor
					.getColumnIndex("SLLimitVelocity"))));
			w.setSLLimitTotalSettlement(Float.valueOf(cursor.getString(cursor
					.getColumnIndex("SLLimitTotalSettlement"))));
			w.setDBLimitVelocity(Float.valueOf(cursor.getString(cursor
					.getColumnIndex("DBLimitVelocity"))));
			w.setDBLimitTotalSettlement(cursor.getString(cursor
					.getColumnIndex("DBLimitTotalSettlement")));
			w.setConstructionFirm(cursor.getString(cursor
					.getColumnIndex("ConstructionFirm")));
			w.setLimitedTotalSubsidenceTime(cursor.getString(cursor
					.getColumnIndex("LimitedTotalSubsidenceTime")));
			lt.add(w);
		}
	}
	@Override
	public Boolean InsertWork(WorkInfos w) {
		try {

			db.execSQL(
					"insert into ProjectIndex(ProjectName,CreateTime,"
							+ "StartChainage,EndChainage,ChainagePrefix,LastOpenTime,Info,"
							+ "GDLimitVelocity,GDLimitTotalSettlement,"
							+ "SLLimitVelocity,SLLimitTotalSettlement,"
							+ "DBLimitVelocity,DBLimitTotalSettlement,ConstructionFirm,"
							+ "LimitedTotalSubsidenceTime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { w.getProjectName(), w.getCreateTime(),
							w.getStartChainage(), w.getEndChainage(),
							w.getChainagePrefix(), w.getLastOpenTime(),
							w.getInfo(), w.getGDLimitVelocity(),
							w.getGDLimitTotalSettlement(),
							w.getSLLimitVelocity(),
							w.getSLLimitTotalSettlement(),
							w.getDBLimitVelocity(),
							w.getDBLimitTotalSettlement(),
							w.getConstructionFirm(),
							w.getLimitedTotalSubsidenceTime() });
			return true;
		} catch (Exception e) {
			System.out.println("InsertWork:" + e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	public void DeleteWork(String name) {
		db.execSQL("delete from ProjectIndex where ProjectName = ?",
				new Object[] { name });

	}

	@Override
	public void UpdateWork(WorkInfos w) {
		db.execSQL(
				"update ProjectIndex set ConstructionFirm=?,Info=? where ProjectName=?",
				new Object[] { w.getConstructionFirm(), w.getInfo(),
						w.getProjectName() });

	}

	@Override
	public WorkInfos SelectWorkMsg() {
		WorkInfos w = new WorkInfos();
		Cursor cursor = db.rawQuery("select * from ProjectIndex", null);
		w.setProjectName(cursor.getString(cursor.getColumnIndex("ProjectName")));
		w.setCreateTime(cursor.getString(cursor.getColumnIndex("CreateTime")));
		w.setStartChainage(Double.valueOf(cursor.getString(cursor
				.getColumnIndex("StartChainage"))));
		w.setEndChainage(Double.valueOf(cursor.getString(cursor
				.getColumnIndex("EndChainage"))));
		w.setChainagePrefix(cursor.getString(cursor
				.getColumnIndex("ChainagePrefix")));
		w.setLastOpenTime(cursor.getString(cursor
				.getColumnIndex("LastOpenTime")));
		w.setInfo(cursor.getString(cursor.getColumnIndex("Info")));
		w.setGDLimitVelocity(Float.valueOf(cursor.getString(cursor
				.getColumnIndex("GDLimitVelocity"))));
		w.setGDLimitTotalSettlement(Float.valueOf(cursor.getString(cursor
				.getColumnIndex("GDLimitTotalSettlement"))));
		w.setSLLimitVelocity(Float.valueOf(cursor.getString(cursor
				.getColumnIndex("SLLimitVelocity"))));
		w.setSLLimitTotalSettlement(Float.valueOf(cursor.getString(cursor
				.getColumnIndex("SLLimitTotalSettlement"))));
		w.setDBLimitVelocity(Float.valueOf(cursor.getString(cursor
				.getColumnIndex("DBLimitVelocity"))));
		w.setDBLimitTotalSettlement(cursor.getString(cursor
				.getColumnIndex("DBLimitTotalSettlement")));
		w.setConstructionFirm(cursor.getString(cursor
				.getColumnIndex("ConstructionFirm")));
		w.setLimitedTotalSubsidenceTime(cursor.getString(cursor
				.getColumnIndex("LimitedTotalSubsidenceTime")));

		return w;
	}

}
