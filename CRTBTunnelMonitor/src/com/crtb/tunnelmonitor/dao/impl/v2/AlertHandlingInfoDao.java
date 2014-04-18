package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.Date;

import com.crtb.tunnelmonitor.entity.AlertHandlingList;

public class AlertHandlingInfoDao extends AbstractDao<AlertHandlingList> {

	private static AlertHandlingInfoDao _instance ;
	
	private AlertHandlingInfoDao(){
		
	}
	
	public static AlertHandlingInfoDao defaultDao(){
		
		if(_instance == null){
			_instance	= new AlertHandlingInfoDao() ;
		}
		
		return _instance ;
	}

    public int insertItem(int alertId, int handling, Date handlingTime, String duePerson,
            int alertStatus, int handlingInfo) {
        AlertHandlingList ah = new AlertHandlingList();
        ah.setAlertID(alertId);
        ah.setHandling(handling);
        ah.setHandlingTime(handlingTime);
        ah.setDuePerson(duePerson);
        ah.setAlertStatus(alertStatus);
        ah.setHandlingInfo(handlingInfo);
        return mDatabase.saveObject(ah);
    }
}
