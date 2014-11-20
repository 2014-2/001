package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.util.StringUtils;
import android.util.Log;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.entity.CrossSectionStopSurveying;

public final class CrossSectionStopSurveyingDao extends AbstractDao<CrossSectionStopSurveying> {

	private String TAG = "CrossSectionStopSurveyingDao: ";
	
	private static CrossSectionStopSurveyingDao _instance;

	private CrossSectionStopSurveyingDao() {
		
	}

	public static CrossSectionStopSurveyingDao defaultDao() {

		if (_instance == null) {
			_instance = new CrossSectionStopSurveyingDao();
		}

		return _instance;
	}

	public boolean getSectionStopState(String sectionGuid){
		final IAccessDatabase mDatabase = getCurrentDb();

		if (mDatabase == null) {
			return false;
		}
		
		if (sectionGuid == null || StringUtils.isEmpty(sectionGuid)) {
			Log.e(Constant.LOG_TAG_DAO,TAG +" 断面ID错误");
			return false;
		}
		
		String sql = "SELECT * FROM CrossSectionStopSurveying"
				   + " WHERE CrossSectionId = ?" 
				   + " LIMIT 1";
	
		CrossSectionStopSurveying sectionStop = mDatabase.queryObject(sql, new String[] { sectionGuid }, CrossSectionStopSurveying.class);
		if(sectionStop == null){
			return false;
		} else{
			//只有停测封存了的断面才会出现在这个表里面
			return true;
		}
	}


}
