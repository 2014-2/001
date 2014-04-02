package com.sxlc.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxlc.dao.SubsidenceTotalDataDao;
import com.sxlc.db.SqliteHelperDTMS;
import com.sxlc.entity.SubsidenceTotalDataInfo;
/**
 * 断面测量记录单数据库接口实现
 *创建时间：2014-3-24下午13:50:00
 *@author 张友
 *@since JDK1.6
 *@version 1.0
 */
public class SubsidenceTotalDataDaoImpl implements SubsidenceTotalDataDao{
	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;
	public SubsidenceTotalDataDaoImpl(Context c,String name){
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();
	}
	//条件查询记录单
	// stationId 设站id，sectionId断面id，rawsheetId记录id，type:类型  0：断面记录单  1：地表下沉记录单
	public List<SubsidenceTotalDataInfo> GetAllSubsidenceTotalData(
			int stationId, int sectionId, int rawsheetId,int type) {
		List<SubsidenceTotalDataInfo> list = null;
		SubsidenceTotalDataInfo entity = null;
		int iIndex = 0;
		String table = "";
		if(type == 0){
			table = " TunnelSettlementTotalData ";
		}else{
			table = " SubsidenceTotalData ";
		}
		String sql = "select * from " + table + " where id>0 ";
		if(stationId!=-1){
			sql = sql + " and StationId=" + Integer.toString(stationId); 
		}
		if(sectionId != -1){
			sql = sql + " and ChainageId=" + Integer.toString(sectionId);
		}
		if(rawsheetId!=-1){
			sql = sql + " and SheetId=" + Integer.toString(rawsheetId);
		}
		
		try {
			Cursor c = db.rawQuery(sql, null);
			while(c.moveToNext()){
				entity = new SubsidenceTotalDataInfo();
				entity.setType(type);
				iIndex = c.getColumnIndex("ID");
				entity.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("StationId");
				entity.setStationId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("ChainageId");
				entity.setChainageId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("SheetId");
				entity.setSheetId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("Coordinate");
				entity.setCoordinate(c.getString(iIndex));
				iIndex = c.getColumnIndex("PntType");
				entity.setPntType(c.getString(iIndex));
				iIndex = c.getColumnIndex("SurveyTime");
				entity.setSurveyTime(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("Info");
				entity.setInfo(c.getString(iIndex));
				iIndex = c.getColumnIndex("MEASNo");
				entity.setMEASNo(c.getShort(iIndex));
				iIndex = c.getColumnIndex("SurveyorID");
				entity.setSurveyorID(c.getInt(iIndex));
				iIndex = c.getColumnIndex("DataStatus");
				entity.setDataStatus(c.getShort(iIndex));
				iIndex = c.getColumnIndex("DataCorrection");
				entity.setDataCorrection(c.getFloat(iIndex));
				if(list == null){
					list = new ArrayList<SubsidenceTotalDataInfo>();
				}
				list.add(entity);
			}
		} catch (Exception e) {
			System.out.println("GetAllSubsidenceTotalData:" + e.getLocalizedMessage());
		}
		
		return list;
	}

	//添加记录单
	public Boolean InsertSubsidenceTotalData(SubsidenceTotalDataInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String table ="";
		if(s.getType() == 0){
			table = " TunnelSettlementTotalData ";
		}else{
			table = " SubsidenceTotalData ";
		}
		String sql = "insert into " + table + " (StationId,ChainageId,SheetId,Coordinate,PntType,SurveyTime," +
				"Info,MEASNo,SurveyorID,DataStatus,DataCorrection) values (?,?,?,?,?,?,?,?,?,?,?)";
		try {
			Object[] obj = new Object[11];
			obj[0] = s.getStationId();
			obj[1] = s.getChainageId();
			obj[2] = s.getSheetId();
			obj[3] = s.getCoordinate();
			obj[4] = s.getPntType();
			obj[5] = s.getSurveyTime();
			obj[6] = s.getInfo();
			obj[7] = s.getMEASNo();
			obj[8] = s.getSurveyorID();
			obj[9] = s.getDataStatus();
			obj[10] = s.getDataCorrection();
			db.execSQL(sql, obj);
			obj =null;
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertSubsidenceTotalData:" + e.getLocalizedMessage());
		}
		return result;
	}

	//更新记录单信息
	public Boolean UpdateSubsidenceTotalData(SubsidenceTotalDataInfo s) {
		boolean result = false;
		if(s == null){
			return result;
		}
		String table ="";
		if(s.getType() == 0){
			table = " TunnelSettlementTotalData ";
		}else{
			table = " SubsidenceTotalData ";
		}
		String sql = "update " + table + " set StationId=?,ChainageId=?,SheetId=?,Coordinate=?,PntType=?,SurveyTime=?," +
				"Info=?,MEASNo=?,SurveyorID=?,DataStatus=?,DataCorrection=? where id=?";
		try {
			Object[] obj = new Object[12];
			obj[0] = s.getStationId();
			obj[1] = s.getChainageId();
			obj[2] = s.getSheetId();
			obj[3] = s.getCoordinate();
			obj[4] = s.getPntType();
			obj[5] = s.getSurveyTime();
			obj[6] = s.getInfo();
			obj[7] = s.getMEASNo();
			obj[8] = s.getSurveyorID();
			obj[9] = s.getDataStatus();
			obj[10] = s.getDataCorrection();
			obj[11] = s.getId();
			db.execSQL(sql, obj);
			obj = null;
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("UpdateSubsidenceTotalData:" + e.getLocalizedMessage());
		}
		return result;
	}

	//删除记录单信息
	public Boolean DeleteSubsidenceTotalData(int id, int level,int type) {
		boolean result = false;
		String table ="";
		if(type == 0){
			table = " TunnelSettlementTotalData ";
		}else{
			table = " SubsidenceTotalData ";
		}
		String sql = "delete from " + table + " where ";
		if(level == 0){
			sql = sql + " id ";
		}else if(level == 1){
			sql = sql + " SheetId ";
		}else if(level == 2){
			sql = sql + " ChainageId ";
		}else if(level == 3){
			sql = sql + " StationId ";
		}
		sql = sql + " = " + Integer.toString(id);
		try {
			db.execSQL(sql);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("DeleteSubsidenceTotalData:" + e.getLocalizedMessage());
		}
		
		return result;
	}

	//根据id获取单条记录单
	public SubsidenceTotalDataInfo GetSubsidenceTotalData(int id,int type) {
		SubsidenceTotalDataInfo entity = null;
		String table ="";
		if(type == 0){
			table = " TunnelSettlementTotalData ";
		}else{
			table = " SubsidenceTotalData ";
		}
		int iIndex = 0;
		String sql = "select * from " + table + " where id=" + Integer.toString(id);
		try {
			Cursor c = db.rawQuery(sql, null);
			while(c.moveToNext()){
				if(entity == null){
					entity = new SubsidenceTotalDataInfo();
				}
				entity.setType(type);
				iIndex = c.getColumnIndex("ID");
				entity.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("StationId");
				entity.setStationId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("ChainageId");
				entity.setChainageId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("SheetId");
				entity.setSheetId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("Coordinate");
				entity.setCoordinate(c.getString(iIndex));
				iIndex = c.getColumnIndex("PntType");
				entity.setPntType(c.getString(iIndex));
				iIndex = c.getColumnIndex("SurveyTime");
				entity.setSurveyTime(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("Info");
				entity.setInfo(c.getString(iIndex));
				iIndex = c.getColumnIndex("MEASNo");
				entity.setMEASNo(c.getShort(iIndex));
				iIndex = c.getColumnIndex("SurveyorID");
				entity.setSurveyorID(c.getInt(iIndex));
				iIndex = c.getColumnIndex("DataStatus");
				entity.setDataStatus(c.getShort(iIndex));
				iIndex = c.getColumnIndex("DataCorrection");
				entity.setDataCorrection(c.getFloat(iIndex));
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("GetSubsidenceTotalData:" + e.getLocalizedMessage());
		}
		return entity;
	}
}
