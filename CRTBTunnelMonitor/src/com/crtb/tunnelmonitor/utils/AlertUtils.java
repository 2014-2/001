
package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.AlertInfo;

public class AlertUtils {

    private static final String TAG = "AlertUtils";

    // 对应  AlertList 表中的OriginalDataID列, 一条测线两测点数据id间的分隔符
    public static final String ORIGINAL_ID_DIVIDER = ",";

    // 累计变形值阈值
    public static final double ACCUMULATIVE_THRESHOLD = 100; // mm

    // 变形速率阈值
    public static final double SPEED_THRESHOLD = 5; // mm/d

    // 0表示拱顶累计下沉值超限
    public static final int GONGDING_LEIJI_XIACHEN_EXCEEDING = 0;

    // 1表示拱顶下沉速率超限
    public static final int GONGDINGI_XIACHEN_SULV_EXCEEDING = 1;

    // 2表示收敛累计值超限
    public static final int SHOULIAN_LEIJI_EXCEEDING = 2;

    // 3表示收敛速率超限
    public static final int SHOULIAN_SULV_EXCEEDING = 3;

    // 4表示地表累计下沉值超限
    public static final int DIBIAO_LEIJI_XIACHEN_EXCEEDING = 4;

    // 5表示地表下沉速率超限
    public static final int DIBIAO_XIACHEN_SULV_EXCEEDING = 5;

    public static final String[] U_TYPE_MSGS = {"拱顶累计下沉值超限", "拱顶下沉速率超限", "收敛累计值超限", "收敛速率超限", "地表累计下沉值超限", "地表下沉速率超限"};

    public static final int ALERT_STATUS_HANDLED = 0;
    public static final int ALERT_STATUS_OPEN = 1;
    public static final int ALERT_STATUS_HANDLING = 2;
    public static final String[] ALERT_STATUS_MSGS = {"已消警", "开", "处理中"};

    public static String[] getPointSubsidenceExceedMsg(Object point) {
        Exceeding ex = checkPointSubsidenceExceed(point);
        return getMsgByExceeding(ex);
    }

    public static String[] getLineConvergenceExceedMsg(TunnelSettlementTotalData s_1,
            TunnelSettlementTotalData s_2) {
        Exceeding ex = checkLineConvergenceExceed(s_1, s_2);
        return getMsgByExceeding(ex);
    }

    private static String[] getMsgByExceeding(Exceeding ex) {
        StringBuilder sb1 = new StringBuilder();
        if (ex.leijiType >= 0) {
            sb1.append(U_TYPE_MSGS[ex.leijiType]).append(" ").append(ex.leijiValue).append("毫米");
        }

        StringBuilder sb2 = new StringBuilder();
        if (ex.sulvType >= 0) {
            sb2.append(U_TYPE_MSGS[ex.sulvType]).append(" ").append(ex.sulvValue).append("毫米/天");
        }

        return new String[] { sb1.toString(), sb2.toString() };
    }

