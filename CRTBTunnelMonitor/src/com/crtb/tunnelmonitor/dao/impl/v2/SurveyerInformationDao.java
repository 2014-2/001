package com.crtb.tunnelmonitor.dao.impl.v2;

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
}
