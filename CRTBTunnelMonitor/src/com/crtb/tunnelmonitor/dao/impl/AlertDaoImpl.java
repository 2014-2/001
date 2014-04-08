package com.crtb.tunnelmonitor.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.dao.AlertDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.AlertInfo;
/**
 * 预警内容数据库接口实现
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class AlertDaoImpl implements AlertDao{
	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;

	public AlertDaoImpl(Context c,String name) {
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();

	}
	/**查询全部*/
	public List<AlertInfo> SelectAllAlert() {
		List<AlertInfo> list = null;
		String sql = "select * from AlertList";
		AlertInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					entity = new AlertInfo();
					iIndex = c.getColumnIndex("ID");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("SheetID");
					entity.setSheetID(Timestamp.valueOf(c.getString(iIndex)));
					iIndex = c.getColumnIndex("CrossSectionID");
					entity.setCrossSectionID(c.getString(iIndex));
					iIndex = c.getColumnIndex("PntType");
					entity.setPntType(c.getInt(iIndex));
					iIndex = c.getColumnIndex("AlertTime");
					entity.setAlertTime(Timestamp.valueOf(c.getString(iIndex)));
					iIndex = c.getColumnIndex("AlertLeverl");
					entity.setAlertLeverl(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Utype");
					entity.setUtype(c.getInt(iIndex));
					iIndex = c.getColumnIndex("UValue");
					entity.setUValue(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("UMax");
					entity.setUMax(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("OriginalDataID");
					entity.setOriginalDataID(c.getString(iIndex));
					if(list == null){
						list = new ArrayList<AlertInfo>();
					}
					list.add(entity);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAllAlert:" + e.getLocalizedMessage());
		}
		return list;
	}

	/**新建预警*/
	public Boolean InsertAlert(AlertInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String sql = "insert into AlertList(SheetID,CrossSectionID,PntType,AlertTime," +
				"AlertLeverl,Utype,UValue,UMax,OriginalDataID) vlaues(?,?,?,?,?,?,?,?,?)";
		try {
			Object[] obj = new Object[9];
			obj[0] = s.getSheetID();
			obj[1] = s.getCrossSectionID();
			obj[2] = s.getPntType();
			obj[3] = s.getAlertTime();
			obj[4] = s.getAlertLeverl();
			obj[5] = s.getUtype();
			obj[6] = s.getUValue();
			obj[7] = s.getUMax();
			obj[8] = s.getOriginalDataID();
			db.execSQL(sql, obj);
			result = true;
			obj = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertAlert:" + e.getLocalizedMessage());
		}
		return result;
	}

	/**查询预警*/
	public AlertInfo SelectAlert(int id) {
		AlertInfo entity = null;
		String sql = "select * from AlertList where id = " + Integer.toString(id);
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					if(entity==null){
						entity = new AlertInfo();
					}
					iIndex = c.getColumnIndex("ID");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("SheetID");
					entity.setSheetID(Timestamp.valueOf(c.getString(iIndex)));
					iIndex = c.getColumnIndex("CrossSectionID");
					entity.setCrossSectionID(c.getString(iIndex));
					iIndex = c.getColumnIndex("PntType");
					entity.setPntType(c.getInt(iIndex));
					iIndex = c.getColumnIndex("AlertTime");
					entity.setAlertTime(Timestamp.valueOf(c.getString(iIndex)));
					iIndex = c.getColumnIndex("AlertLeverl");
					entity.setAlertLeverl(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Utype");
					entity.setUtype(c.getInt(iIndex));
					iIndex = c.getColumnIndex("UValue");
					entity.setUValue(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("UMax");
					entity.setUMax(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("OriginalDataID");
					entity.setOriginalDataID(c.getString(iIndex));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAlert:" + e.getLocalizedMessage());
		}
		return null;
	}

	/**删除预警*/
	public Boolean DeleteAlert(int id) {
		boolean result = false;
		String sql = "delete from AlertList where id=?";
		try {
			Object[] obj = {id};
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("DeleteAlert:" + e.getLocalizedMessage());
		}
		return result;
	}

	/**编辑预警*/
	public Boolean UpdateAlert(AlertInfo s) {
		boolean result = false;
		if(s==null){
			return result;
		}
		String sql = "update AlertList set SheetID=?,CrossSectionID=?,PntType=?," +
				"AlertLeverl=?,Utype=?,UValue=?,UMax=?,OriginalDataID=? where id = ?";
		try {
			Object[] obj = {s.getSheetID(),s.getCrossSectionID(),s.getPntType(),
					s.getAlertLeverl(),s.getUtype(),s.getUValue(),s.getUMax(),
					s.getOriginalDataID(),s.getId()};
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("UpdateAlert:" + e.getLocalizedMessage());
		}
		return result;
	}
}
