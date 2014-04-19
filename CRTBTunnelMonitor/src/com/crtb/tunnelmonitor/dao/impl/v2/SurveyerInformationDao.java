package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import com.crtb.tunnelmonitor.entity.SurveyerInformation;

public class SurveyerInformationDao extends AbstractDao<SurveyerInformation> {

	private static SurveyerInformationDao _instance ;
	
	private SurveyerInformationDao(){
		
	}
	
	public static SurveyerInformationDao defaultDao(){
		
		if(_instance == null){
			_instance	= new SurveyerInformationDao() ;
		}
		
		return _instance ;
	}
	
	public void deleteAll(){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return ;
		}
		
		mDatabase.deleteAll(SurveyerInformation.class);
	}
	
	public SurveyerInformation querySurveyerByName(String name){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from SurveyerInformation where surveyerName = ?" ;
		
		return mDatabase.queryObject(sql, new String[]{name}, SurveyerInformation.class);
	}
	
	public List<SurveyerInformation> queryAllSurveyerInformation(){
		
		final IAccessDatabase mDatabase = getCurrentDb();
		
		if(mDatabase == null){
			return null ;
		}
		
		String sql = "select * from SurveyerInformation" ;
		
		return mDatabase.queryObjects(sql, SurveyerInformation.class);
	}
}
