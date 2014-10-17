package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.Date;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.util.DateUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.utils.AlertUtils;

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
    
    public List<AlertHandlingList> queryByAlertIdOrderByHandlingTimeAscAndNoUpload(int alertId) {
        final IAccessDatabase db = getCurrentDb();
        String alertGuid = AlertListDao.defaultDao().getGuidById(alertId);

        if (alertGuid == null || db == null) {
            return null;
        }


        String sql = "SELECT * from AlertHandlingList"
                + " WHERE"
                + " AlertID=?"
                + " And AlertStatus != 2"
                + " ORDER BY HandlingTime"
                ;

        return db.queryObjects(sql, new String[]{alertGuid}, AlertHandlingList.class);
    }
    
    public AlertHandlingList queryNoHandlingInfoByAlertId(AlertInfo alertInfo) {
        final IAccessDatabase db = getCurrentDb();
        String alertGuid = AlertListDao.defaultDao().getGuidById(alertInfo.getAlertId());

        if (alertGuid == null || db == null) {
            return null;
        }

        String sql = "SELECT Top 1 from AlertHandlingList"
                + " WHERE"
                + " AlertID=?"
                + " ORDER BY HandlingTime"
                ;

		AlertHandlingList ahl = db.queryObject(sql, new String[] { alertGuid },AlertHandlingList.class);
		if (ahl == null) {
			// 存在没有处理的详情的AlertList，自动添加一个处理详情
			ahl = new AlertHandlingList();
			ahl.setAlertID(alertGuid);
			ahl.setAlertStatus(AlertUtils.ALERT_STATUS_OPEN);
			ahl.setHandling(AlertUtils.POINT_DATASTATUS_NONE);
			ahl.setInfo("");
			ahl.setHandlingTime(DateUtils.toDate(alertInfo.getDate()));
			ahl.setDuePerson(alertInfo.getDuePerson() != null ? alertInfo.getDuePerson() : "");
			ahl.setHandlingInfo(false);
			db.saveObject(ahl);
			return ahl;
		} else {
			// 不存在没有处理详情的AlertListIndex
			return null;
		}
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

    public int insertIfNotExist(int alertId, int handling,String info, Date handlingTime, String duePerson,
            int alertStatus, int handlingInfo) {
        int id = -1;
        if (queryOne(alertId, alertStatus) == null) {
            id = insertItem(alertId, handling,info, handlingTime, duePerson, alertStatus, handlingInfo);
        }
        return id;
    }

    public int insertItem(int alertId, int handling,String info, Date handlingTime, String duePerson,
            int alertStatus, int handlingInfo) {
        Log.d(TAG, "AlertHandlingInfoDao insertItem");
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return -1;
        }
        
        if(info == null){
        	info = "";
        }

        String guid = AlertListDao.defaultDao().getGuidById(alertId);

        AlertHandlingList ah = new AlertHandlingList();
        AlertHandlingList old= queryOne(alertId, alertStatus);
        boolean needUpdate = false;
        if(old != null && (old.getInfo() == null || old.getInfo().equals(""))){
        	ah.setID(old.getID());
        	needUpdate = true;
        }
        ah.setAlertID(guid);
        ah.setInfo(info);
        //YX 正常保存handling
        ah.setHandling(handling);
        ah.setHandlingTime(handlingTime);
        ah.setDuePerson(duePerson);
        ah.setAlertStatus(alertStatus);
        ah.setHandlingInfo(false);
        int ret = 0;
        if(needUpdate){
        	ret = mDatabase.updateObject(ah);
        } else {
        	ret = mDatabase.saveObject(ah);
        }
        Log.d(TAG, "AlertHandlingInfoDao insertItem, ret: " + ret);
        return ret;
    }

    public void insertItems(List<AlertHandlingList> handings){
    	
    	 Log.d(TAG, "AlertHandlingInfoDao insertItem");
         final IAccessDatabase mDatabase = getCurrentDb();

         if (mDatabase == null) {
             return ;
         }

 		if(handings != null && handings.size() > 0){
        	mDatabase.saveObjectList(handings);
        }  	
 		Log.d(TAG, "AlertHandlingInfoDao insertItems End");
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
    
    public void updateUploadStatus(int id, int uploadStatus) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            String sql = "UPDATE AlertHandlingList"
                    + " SET UploadStatus=" + uploadStatus
                    + " WHERE ID=?";
            String[] args = new String[]{String.valueOf(id)};
            db.execute(sql, args);
        }
    }
}
