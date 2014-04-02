package com.sxlc.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxlc.dao.TotalStationDao;
import com.sxlc.db.SqliteHelperDTMS;
import com.sxlc.entity.RecordInfo;
import com.sxlc.entity.SubsidenceTotalDataInfo;
import com.sxlc.entity.TotalStationInfo;
import com.sxlc.entity.WorkInfos;

public class TotalStationDaoImpl implements TotalStationDao{
	
	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;
	public TotalStationDaoImpl(Context c,String name){
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();
	}
	
	/**查询全部全站仪连接参数信息*/
	public List<TotalStationInfo> SelectAllTotalStation() {
		List<TotalStationInfo> list = null;
		String sql = "select * from TotalStationIndex";
		TotalStationInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					entity = new TotalStationInfo();
					iIndex = c.getColumnIndex("ID");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Name");
					entity.setName(c.getString(iIndex));
					iIndex = c.getColumnIndex("TotalstationType");
					entity.setTotalstationType(c.getString(iIndex));
					iIndex = c.getColumnIndex("BaudRate");
					entity.setBaudRate(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Port");
					entity.setPort(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Parity");
					entity.setParity(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Databits");
					entity.setDatabits(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Stopbits");
					entity.setStopbits(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));
					if(list == null){
						list = new ArrayList<TotalStationInfo>();
					}
					list.add(entity);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAllTotalStation:" + e.getLocalizedMessage());
		}
		return list;
	}

	/**新建全站仪连接参数信息*/
	public Boolean InsertTotalStation(TotalStationInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String sql = "insert into TotalStationIndex(Name,TotalstationType,BaudRate," +
				"Port,Parity,Databits,Stopbits,Info) values(?,?,?,?,?,?,?,?)";
		try {
			Object[] obj = new Object[8];
			obj[0] = s.getName();
			obj[1] = s.getTotalstationType();
			obj[2] = s.getBaudRate();
			obj[3] = s.getPort();
			obj[4] = s.getParity();
			obj[5] = s.getDatabits();
			obj[6] = s.getStopbits();
			obj[7] = s.getInfo();
			db.execSQL(sql, obj);
			result = true;
			obj = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertTotalStation:" + e.getLocalizedMessage());
		}
		return result;
	}

	/**查询全站仪连接参数信息*/
	public TotalStationInfo SelectTotalStation(int id) {
		TotalStationInfo entity = null;
		String sql = "select * from TotalStationIndex where id=" + Integer.toString(id);
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					if(entity == null){
						entity = new TotalStationInfo();
					}
					iIndex = c.getColumnIndex("ID");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Name");
					entity.setName(c.getString(iIndex));
					iIndex = c.getColumnIndex("TotalstationType");
					entity.setTotalstationType(c.getString(iIndex));
					iIndex = c.getColumnIndex("BaudRate");
					entity.setBaudRate(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Port");
					entity.setPort(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Parity");
					entity.setParity(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Databits");
					entity.setDatabits(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Stopbits");
					entity.setStopbits(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectTotalStation:" + e.getLocalizedMessage());
		}
		return entity;
	}

	/**删除全站仪连接参数信息*/
	public Boolean DeleteTotalStation(int id) {
		boolean result = false;
		String sql = "delete from TotalStationIndex where id =?";
		try {
			Object[] obj = {id};
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("DeleteTotalStation:" + e.getLocalizedMessage());
		}
		return result;
	}

	/**编辑全站仪连接参数信息*/
	public Boolean UpdateTotalStation(TotalStationInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String sql = "update TotalStationIndex set Name=?,TotalstationType=?,BaudRate=?," +
				"Port=?,Parity=?,Databits=?,Stopbits=?,Info=? where id = ?";
		try {
			Object[] obj = new Object[9];
			obj[0] = s.getName();
			obj[1] = s.getTotalstationType();
			obj[2] = s.getBaudRate();
			obj[3] = s.getPort();
			obj[4] = s.getParity();
			obj[5] = s.getDatabits();
			obj[6] = s.getStopbits();
			obj[7] = s.getInfo();
			obj[8] = s.getId();
			db.execSQL(sql, obj);
			result = true;
			obj = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("UpdateTotalStation:" + e.getLocalizedMessage());
		}
		return result;
	}
	@Override
	public void GetTotalStationList(List<TotalStationInfo> list) {
		if(list == null)
		{
			list = new ArrayList<TotalStationInfo>();
		}
		else
		{
			list.clear();
		}
		String sql = "select * from TotalStationIndex";
		TotalStationInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					entity = new TotalStationInfo();
					iIndex = c.getColumnIndex("Id");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Name");
					entity.setName(c.getString(iIndex));
					iIndex = c.getColumnIndex("TotalstationType");
					entity.setTotalstationType(c.getString(iIndex));
					iIndex = c.getColumnIndex("BaudRate");
					entity.setBaudRate(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Port");
					entity.setPort(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Parity");
					entity.setParity(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Databits");
					entity.setDatabits(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Stopbits");
					entity.setStopbits(c.getInt(iIndex));
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
