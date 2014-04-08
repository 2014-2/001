package com.crtb.tunnelmonitor.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.dao.AlertHandlingDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.AlertHandlingInfo;

/**
 * 预警日志数据库接口实现
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class AlertHandlingDaoImpl implements AlertHandlingDao{
	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;

	public AlertHandlingDaoImpl(Context c,String name) {
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();

	}
	/**查询全部*/
	public List<AlertHandlingInfo> SelectAllAlertHandling() {
		List<AlertHandlingInfo> list = null;
		String sql = "select * from AlertHandlingList";
		AlertHandlingInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					entity = new AlertHandlingInfo();
					iIndex = c.getColumnIndex("ID");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("AlertID");
					entity.setAlertID(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Handling");
					entity.setHandling(c.getInt(iIndex));
					iIndex = c.getColumnIndex("HandlingTime");
					entity.setHandlingTime(Timestamp.valueOf(c.getString(iIndex)));
					iIndex = c.getColumnIndex("DuePerson");
					entity.setDuePerson(c.getString(iIndex));
					iIndex = c.getColumnIndex("AlertStatus");
					entity.setAlertStatus(c.getInt(iIndex));
					iIndex = c.getColumnIndex("HandlingInfo");
					entity.setHandlingInfo(c.getInt(iIndex));
					if(list == null){
						list = new ArrayList<AlertHandlingInfo>();
					}
					list.add(entity);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAllAlertHandling:" + e.getLocalizedMessage());
		}
		return list;
	}

	/**新建预警*/
	public Boolean InsertAlertHandling(AlertHandlingInfo s) {
		boolean result = false;
		if(s==null){
			return result;
		}
		String sql = "insert into AlertHandlingList(AlertID,Handling,HandlingTime,DuePerson," +
				"AlertStatus,HandlingInfo) values(?,?,?,?,?,?)";
		try {
			Object[] obj = {s.getAlertID(),s.getHandling(),s.getHandlingTime(),s.getDuePerson(),s.getAlertStatus(),s.getHandlingInfo()};
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertAlertHandling:" + e.getLocalizedMessage());
		}
		return result;
	}
}
