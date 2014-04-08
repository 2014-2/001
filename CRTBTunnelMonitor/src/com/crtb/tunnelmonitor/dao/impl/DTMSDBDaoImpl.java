/**
 * 
 */
package com.crtb.tunnelmonitor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.db.DTMSDB;
import com.crtb.tunnelmonitor.entity.WorkInfos;

/**
 * @author Administrator
 *
 */
public class DTMSDBDaoImpl {
	/**
	 * 
	 */
	private DTMSDB helper = null;
	private SQLiteDatabase db = null;

	public DTMSDBDaoImpl(Context c) {
		helper = new DTMSDB(c);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void ConnectDB()
	{
		if(db == null)
		{
			db = helper.getReadableDatabase();
		}
	}
	
	
	public void GetWorkList(List<WorkInfos> lt) {

		if(lt == null)
		{
			lt = new ArrayList<WorkInfos>();
		}
		else
		{
			lt.clear();
		}
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
		}
		return false;
	}

	public void DeleteWork(String name) {
		db.execSQL("delete from ProjectIndex where ProjectName = ?",
				new Object[] { name });

	}

	public void UpdateWork(WorkInfos w) {
		db.execSQL(
				"update ProjectIndex set ConstructionFirm=?,Info=? where ProjectName=?",
				new Object[] { w.getConstructionFirm(), w.getInfo(),
						w.getProjectName() });

	}
}
