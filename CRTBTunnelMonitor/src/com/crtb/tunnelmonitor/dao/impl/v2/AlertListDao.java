
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

    public List<AlertList> queryAllRawSheetIndex() {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from AlertList";

        return mDatabase.queryObjects(sql, AlertList.class);
    }

    public AlertList queryOneById(int id) {
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from AlertList where ID=?";
        String[] args = new String[] { String.valueOf(id) };

        return mDatabase.queryObject(sql, args, AlertList.class);
    }

    public AlertList queryOne(int sheetId, int chainageId, String pntType, String originalDataID) {
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from AlertList where SheetID=? AND CrossSectionID=?"
                + " AND PntType=\'" + pntType + "\'" + " AND originalDataID=\'" + originalDataID
                + "\'";
        String[] args = new String[] { String.valueOf(sheetId), String.valueOf(chainageId) };

        return mDatabase.queryObject(sql, args, AlertList.class);
    }

    public int insertOrUpdate(TunnelSettlementTotalData point, int alertLevel, int Utype,
            double UValue, double UMax, String originalDataID) {

        int sheetId = point.getSheetId();
        int chainageId = point.getChainageId();

        String pntType = point.getPntType();

        if (pntType != null && pntType.contains("_")) {// such as "S1_1" or
                                                       // "S1_2"
            pntType = pntType.substring(0, pntType.indexOf("_"));
        }

        AlertList al = queryOne(sheetId, chainageId, pntType, originalDataID);

        if (al != null) {
            updatePointAlertItem(point, Utype, UValue, originalDataID);
            return al.getID();
        } else {
            return insertItem(point, alertLevel, Utype, UValue, UMax, originalDataID);
        }
    }

    public int insertOrUpdate(SubsidenceTotalData point, int alertLevel, int Utype, double UValue,
            double UMax, String originalDataID) {

        int sheetId = point.getSheetId();
        int chainageId = point.getChainageId();
        String pntType = point.getPntType();

        AlertList al = queryOne(sheetId, chainageId, pntType, originalDataID);

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

        Log.d(TAG, "AlertListDao insertItem");
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return -1;
        }

        AlertList al = new AlertList();
        String pntType = point.getPntType();

        if (pntType != null && pntType.contains("_")) {// such as "S1_1" or
                                                       // "S1_2"
            pntType = pntType.substring(0, pntType.indexOf("_"));
        }

        al.setSheetID(point.getSheetId());
        al.setCrossSectionID(point.getChainageId());
        al.setPntType(pntType);
        al.setAlertTime(point.getSurveyTime());
        al.setAlertLeverl(alertLevel);
        al.setUtype(Utype);
        al.setUValue(UValue);
        al.setUMax(UMax);
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

        Log.d(TAG, "AlertListDao insertItem");
        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return -1;
        }

        AlertList al = new AlertList();
        al.setSheetID(point.getSheetId());
        al.setCrossSectionID(point.getChainageId());
        al.setPntType(point.getPntType());
        al.setAlertTime(point.getSurveyTime());
        al.setAlertLeverl(alertLevel);
        al.setUtype(Utype);
        al.setUValue(UValue);
        al.setUMax(UMax);
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

        int sheetId = point.getSheetId();
        int chainageId = point.getChainageId();

        updatePointAlertItem(sheetId, chainageId, Utype, UValue, originalDataID);
    }

    public void updatePointAlertItem(SubsidenceTotalData point, int Utype, double UValue,
            String originalDataID) {

        int sheetId = point.getSheetId();
        int chainageId = point.getChainageId();

        updatePointAlertItem(sheetId, chainageId, Utype, UValue, originalDataID);
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
