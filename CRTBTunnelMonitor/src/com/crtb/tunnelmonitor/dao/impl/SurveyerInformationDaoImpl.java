package com.crtb.tunnelmonitor.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.dao.SurveyerInformationDao;
import com.crtb.tunnelmonitor.db.SqliteHelperDTMS;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;

/**
 * 测量人员信息数据库接口实现
 */
public class SurveyerInformationDaoImpl implements SurveyerInformationDao {
	private final String TABLE = "SurveyerInformation";

	public static final String NAME = "SurveyerName";

	public static final String NOTE = "Info";

	private SqliteHelperDTMS helper = null;
	private SQLiteDatabase db = null;

	public static SurveyerInformationDaoImpl instance;

	public static SurveyerInformationDaoImpl getInstance() {
		if (instance == null) {
			instance = new SurveyerInformationDaoImpl(
					AppCRTBApplication.getInstance());
		}
		return instance;
	}

	private SurveyerInformationDaoImpl(Context c) {
		helper = new SqliteHelperDTMS(c);
		db = helper.getReadableDatabase();

	}

	/** 查询全部 */
	public List<SurveyerInformation> SelectAllSurveyerInfo() {
		List<SurveyerInformation> list = null;
		SurveyerInformation entity = null;
		int iIndex = 0;
		String sql = "select * from SurveyerInformation";
		try {
			Cursor c = db.rawQuery(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
					entity = new SurveyerInformation();
					iIndex = c.getColumnIndexOrThrow("Id");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("SurveyerName");
					entity.setSurveyerName(c.getString(iIndex));
					iIndex = c.getColumnIndex("CertificateID");
					entity.setCertificateID(c.getString(iIndex));
					iIndex = c.getColumnIndex("Password");
					entity.setPassword(c.getString(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));
					iIndex = c.getColumnIndex("ProjectID");
					entity.setProjectID(c.getInt(iIndex));
					if (list == null) {
						list = new ArrayList<SurveyerInformation>();
					}
					list.add(entity);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectAllSurveyerInfo:"
					+ e.getLocalizedMessage());
		}
		return list;
	}

	public List<String> getFieldsByName(String fieldName) {
		Cursor cursor = db.query(TABLE, new String[] { fieldName }, null, null,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			List<String> list = new ArrayList<String>();
			do {
				String name = cursor
						.getString(cursor.getColumnIndex(fieldName));
				if (!TextUtils.isEmpty(name)) {
					list.add(name);
				}
			} while (cursor.moveToNext());
			cursor.close();
			return list;
		}
		return null;
	}

	/** 新建测量人员 */
	public Boolean InsertSurveyerInfo(SurveyerInformation s) {
		boolean result = false;
		if (s == null) {
			return result;
		}
		String sql = "insert into SurveyerInformation(SurveyerName,CertificateID,Password,Info,ProjectID)"
				+ " values(?,?,?,?,?)";
		try {
			Object[] obj = { s.getSurveyerName(), s.getCertificateID(),
					s.getPassword(), s.getInfo(), s.getProjectID() };
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("InsertSurveyerInfo:" + e.getLocalizedMessage());
		}
		return result;
	}

	public String getIdCardByName(String card) {
		Cursor cursor = db.query(TABLE, new String[] { "SurveyerName" }, null,
				null, null, null, null);
		// Cursor cursor=db.query(TABLE, new String[]{"SurveyerName"},
		// "CertificateID='"+card+"'", null, null, null, null);
		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			return cursor.getString(cursor.getColumnIndex("SurveyerName"));
		}
		return null;
	}

	/** 查询测量人员 */
	public SurveyerInformation SelectSurveyerInfo(int id) {
		SurveyerInformation entity = null;
		String sql = "select * from SurveyerInformation where id="
				+ Integer.toString(id);
		int iIndex = 0;
		try {
			Cursor c = db.rawQuery(sql, null);
			if (c != null) {
				while (c.moveToNext()) {
					if (entity == null) {
						entity = new SurveyerInformation();
					}
					iIndex = c.getColumnIndex("ID");
					entity.setId(c.getInt(iIndex));
					iIndex = c.getColumnIndex("SurveyerName");
					entity.setSurveyerName(c.getString(iIndex));
					iIndex = c.getColumnIndex("CertificateID");
					entity.setCertificateID(c.getString(iIndex));
					iIndex = c.getColumnIndex("Password");
					entity.setPassword(c.getString(iIndex));
					iIndex = c.getColumnIndex("Info");
					entity.setInfo(c.getString(iIndex));
					iIndex = c.getColumnIndex("ProjectID");
					entity.setProjectID(c.getInt(iIndex));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("SelectSurveyerInfo:" + e.getLocalizedMessage());
		}
		return entity;
	}

	/** 删除测量人员 */
	public Boolean DeleteSurveyerInfo(int id) {
		boolean result = false;
		String sql = "delete from SurveyerInformation where id=?";
		try {
			Object[] obj = { id };
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("DeleteSurveyerInfo:" + e.getLocalizedMessage());
		}
		return result;
	}

	/** 编辑测量人员 */
	public Boolean UpdateSurveyerInfo(SurveyerInformation s) {
		boolean result = false;
		if (s == null) {
			return result;
		}
		String sql = "update SurveyerInformation set SurveyerName=?,CertificateID=?,"
				+ "Password=?,Info=?,ProjectID=? where id =?";
		try {
			Object[] obj = { s.getSurveyerName(), s.getCertificateID(),
					s.getPassword(), s.getInfo(), s.getPassword() };
			db.execSQL(sql, obj);
			result = true;
		} catch (Exception e) {
			// TODO: handle exception
			result = false;
			System.out.println("UpdateSurveyerInfo:" + e.getLocalizedMessage());
		}
		return result;
	}

	@Override
	public void deleteAll() {
		String sql = "delete from " + TABLE;
	}

}