    /**
     * 检测 断面拱顶 或 地表测点 的 累计沉降 或 单次沉降速率 是否超限.
     * 
     * @param point
     *            本次测量得到的测量点信息.
     *            因为TunnelSettlementTotalData和SubsidenceTotalData有完全相同的类结构定义,
     *            故这里用一个方法以避免代码重复。
     * @return error list; if null or empty, it means no exceeding.
     */
    public static Exceeding checkPointSubsidenceExceed(Object point) {
        Log.d(TAG, "checkPointSubsidenceExceed");
        Exceeding ret = new Exceeding();

        int type = 0;
        List pastInfoList = null;
        String[] thisCoords = null;
        Date thisTime = null;
        int chainageId = -1;
        String pntType = null;
        String originalDataID = null;

        if (point instanceof TunnelSettlementTotalData) {
            type = 1;
            TunnelSettlementTotalData tPoint = (TunnelSettlementTotalData) point;
            thisCoords = tPoint.getCoordinate().split(",");
            thisTime = tPoint.getSurveyTime();
            pastInfoList = TunnelSettlementTotalDataDao.defaultDao().queryInfoBeforeMEASNo(
                    tPoint.getChainageId(), tPoint.getPntType(), tPoint.getMEASNo());
            chainageId = ((TunnelSettlementTotalData) point).getChainageId();
            pntType = ((TunnelSettlementTotalData) point).getPntType();
            originalDataID = String.valueOf(((TunnelSettlementTotalData) point).getID());
        } else if (point instanceof SubsidenceTotalData) {
            type = 2;
            SubsidenceTotalData sPoint = (SubsidenceTotalData) point;
            thisCoords = sPoint.getCoordinate().split(",");
            thisTime = sPoint.getSurveyTime();
            pastInfoList = SubsidenceTotalDataDao.defaultDao().queryInfoBeforeMEASNo(
                    sPoint.getChainageId(), sPoint.getPntType(), sPoint.getMEASNo());
            chainageId = ((SubsidenceTotalData) point).getChainageId();
            pntType = ((SubsidenceTotalData) point).getPntType();
            originalDataID = String.valueOf(((SubsidenceTotalData) point).getID());
        } else {
            return null;
        }

        if (thisCoords == null || thisCoords.length < 3) {
            return null;
        }

        if (pastInfoList != null && pastInfoList.size() > 0) {

            Object firstInfo = pastInfoList.get(0);

            if (!TextUtils.isEmpty(thisCoords[2])) {
                double thisZ = Double.valueOf(thisCoords[2]);
                String[] firstCoords = null;
                if (type == 1) {
                    firstCoords = ((TunnelSettlementTotalData) firstInfo).getCoordinate()
                            .split(",");
                } else if (type == 2) {
                    firstCoords = ((SubsidenceTotalData) firstInfo).getCoordinate().split(",");
                }

                if (firstCoords != null && firstCoords.length == 3
                        && !TextUtils.isEmpty(firstCoords[2])) {
                    double firstZ = Double.valueOf(firstCoords[2]);
                    double accumulativeSubsidence = Math.abs(thisZ - firstZ);
                    Log.d(TAG, "累计沉降： " + accumulativeSubsidence);
                    if (accumulativeSubsidence >= ACCUMULATIVE_THRESHOLD) {
                        int uType = type == 1 ? GONGDING_LEIJI_XIACHEN_EXCEEDING
                                : DIBIAO_LEIJI_XIACHEN_EXCEEDING;
                        ret.leijiType = uType;
                        ret.leijiValue = accumulativeSubsidence;
                        int alertId = -1;
                        if (type == 1) {
                            alertId = AlertListDao.defaultDao().insertItem((TunnelSettlementTotalData) point, 3/* default */,
                                    uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
                        } else {
                            alertId = AlertListDao.defaultDao().insertItem((SubsidenceTotalData) point, 3/* default */,
                                    uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
                        }
                        AlertHandlingInfoDao.defaultDao().insertItem(alertId, null,
                                thisTime, String.valueOf(chainageId) + pntType, 1/*报警*/, 01/*true*/);
                    }
                }

                Object lastInfo = pastInfoList.get(pastInfoList.size() - 1);
                String[] lastCoords = null;
                Date lastTime = null;
                if (type == 1) {
                    lastCoords = ((TunnelSettlementTotalData) lastInfo).getCoordinate().split(",");
                    lastTime = ((TunnelSettlementTotalData) lastInfo).getSurveyTime();
                } else if (type == 2) {
                    lastCoords = ((SubsidenceTotalData) lastInfo).getCoordinate().split(",");
                    lastTime = ((SubsidenceTotalData) lastInfo).getSurveyTime();
                }

                if (lastCoords != null && lastCoords.length == 3
                        && !TextUtils.isEmpty(lastCoords[2]) && lastTime != null
                        && lastTime != null) {
                    double lastZ = Double.valueOf(lastCoords[2]);
                    double deltaZ = Math.abs(thisZ - lastZ);
                    long deltaT = Math.abs(thisTime.getTime() - lastTime.getTime());
                    double subsidenceSpeed = deltaZ / (deltaT / Time.DAY_MILLISECEND_RATIO);
                    Log.d(TAG, "沉降速率： " + subsidenceSpeed);
                    if (subsidenceSpeed >= SPEED_THRESHOLD) {
                        int uType = type == 1 ? GONGDINGI_XIACHEN_SULV_EXCEEDING
                                : DIBIAO_XIACHEN_SULV_EXCEEDING;
                        ret.sulvType = uType;
                        ret.sulvValue = subsidenceSpeed;
                        int alertId = -1;
                        if (type == 1) {
                            alertId = AlertListDao.defaultDao().insertItem((TunnelSettlementTotalData) point, 3/* default */,
                                    uType, subsidenceSpeed, SPEED_THRESHOLD, originalDataID);
                        } else {
                            alertId = AlertListDao.defaultDao().insertItem((SubsidenceTotalData) point, 3/* default */,
                                    uType, subsidenceSpeed, SPEED_THRESHOLD, originalDataID);
                        }
                        AlertHandlingInfoDao.defaultDao().insertItem(alertId, null,
                                thisTime, String.valueOf(chainageId) + pntType, 1/*报警*/, 01/*true*/);
                    }
                }
            }

        }
        return ret;
    }

    /**
     * @param s_1
     *            测线的其中一个端点
     * @param s_2
     *            测线的另一个端点
     * @return error list; if null or empty, it means no exceeding.
     */
    public static Exceeding checkLineConvergenceExceed(TunnelSettlementTotalData s_1,
            TunnelSettlementTotalData s_2) {
        Log.d(TAG, "checkLineConvergenceExceed");
        Exceeding ret = new Exceeding();

        String originalDataID = s_1.getID() + ORIGINAL_ID_DIVIDER + s_2.getID();

        double lineThisLength = getLineLength(s_1, s_2);
        Date s_1ThisTime = s_1.getSurveyTime();
        Date s_2ThisTime = s_2.getSurveyTime();
        Date thisTimeDate = s_1ThisTime;
        double thisTime = s_1ThisTime.getTime();
        if (s_2ThisTime.getTime() > thisTime) {
            thisTime = s_2ThisTime.getTime();
            thisTimeDate = s_2ThisTime;
        }

        int chainageId = s_1.getChainageId();

        List<TunnelSettlementTotalData> s_1InfoList = TunnelSettlementTotalDataDao.defaultDao()
                .queryInfoBeforeMEASNo(chainageId, s_1.getPntType(), s_1.getMEASNo());
        if (s_1InfoList != null && s_1InfoList.size() > 0) {
            TunnelSettlementTotalData s_1First = s_1InfoList.get(0);
            TunnelSettlementTotalData s_2First = TunnelSettlementTotalDataDao.defaultDao()
                    .queryOppositePointOfALine(s_1First, s_2.getPntType());
            double lineFirstLength = getLineLength(s_1First, s_2First);
            double convergence = Math.abs(lineFirstLength - lineThisLength);
            if (convergence >= ACCUMULATIVE_THRESHOLD) {
                int uType = SHOULIAN_LEIJI_EXCEEDING;
                ret.leijiType = uType;
                ret.leijiValue = convergence;
                int alertId = AlertListDao.defaultDao().insertItem(s_1, 3/* default */,
                        uType, convergence, ACCUMULATIVE_THRESHOLD, originalDataID);
                AlertHandlingInfoDao.defaultDao().insertItem(alertId, null,
                        thisTimeDate, String.valueOf(chainageId) + s_1.getPntType(), 1/*报警*/, 01/*true*/);
            }

            TunnelSettlementTotalData s_1Last = s_1InfoList.get(s_1InfoList.size() - 1);
            TunnelSettlementTotalData s_2Last = TunnelSettlementTotalDataDao.defaultDao()
                    .queryOppositePointOfALine(s_1Last, s_2.getPntType());
            double lineLastLength = getLineLength(s_1Last, s_2Last);
            double deltaLenth = Math.abs(lineLastLength - lineThisLength);
            Date s_1LastTime = s_1Last.getSurveyTime();
            Date s_2LastTime = s_2Last.getSurveyTime();
            double lastTime = s_1LastTime.getTime();
            if (s_2LastTime.getTime() > lastTime) {
                lastTime = s_2LastTime.getTime();
            }
            double deltaTime = Math.abs((thisTime - lastTime))
                    / Time.DAY_MILLISECEND_RATIO;
            double shoulianSpeed = deltaLenth / deltaTime;
            if (shoulianSpeed >= SPEED_THRESHOLD) {
                int uType = SHOULIAN_SULV_EXCEEDING;
                ret.sulvType = uType;
                ret.sulvValue = shoulianSpeed;
                int alertId = AlertListDao.defaultDao().insertItem(s_1, 3/* default */, uType, shoulianSpeed,
                        SPEED_THRESHOLD, originalDataID);
                AlertHandlingInfoDao.defaultDao().insertItem(alertId, null,
                        thisTimeDate, String.valueOf(chainageId) + s_1.getPntType(), 1/*报警*/, 1/*true*/);
            }
        }

        return ret;
    }

    public static double getLineLength(TunnelSettlementTotalData s_1, TunnelSettlementTotalData s_2) {

        String[] s_1cs = s_1.getCoordinate().split(",");
        String[] s_2cs = s_2.getCoordinate().split(",");
        if (s_1cs == null || s_1cs.length < 3 || s_2cs == null || s_2cs.length < 3) {
            return 0;
        }

        // 空间勾股定理计算两点之间测线长度
        double squar = Math.pow(Double.valueOf(s_1cs[0]) - Double.valueOf(s_2cs[0]), 2)// x
                + Math.pow(Double.valueOf(s_1cs[1]) - Double.valueOf(s_2cs[1]), 2)// y
                + Math.pow(Double.valueOf(s_1cs[2]) - Double.valueOf(s_2cs[2]), 2);// z
        return Math.sqrt(squar);
    }

    public static ArrayList<AlertInfo> getAlertInfoList() {
        Log.d(TAG, "getAlertInfoList");

//        AlertListDao.defaultDao().createTable();
//        AlertHandlingInfoDao.defaultDao().createTable();

        ArrayList<AlertInfo> l = new ArrayList<AlertInfo>();
        String sql = "SELECT"
                + " AlertList.ID AS alertId,"
                + " AlertHandlingList.ID AS alertHandlingId,"
                + " TunnelCrossSectionIndex.sectionName AS sectionName,"
                + " AlertList.AlertTime AS date,"
                + " AlertList.PntType AS pntType,"
                + " AlertList.Utype AS utype,"
                + " AlertHandlingList.AlertStatus AS status"
                + " FROM AlertList LEFT JOIN AlertHandlingList"
                + " ON AlertList.ID=AlertHandlingList.AlertID"
                + " LEFT JOIN TunnelCrossSectionIndex"
                + " ON AlertList.CrossSectionID=TunnelCrossSectionIndex.ID"
                + " ORDER BY"
                + " CASE AlertHandlingList.AlertStatus"
                + "   WHEN 1 THEN 0"
                + "   WHEN 2 THEN 1"
                + "   WHEN 0 THEN 2"
                + " END DESC,"
                + " AlertList.AlertTime DESC";

        Cursor c = null;
        try {
            c = AlertListDao.defaultDao().executeQuerySQL(sql, null);

            if (c != null) {
                Log.d(TAG, "cursor count: " + c.getCount());
                while (c.moveToNext()) {
                    AlertInfo ai = new AlertInfo();
                    ai.setAlertId(c.getInt(0));
                    ai.setAlertHandlingId(c.getInt(1));
                    ai.setXinghao(c.getString(2));
                    String dateStr = c.getString(3);
                    ai.setDate(dateStr);
                    ai.setPntType(c.getString(4));
                    int uType = c.getInt(5);
                    ai.setUType(uType);
                    ai.setUTypeMsg((uType >= 0 && uType < 6) ? U_TYPE_MSGS[uType] : "");
                    int alertStatus = c.getInt(6);
                    ai.setAlertStatus(alertStatus);
                    ai.setAlertStatusMsg((alertStatus >= 0 && alertStatus < 3) ? ALERT_STATUS_MSGS[alertStatus]
                            : "");
                    l.add(ai);
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getAlertInfoList", e);
        }  finally {
            if (c != null) {
                c.close();
            }
        }
        return l;
    }

    public static int getAlertCountOfState(int state) {

        Log.d(TAG, "getAlertCountOfState");

        // AlertListDao.defaultDao().createTable();
        // AlertHandlingInfoDao.defaultDao().createTable();
        String sql = "SELECT * FROM AlertList INNER JOIN AlertHandlingList"
                + " ON AlertList.ID=AlertHandlingList.AlertID"
                + " WHERE AlertStatus=?";
        String[] args = new String[] { String.valueOf(state) };
        int count = 0;
        Cursor c = null;
        try {
            c = AlertListDao.defaultDao().executeQuerySQL(sql, args);
            if (c != null) {
                count = c.getCount();
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getAlertCountOfState", e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        Log.d(TAG, "getAlertCountOfState, return count: " + count);
        return count;
    }

    /**
     * 在UI处理Alert时，调用本方法
     * 
     * @param alertId
     *            处理的Alert在数据库中的ID
     * @param dataStatus
     *            要更新的TunnelSettlementTotalData表或SubsidenceTotalData表的DataStatus列
     * @param correction
     *            要更新的TunnelSettlementTotalData表或SubsidenceTotalData表的DataCorrection列
     * @param alertStatus
     *            要新插入AlertHandlingList表的AlertStatus列
     * @param handling
     *            要新插入AlertHandlingList表的Handling列
     * @param handlingTime
     *            要新插入AlertHandlingList表的HandlingTime列
     */
    public static void handleAlert(int alertId, int dataStatus, float correction, int alertStatus,
            String handling, Date handlingTime) {
        AlertList al = AlertListDao.defaultDao().queryOneById(alertId);
        String originalID = al.getOriginalDataID();
        if (!TextUtils.isEmpty(originalID)) {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            if (originalID.contains(ORIGINAL_ID_DIVIDER)) {
                String[] idStrs = originalID.split(ORIGINAL_ID_DIVIDER);
                for (String idStr : idStrs) {
                    ids.add(Integer.valueOf(idStr));
                }
            } else {
                ids.add(Integer.valueOf(originalID));
            }

            if (ids.size() == 1) {
                TunnelSettlementTotalDataDao.defaultDao().updateDataStatus(ids.get(0), dataStatus);
            } else {
                for (int id : ids) {
                    SubsidenceTotalDataDao.defaultDao().updateDataStatus(id, dataStatus);
                }
            }
        }

        String duePerson = String.valueOf(al.getCrossSectionID()) + al.getPntType();
        AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, handlingTime, duePerson,
                alertStatus, 1/* true */);
    }

    public static class Exceeding {
        int leijiType = -1;
        int sulvType = -1;
        double leijiValue = -1;
        double sulvValue = -1;
    }
}
