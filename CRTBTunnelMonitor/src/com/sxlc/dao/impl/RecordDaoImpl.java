package com.sxlc.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sxlc.dao.RecordDao;
import com.sxlc.db.SqliteHelperDTMS;
import com.sxlc.entity.RecordInfo;
import com.sxlc.entity.SubsidenceCrossSectionInfo;
import com.sxlc.entity.SubsidenceTotalDataInfo;
import com.sxlc.entity.TunnelCrossSectionInfo;
import com.sxlc.entity.WorkInfos;
/**
 * 记录单数据库实现
 *创建时间：2014-3-21下午10:14:28
 *@author 张涛
 *@since JDK1.6
 *@version 1.0
 */
public class RecordDaoImpl implements RecordDao {

	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;

	public RecordDaoImpl(Context c,String name) {
		helper = new SqliteHelperDTMS(c, name,null,0);
		db = helper.getReadableDatabase();

	}
	@Override
	public List<RecordInfo> RecordAll(int type) {
		List<RecordInfo> list = new ArrayList<RecordInfo>();
		RecordInfo r = null;
		int iIndex = 0;
		String sql = "select * from RawSheetIndex CrossSectionType=" + Integer.toString(type);
		try {
			Cursor c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				r =new RecordInfo();
				iIndex = c.getColumnIndex("ID");
				r.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("CrossSectionType");
				r.setCrossSectionType(c.getInt(iIndex));
				iIndex = c.getColumnIndex("Info");
				r.setInfo(c.getString(iIndex));
				iIndex = c.getColumnIndex("FACEDK");
				r.setFacedk(c.getDouble(iIndex));
				iIndex = c.getColumnIndex("TEMPERATURE");
				r.setTemperature(c.getDouble(iIndex));
				iIndex = c.getColumnIndex("CrossSectionIDs");
				r.setCrossSectionIDs(c.getString(iIndex));
				iIndex = c.getColumnIndex("CreateTime");
				r.setCreateTime(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("FACEDESCRIPTION");
				r.setFacedescription(c.getString(iIndex));
				list.add(r);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	@Override
	public Boolean AddRecord(RecordInfo r) {
		boolean result = false;
		if(r == null){
			return result;
		}
		try {
			String sql = "insert into RawSheetIndex(CrossSectionType,CreateTime,FACEDK,FACEDESCRIPTION,TEMPERATURE,CrossSectionIDs)" +
					" values(?,?,?,?,?,?)";
			Object[] obj = {r.getCrossSectionType(),r.getCreateTime(),r.getFacedk(),r.getFacedescription(),r.getTemperature(),r.getCrossSectionIDs()};
			db.execSQL(sql,obj);
			result = true;
		} catch (Exception e) {
			result = false;
			System.out.println("AddRecord:" + e.getLocalizedMessage());
		}
		//return false;
		return result;

	}

	@Override
	public RecordInfo SelectRecord(int id) {
		RecordInfo entity = null;
		String sql = "select * from RawSheetIndex where id=" + Integer.toString(id);
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if(c!=null){
				while(c.moveToNext()){
					if(entity == null){
						entity = new RecordInfo();
					}
					iIndex = c.getColumnIndex("ID");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("CrossSectionType");
					entity.setCrossSectionType(c.getInt(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));
					iIndex = c.getColumnIndex("FACEDK");
					entity.setFacedk(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("TEMPERATURE");
					entity.setTemperature(c.getDouble(iIndex));
					iIndex = c.getColumnIndex("CrossSectionIDs");
					entity.setCrossSectionIDs(c.getString(iIndex));
					iIndex = c.getColumnIndex("CreateTime");
					entity.setCreateTime(Timestamp.valueOf(c.getString(iIndex)));
					iIndex = c.getColumnIndex("FACEDESCRIPTION");
					entity.setFacedescription(c.getString(iIndex));
				}
			}
		} catch (Exception e) {
			System.out.println("SelectRecord:" + e.getLocalizedMessage());
		}
		return entity;
	}

	@Override
	public Boolean DeleteRecord(int id) {
		boolean result = false;
		String sql = "delete from TunnelSettlementTotalData where SheetId=?";
		try {
			Object[] obj = {id};
			db.execSQL(sql, obj);
			sql = "delete from SubsidenceTotalData where SheetId=?";
			db.execSQL(sql, obj);
			sql = "delete from RawSheetIndex where id = ?";
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			result = false;
			System.out.println("DeleteRecord:" + e.getLocalizedMessage());
		}
		return result;
	}

	@Override
	public Boolean UpdateRecord(RecordInfo r) {
		boolean result = false;
		if(r == null){
			return result;
		}
		String sql = "update RawSheetIndex set CrossSectionType=?,FACEDK=?,FACEDESCRIPTION=?,TEMPERATURE=?,CrossSectionIDs=? where Id=?";
		try {
			Object[] obj = {r.getCrossSectionType(),r.getFacedk(),r.getFacedescription(),r.getTemperature(),r.getCrossSectionIDs(),r.getId()};
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			result = false;
			System.err.println("UpdateRecord:" + e.getLocalizedMessage());
		}
		return result;
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
	public String GetSectionName(WorkInfos w, RecordInfo Value) {
		
		int iChainage = (int)Value.getFacedk().doubleValue();
		String sName = w.getChainagePrefix();
		int iDiv = 0,iMod = 0;
		iDiv = iChainage / 1000;
		iMod = iChainage % 1000;
		double dMod = (double)iMod+(Value.getFacedk().doubleValue()-(double)iChainage);
		
		if (iDiv > 0) {
			sName += Integer.toString(iDiv);
		}
		sName += '+';
		sName += Double.toString(dMod);
		
		return sName;
	}

	@Override
	public void GetRecordList(int type,WorkInfos w,List<RecordInfo> list) {
		if(list == null)
		{
			list = new ArrayList<RecordInfo>();
		}
		else
		{
			list.clear();
		}
		RecordInfo r = null;
		int iIndex = 0;
		String sql = "select * from RawSheetIndex where CrossSectionType=" + Integer.toString(type);
		try {
			Cursor c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				r =new RecordInfo();
				iIndex = c.getColumnIndex("Id");
				r.setId(c.getInt(iIndex));
				iIndex = c.getColumnIndex("CrossSectionType");
				r.setCrossSectionType(c.getInt(iIndex));
				iIndex = c.getColumnIndex("Info");
				r.setInfo(c.getString(iIndex));
				iIndex = c.getColumnIndex("FACEDK");
				r.setFacedk(c.getDouble(iIndex));
				iIndex = c.getColumnIndex("TEMPERATURE");
				r.setTemperature(c.getDouble(iIndex));
				iIndex = c.getColumnIndex("CrossSectionIDs");
				r.setCrossSectionIDs(c.getString(iIndex));
				iIndex = c.getColumnIndex("CreateTime");
				r.setCreateTime(Timestamp.valueOf(c.getString(iIndex)));
				iIndex = c.getColumnIndex("FACEDESCRIPTION");
				r.setFacedescription(c.getString(iIndex));
				List<SubsidenceTotalDataInfo> sublist = GetAllSubsidenceTotalData(-1,-1,r.getId(),r.getCrossSectionType());
				//r.setSectionlist(sublist);
				r.setChainageName(GetSectionName(w,r));
				list.add(r);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Override
	public Boolean AddRecord(String type, RecordInfo r) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void SelectRecord(RecordInfo r) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void DeleteRecord(String name) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void CompileRecord(RecordInfo r) {
		// TODO Auto-generated method stub
		
	}
	

}
