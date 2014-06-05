package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.Date;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertList;

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

    public void createTable() {
        final IAccessDatabase db = getCurrentDb();

        if(db == null){
            return;
        }
        db.createTable(AlertHandlingList.class);
    }

    public List<AlertHandlingList> queryByAlertIdOrderByHandlingTimeDesc(int alertId) {
        final IAccessDatabase db = getCurrentDb();
        String alertGuid = AlertListDao.defaultDao().getGuidById(alertId);

        if (alertGuid == null || db == null) {
            return null;
        }


        String sql = "SELECT * from AlertHandlingList"
                + " WHERE"
                + " AlertID=?"
                + " ORDER BY HandlingTime DESC, ID DESC"
                ;

        return db.queryObjects(sql, new String[]{alertGuid}, AlertHandlingList.class);
    }

    public AlertHandlingList queryOne(int alertId,
            int alertStatus) {
        String alertGuid = AlertListDao.defaultDao().getGuidById(alertId);

        final IAccessDatabase db = getCurrentDb();

        if (db == null) {
            return null;
        }

        String sql = "SELECT * from AlertHandlingList WHERE"
                + " AlertID=?"
                + " AND AlertStatus=?";

        String[] args = new String[] {
                alertGuid, String.valueOf(alertStatus)
        };

        return db.queryObject(sql, args, AlertHandlingList.class);
    }

    public int insertIfNotExist(int alertId, String handling, Date handlingTime, String duePerson,
            int alertStatus, int handlingInfo) {
        int id = -1;
        if (queryOne(alertId, alertStatus) == null) {
            id = insertItem(alertId, handling, handlingTime, duePerson, alertStatus, handlingInfo);
        }
        return id;
    }

    public int insertItem(int alertId, String handling, Date handlingTime, String duePerson,
            int alertStatus, int handlingInfo) {
        Log.d(TAG, "AlertHandlingInfoDao insertItem");
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return -1;
        }

        String guid = AlertListDao.defaultDao().getGuidById(alertId);

        AlertHandlingList ah = new AlertHandlingList();
        ah.setAlertID(guid);
        ah.setHandling(handling);
        ah.setHandlingTime(handlingTime);
        ah.setDuePerson(duePerson);
        ah.setAlertStatus(alertStatus);
        ah.setHandlingInfo(handlingInfo == 1);

        int ret = mDatabase.saveObject(ah);
        Log.d(TAG, "AlertHandlingInfoDao insertItem, ret: " + ret);
        return ret;
    }

    public void deleteItemById(int id) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            String sql = "DELETE FROM AlertHandlingList"
                    + " WHERE ID=?";
            String[] args = new String[]{String.valueOf(id)};
            db.execute(sql, args);
        }
    }

    public void deleteByAlertId(int alertId) {
        String alertGuid = AlertListDao.defaultDao().getGuidById(alertId);
        IAccessDatabase db = getCurrentDb();
        if (alertGuid != null && db != null) {
            String sql = "DELETE FROM AlertHandlingList"
//                    + " WHERE AlertID=\'" + alertGuid + "\'";
                      + " WHERE AlertID=?";
            String[] args = new String[]{alertGuid};
            db.execute(sql, args);
        }
    }

    public void updateAlertStatus(int id, int alertStatus) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            String sql = "UPDATE AlertHandlingList"
                    + " SET AlertStatus=" + alertStatus
                    + " WHERE ID=?";
            String[] args = new String[]{String.valueOf(id)};
            db.execute(sql, args);
        }
    }
}
