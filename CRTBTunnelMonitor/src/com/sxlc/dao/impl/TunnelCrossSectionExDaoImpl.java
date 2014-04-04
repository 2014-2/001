package com.sxlc.dao.impl;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxlc.dao.TunnelCrossSectionExDao;
import com.sxlc.db.SqliteHelperDTMS;
import com.sxlc.entity.TunnelCrossSectionExInfo;

/**
 * 断面测量数据库接口实现
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */

public class TunnelCrossSectionExDaoImpl implements TunnelCrossSectionExDao{

	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;
	public TunnelCrossSectionExDaoImpl(Context c,String name){
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();
	}
	
	//查询所有测量数据
	public List<TunnelCrossSectionExInfo> GetAllTunnelCrossSection() {
		List<TunnelCrossSectionExInfo> list = null;
		String sql = "select * from TunnelCrossSectionExIndex";
		TunnelCrossSectionExInfo entity = null;
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			while(c.moveToNext()){
				entity = new TunnelCrossSectionExInfo();
				iIndex = c.getColumnIndex("ID");
				entity.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("ZONECODE");
				entity.setZonecode(c.getString(iIndex));
				iIndex = c.getColumnIndex("SITECODE");
				entity.setSitecode(c.getString(iIndex));
				iIndex = c.getColumnIndex("SECTNAME");
				entity.setSectname(c.getString(iIndex));
				iIndex = c.getColumnIndex("SECTCODE");
				entity.setSectcode(c.getString(iIndex));
				iIndex = c.getColumnIndex("SECTKILO");
				entity.setSectkilo(c.getString(iIndex));
				iIndex = c.getColumnIndex("METHOD");
				entity.setMethod(c.getString(iIndex));
				iIndex = c.getColumnIndex("WIDTH");
				entity.setWidth(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("MOVEVALUE_U0");
				entity.setMovevalue_uo(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("UPDATEDATE");
				entity.setUpdateDate(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("REMARK_U0");
				entity.setRemark_uo(c.getString(iIndex));
				iIndex = c.getColumnIndex("HOLENAME");
				entity.setHolename(c.getString(iIndex));
				iIndex = c.getColumnIndex("HOLESTARTKILO");
				entity.setHolestartkilo(c.getString(iIndex));
				iIndex = c.getColumnIndex("INNERCODES");
				entity.setInnercode(c.getString(iIndex));
				iIndex = c.getColumnIndex("LAYTIME");
				entity.setLayDate(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("UPLOAD");
				entity.setUpload(c.getInt(iIndex));
				iIndex = c.getColumnIndex("DESCRIPTION");
				entity.setDescription(c.getString(iIndex));
				if(list == null){
					list = new ArrayList<TunnelCrossSectionExInfo>();
				}
				list.add(entity);
			}
		} catch (Exception e) {
			System.out.println("GetAllTunnelCrossSection:" + e.getLocalizedMessage());
		}
		
		return list;
	}

	//添加测量数据
	public Boolean InsertTunnelCrossSection(TunnelCrossSectionExInfo t) {
		boolean result = false;
		if(t== null){
			return result;
		}
		String sql = "insert into TunnelCrossSectionExIndex(ZONECODE,SITECODE,SECTNAME,SECTCODE,SECTKILO," +
				" METHOD,WIDTH,MOVEVALUE_U0,UPDATEDATE,REMARK_U0,HOLENAME,HOLESTARTKILO,INNERCODES,LAYTIME," +
				" UPLOAD,DESCRIPTION) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			Object[] objects = new Object[16];
			objects[0] = t.getZonecode();
			objects[1] = t.getSitecode();
			objects[2] = t.getSectname();
			objects[3] = t.getSectcode();
			objects[4] = t.getSectkilo();
			objects[5] = t.getMethod();
			objects[6] = t.getWidth();
			objects[7] = t.getMovevalue_uo();
			objects[8] = t.getUpdateDate();
			objects[9] = t.getRemark_uo();
			objects[10] = t.getHolename();
			objects[11] = t.getHolestartkilo();
			objects[12] = t.getInnercode();
			objects[13] = t.getLayDate();
			objects[14] = t.getUpload();
			objects[15] = t.getDescription();
			db.execSQL(sql, objects);
			result = true;
			objects = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertTunnelCrossSection:" + e.getLocalizedMessage());
		}
		return result;
	}
	
	//修改测量信息
	public Boolean UpdateTunnelCrossSection(TunnelCrossSectionExInfo t) {
		boolean result = false;
		if(t== null){
			return result;
		}
		String sql = "update TunnelCrossSectionExIndex set ZONECODE=?,SITECODE=?,SECTNAME=?,SECTCODE=?,SECTKILO=?," +
				" METHOD=?,WIDTH=?,MOVEVALUE_U0=?,UPDATEDATE=?,REMARK_U0=?,HOLENAME=?,HOLESTARTKILO=?,INNERCODES=?,LAYTIME=?," +
				" UPLOAD=?,DESCRIPTION=? where id = ?";
		try {
			Object[] objects = new Object[16];
			objects[0] = t.getZonecode();
			objects[1] = t.getSitecode();
			objects[2] = t.getSectname();
			objects[3] = t.getSectcode();
			objects[4] = t.getSectkilo();
			objects[5] = t.getMethod();
			objects[6] = t.getWidth();
			objects[7] = t.getMovevalue_uo();
			objects[8] = t.getUpdateDate();
			objects[9] = t.getRemark_uo();
			objects[10] = t.getHolename();
			objects[11] = t.getHolestartkilo();
			objects[12] = t.getInnercode();
			objects[13] = t.getLayDate();
			objects[14] = t.getUpload();
			objects[15] = t.getDescription();
			objects[16] = t.getId();
			db.execSQL(sql,objects);
			result = true;
			objects = null;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("UpdateTunnelCrossSection:" + e.getLocalizedMessage());
		}
		return result;
	}

	@Override
	//删除测量信息
	public Boolean DeleteTunnelCrossSection(int id) {
		boolean result = false;
		String sql = "delete from TunnelCrossSectionExIndex where id = ?";
		try {
			Object[] obj = new Object[1];
			obj[0] = id;
			db.execSQL(sql,obj);
			obj = null;
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("DeleteTunnelCrossSection:" + e.getLocalizedMessage());
			result = false;
		}
		return result;
	}

	@Override
	//查看测量信息
	public TunnelCrossSectionExInfo GetTunnelCrossSectionExInfo(int id) {
		TunnelCrossSectionExInfo entity = null;
		String sql = "select * from DeleteTunnelCrossSection where id=" + Integer.toString(id);
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql,null);
			while(c.moveToNext()){
				if(entity == null){
					entity = new TunnelCrossSectionExInfo();
				}
				iIndex = c.getColumnIndex("ID");
				entity.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("ZONECODE");
				entity.setZonecode(c.getString(iIndex));
				iIndex = c.getColumnIndex("SITECODE");
				entity.setSitecode(c.getString(iIndex));
				iIndex = c.getColumnIndex("SECTNAME");
				entity.setSectname(c.getString(iIndex));
				iIndex = c.getColumnIndex("SECTCODE");
				entity.setSectcode(c.getString(iIndex));
				iIndex = c.getColumnIndex("SECTKILO");
				entity.setSectkilo(c.getString(iIndex));
				iIndex = c.getColumnIndex("METHOD");
				entity.setMethod(c.getString(iIndex));
				iIndex = c.getColumnIndex("WIDTH");
				entity.setWidth(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("MOVEVALUE_U0");
				entity.setMovevalue_uo(c.getFloat(iIndex));
				iIndex = c.getColumnIndex("UPDATEDATE");
				entity.setUpdateDate(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("REMARK_U0");
				entity.setRemark_uo(c.getString(iIndex));
				iIndex = c.getColumnIndex("HOLENAME");
				entity.setHolename(c.getString(iIndex));
				iIndex = c.getColumnIndex("HOLESTARTKILO");
				entity.setHolestartkilo(c.getString(iIndex));
				iIndex = c.getColumnIndex("INNERCODES");
				entity.setInnercode(c.getString(iIndex));
				iIndex = c.getColumnIndex("LAYTIME");
				entity.setLayDate(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("UPLOAD");
				entity.setUpload(c.getInt(iIndex));
				iIndex = c.getColumnIndex("DESCRIPTION");
				entity.setDescription(c.getString(iIndex));
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("GetTunnelCrossSectionExInfo:" + e.getLocalizedMessage());
		}
		return null;
	}

}
