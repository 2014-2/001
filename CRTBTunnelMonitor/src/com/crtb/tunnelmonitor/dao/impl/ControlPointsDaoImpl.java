package com.crtb.tunnelmonitor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crtb.tunnelmonitor.dao.ControlPointsDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.ControlPointsInfo;
import com.crtb.tunnelmonitor.entity.TotalStationInfo;
/**
 * 控制点数据库接口实现
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class ControlPointsDaoImpl implements ControlPointsDao{
	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;
	public ControlPointsDaoImpl(Context c,String name){
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();
	}
	
	@Override
	//查询所有控制点
	public List<ControlPointsInfo> GetAllStation() {
		List<ControlPointsInfo> list = null;
		ControlPointsInfo entity = null;
		String sql = "select * from ControlPointsIndex";
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					entity = new ControlPointsInfo();
					iIndex = c.getColumnIndex("Id");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Name");
					entity.setName(c.getString(iIndex));
					iIndex = c.getColumnIndex("x");
					entity.setX(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("y");
					entity.setY(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("z");
					entity.setZ(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));
					if(list == null){
						list = new ArrayList<ControlPointsInfo>();
					}
					list.add(entity);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("GetAllStation:" + e.getLocalizedMessage());
		}
		return list;
	}

	@Override
	//添加控制点
	public boolean InsertStationInfo(ControlPointsInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String sql = "insert into ControlPointsIndex(Name,x,y,z,Info) values(?,?,?,?,?)";
		try {
			Object[] obj = new Object[5];
			obj[0] = s.getName();
			obj[1] = s.getX();
			obj[2] = s.getY();
			obj[3] = s.getZ();
			obj[4] = s.getInfo();
			db.execSQL(sql, obj);
			result = true;
			obj = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertStationInfo:" + e.getLocalizedMessage());
		}
		return result;
	}

	@Override
	//更新控制点
	public boolean UpdateStationInfo(ControlPointsInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String sql = "update ControlPointsIndex set Name=?,x=?,y=?,z=?,Info=? where id=?";
		try {
			Object[] obj = new Object[6];
			obj[0] = s.getName();
			obj[1] = s.getX();
			obj[2] = s.getY();
			obj[3] = s.getZ();
			obj[4] = s.getInfo();
			obj[5] = s.getId();
			db.execSQL(sql, obj);
			result = true;
			obj = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("UpdateStationInfo:" + e.getLocalizedMessage());
		}
		return result;
	}

	@Override
	// 获取单个控制点
	public ControlPointsInfo GetControlPoints(int id) {
		ControlPointsInfo entity = null;
		String sql = "select * from ControlPointsIndex where id=" + Integer.toString(id);
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					if(entity == null)
						entity = new ControlPointsInfo();
					iIndex = c.getColumnIndex("Id");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Name");
					entity.setName(c.getString(iIndex));
					iIndex = c.getColumnIndex("x");
					entity.setX(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("y");
					entity.setY(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("z");
					entity.setZ(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("GetControlPoints:" + e.getLocalizedMessage());
		}
		return entity;
	}

	@Override
	//删除控制点
	public boolean DeleteStationInfo(int id) {
		boolean result = false;
		String sql = "delete from ControlPointsIndex where id = ?";
		try {
			Object[] obj = {id};
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("DeleteStationInfo:" + e.getLocalizedMessage());
		}
		return result;
	}
	
	@Override
	public void GetControlPointsList(List<ControlPointsInfo> list) {
		if(list == null)
		{
			list = new ArrayList<ControlPointsInfo>();
		}
		else
		{
			list.clear();
		}
		String sql = "select * from ControlPointsIndex";
		ControlPointsInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					entity = new ControlPointsInfo();
					iIndex = c.getColumnIndex("Id");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Name");
					entity.setName(c.getString(iIndex));
					iIndex = c.getColumnIndex("X");
					entity.setX(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("Y");
					entity.setY(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("Z");
					entity.setZ(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));

					list.add(entity);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAllTotalStation:" + e.getLocalizedMessage());
		}
	}
}
