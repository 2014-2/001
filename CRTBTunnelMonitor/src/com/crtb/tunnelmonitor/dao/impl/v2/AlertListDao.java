
package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import android.database.Cursor;
import android.util.Log;

import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class AlertListDao extends AbstractDao<AlertList> {

    private static AlertListDao _instance;

    private AlertListDao() {

    }

    public static AlertListDao defaultDao() {

        if (_instance == null) {
            _instance = new AlertListDao();
        }

        return _instance;
    }

    public void createTable() {
        final IAccessDatabase db = getCurrentDb();

        if (db == null) {
            return;
        }
        db.createTable(AlertList.class);
    }

    public List<AlertList> queryAll() {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from AlertList";

        return mDatabase.queryObjects(sql, AlertList.class);
    }

    public AlertList queryOneById(int id) {
        Log.d(TAG, "AlertListDao queryOneById, id: " + id);

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from AlertList where ID=?";
        String[] args = new String[] { String.valueOf(id) };

        return mDatabase.queryObject(sql, args, AlertList.class);
    }

    public void deleteAlert(int sheetId, int chainageId, String originalDataID, int uType) {
        final IAccessDatabase mDatabase = getCurrentDb();
        Log.d(TAG, "AlertListDao deleteAlert, uType: " + uType);

        if (mDatabase == null) {
            return;
        }

        String sql = "delete from AlertList where SheetID=? AND CrossSectionID=?" + " AND Utype=?"
                + " AND originalDataID=\'" + originalDataID + "\'";
        String[] args = new String[] { String.valueOf(sheetId), String.valueOf(chainageId),
                String.valueOf(uType) };

        mDatabase.execute(sql, args);

    }

    public AlertList queryOne(int sheetId, int chainageId, String originalDataID, int uType) {
        final IAccessDatabase mDatabase = getCurrentDb();
        Log.d(TAG, "AlertListDao queryOne, uType: " + uType);

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from AlertList where SheetID=? AND CrossSectionID=?"
                + " AND Utype=?"
                + " AND originalDataID=\'" + originalDataID + "\'";
        String[] args = new String[] { String.valueOf(sheetId), String.valueOf(chainageId), String.valueOf(uType) };

        return mDatabase.queryObject(sql, args, AlertList.class);
    }

    public int insertOrUpdate(TunnelSettlementTotalData point, int alertLevel, int Utype,
            double UValue, double UMax, String originalDataID) {

        Log.d(TAG, "AlertListDao insertOrUpdate TunnelSettlementTotalData");
        int sheetId = Integer.valueOf(point.getSheetId());
        int chainageId = Integer.valueOf(point.getChainageId());

//        String pntType = point.getPntType();

//        if (pntType != null && pntType.length() > 2) {// such as "S1-1" or
//                                                       // "S1-2"
//            Log.d(TAG, "AlertListDao insertOrUpdate, pntType.contains(-)");
//            pntType = pntType.substring(0, pntType.length() - 2);
//        }

        AlertList al = queryOne(sheetId, chainageId, originalDataID, Utype);

        if (al != null) {
            updatePointAlertItem(point, Utype, UValue, originalDataID);
            return al.getID();
        } else {
            return insertItem(point, alertLevel, Utype, UValue, UMax, originalDataID);
        }
    }

    public int insertOrUpdate(SubsidenceTotalData point, int alertLevel, int Utype, double UValue,
            double UMax, String originalDataID) {
        Log.d(TAG, "AlertListDao insertOrUpdate SubsidenceTotalData");

        int sheetId = Integer.valueOf(point.getSheetId());
        int chainageId = Integer.valueOf(point.getChainageId());
//        String pntType = point.getPntType();

        AlertList al = queryOne(sheetId, chainageId, originalDataID, Utype);

        if (al != null) {
            updatePointAlertItem(point, Utype, UValue, originalDataID);
            return al.getID();
        } else {
            return insertItem(point, alertLevel, Utype, UValue, UMax, originalDataID);
        }
    }

    /**
     * @param point
     *            产生预警信息的那次测量的测量点信息
     * @param alertLevel
     * @param Utype
     * @param UValue
     * @param UMax
     * @param originalDataID
     * @return
     */
    public int insertItem(TunnelSettlementTotalData point, int alertLevel, int Utype,
            double UValue, double UMax, String originalDataID) {

        Log.d(TAG, "AlertListDao insertItem TunnelSettlementTotalData");
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return -1;
        }

        AlertList al = new AlertList();
        String pntType = point.getPntType();

        if (pntType != null && pntType.length() > 2) {// such as "S1-1" or
                                                       // "S1-2"
            Log.d(TAG, "AlertListDao insertItem, pntType.contains(-)");
            pntType = pntType.substring(0, pntType.length() - 2);
        }

        al.setSheetID(Integer.valueOf(point.getSheetId()));
        al.setCrossSectionID(Integer.valueOf(point.getChainageId()));
        al.setPntType(pntType);
        al.setAlertTime(point.getSurveyTime());
        al.setAlertLeverl(alertLevel);
        al.setUtype(Utype);
        al.setUValue(UValue);
        al.setUMax((int)UMax);
        al.setOriginalDataID(originalDataID);

        int ret = mDatabase.saveObject(al);
        Log.d(TAG, "AlertListDao insertItem, ret: " + ret);
        return ret;
    }

    /**
     * @param point
     *            产生预警信息的那次测量的测量点信息
     * @param alertLevel
     * @param Utype
     * @param UValue
     * @param UMax
     * @param originalDataID
     * @return
     */
    public int insertItem(SubsidenceTotalData point, int alertLevel, int Utype, double UValue,
            double UMax, String originalDataID) {

        Log.d(TAG, "AlertListDao insertItem SubsidenceTotalData");
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return -1;
        }

        AlertList al = new AlertList();
        al.setSheetID(Integer.valueOf(point.getSheetId()));
        al.setCrossSectionID(Integer.valueOf(point.getChainageId()));
        al.setPntType(point.getPntType());
        al.setAlertTime(point.getSurveyTime());
        al.setAlertLeverl(alertLevel);
        al.setUtype(Utype);
        al.setUValue(UValue);
        al.setUMax((int)UMax);
        al.setOriginalDataID(originalDataID);

        int ret = mDatabase.saveObject(al);
        Log.d(TAG, "AlertListDao insertItem, ret: " + ret);
        return ret;
    }

    public void updatePointAlertItem(int sheetId, int chainageId, int Utype, double UValue,
            String originalDataID) {
        IAccessDatabase db = getCurrentDb();
        if (db == null) {
            return;
        }

        String sql = "UPDATE AlertList" + " SET UValue=" + UValue
                + " WHERE SheetID=? AND CrossSectionID=?" + " AND Utype=" + Utype
                + " AND originalDataID=\'" + originalDataID + "\'";

        String[] args = new String[] { String.valueOf(sheetId), String.valueOf(chainageId) };

        db.execute(sql, args);
    }

    public void updatePointAlertItem(TunnelSettlementTotalData point, int Utype, double UValue,
            String originalDataID) {
        Log.d(TAG, "AlertListDao updatePointAlertItem TunnelSettlementTotalData");
        int sheetId = Integer.valueOf(point.getSheetId());
        int chainageId = Integer.valueOf(point.getChainageId());

        updatePointAlertItem(sheetId, chainageId, Utype, UValue, originalDataID);
    }

    public void updatePointAlertItem(SubsidenceTotalData point, int Utype, double UValue,
            String originalDataID) {
        Log.d(TAG, "AlertListDao updatePointAlertItem SubsidenceTotalData");
        int sheetId = Integer.valueOf(point.getSheetId());
        int chainageId = Integer.valueOf(point.getChainageId());

        updatePointAlertItem(sheetId, chainageId, Utype, UValue, originalDataID);
    }

    public List<AlertList> queryTunnelSettlementAlertsByOriginalDataId(int originalDataId) {
        IAccessDatabase db = getCurrentDb();
        if (db == null) {
            return null;
        }
        String oid = String.valueOf(originalDataId);
        String sql = "select * from AlertList where (OriginalDataID = \'"
                + oid + "\'"
                + " OR OriginalDataID LIKE \'" + oid + ",%\'"
                + " OR OriginalDataID LIKE \'%," + oid + "\')"
                + " AND Utype<=3";//隧道内
        return db.queryObjects(sql, AlertList.class);
    }

    public List<AlertList> queryGroundSubsidenceAlertsByOriginalDataId(int originalDataId) {
        IAccessDatabase db = getCurrentDb();
        if (db == null) {
            return null;
        }
        String sql = "select * from AlertList where OriginalDataID = ?"
                + " AND Utype>=4";//地表下沉
        String[] args = new String[] {String.valueOf(originalDataId)};
        return db.queryObjects(sql, args, AlertList.class);
    }

    public Cursor executeQuerySQL(String sql, String[] args) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            Cursor c = db.executeQuerySQL(sql, args);
            return c;
        }
        return null;
    }

}
