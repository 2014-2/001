
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

    public static final int POINT_DATASTATUS_NONE = 0;
    public static final int POINT_DATASTATUS_DISCARD = 1;
    public static final int POINT_DATASTATUS_AS_FIRSTLINE = 2;
    public static final int POINT_DATASTATUS_CORRECTION = 3;

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
        if (ex == null) {
            return null;
        }
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

        float sumOfDataCorrection = 0f;
        float thisDataCorrection = 0f;

        if (point instanceof TunnelSettlementTotalData) {
            type = 1;
            TunnelSettlementTotalData tPoint = (TunnelSettlementTotalData) point;
            thisCoords = tPoint.getCoordinate().split(",");
            thisTime = tPoint.getSurveyTime();
            pastInfoList = TunnelSettlementTotalDataDao.defaultDao().queryInfoBeforeMEASNo(
                    tPoint.getChainageId(), tPoint.getPntType(), tPoint.getMEASNo());
            sumOfDataCorrection = calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(pastInfoList);
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
            sumOfDataCorrection = calculateSumOfDataCorrectionsOfSubsidenceTotalDatas(pastInfoList);
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
                Log.d(TAG, "this z: " + thisZ + " m");
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
                    Log.d(TAG, "first z:" + firstZ + " m");
                    double accumulativeSubsidence = thisZ - firstZ;
                    accumulativeSubsidence *= 1000;//CHANGE TO MILLIMETER
                    Log.d(TAG, "累计沉降: " + accumulativeSubsidence + " mm");
                    accumulativeSubsidence += sumOfDataCorrection;
                    accumulativeSubsidence = Math.abs(accumulativeSubsidence);
                    if (accumulativeSubsidence >= ACCUMULATIVE_THRESHOLD) {
                        accumulativeSubsidence = Math.round(accumulativeSubsidence);
                        int uType = type == 1 ? GONGDING_LEIJI_XIACHEN_EXCEEDING
                                : DIBIAO_LEIJI_XIACHEN_EXCEEDING;
                        ret.leijiType = uType;
                        ret.leijiValue = (long) accumulativeSubsidence;
                        int alertId = -1;
                        if (type == 1) {
                            alertId = AlertListDao.defaultDao().insertOrUpdate((TunnelSettlementTotalData) point, 3/* default */,
                                    uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
                        } else {
                            alertId = AlertListDao.defaultDao().insertOrUpdate((SubsidenceTotalData) point, 3/* default */,
                                    uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
                        }
                        AlertHandlingInfoDao.defaultDao().insertIfNotExist(alertId, null,
                                thisTime, String.valueOf(chainageId) + pntType, ALERT_STATUS_OPEN/*报警*/, 1/*true*/);
                    }
                }

                Object lastInfo = pastInfoList.get(pastInfoList.size() - 1);
                String[] lastCoords = null;
                Date lastTime = null;
                if (type == 1) {
                    lastCoords = ((TunnelSettlementTotalData) lastInfo).getCoordinate().split(",");
                    lastTime = ((TunnelSettlementTotalData) lastInfo).getSurveyTime();
                    thisDataCorrection = ((TunnelSettlementTotalData) lastInfo).getDataCorrection();
                } else if (type == 2) {
                    lastCoords = ((SubsidenceTotalData) lastInfo).getCoordinate().split(",");
                    lastTime = ((SubsidenceTotalData) lastInfo).getSurveyTime();
                    thisDataCorrection = ((SubsidenceTotalData) lastInfo).getDataCorrection();
                }

                if (lastCoords != null && lastCoords.length == 3
                        && !TextUtils.isEmpty(lastCoords[2]) && lastTime != null
                        && lastTime != null) {
                    double lastZ = Double.valueOf(lastCoords[2]);
                    Log.d(TAG, "last z: " + lastZ + " m");
                    double deltaZ = thisZ - lastZ;
                    Log.d(TAG, "delta z: " + deltaZ + " m");
                    deltaZ *= 1000;//CHANGE TO MILLIMETER
                    deltaZ += thisDataCorrection;
                    deltaZ = Math.abs(deltaZ);
                    long deltaT = Math.abs(thisTime.getTime() - lastTime.getTime());
                    Log.d(TAG, "delta t: " + deltaT + " ms");
                    if (deltaT < Time.ONE_HOUR) {
                        deltaT = Time.ONE_HOUR;//ONE HOUR at least to avoid infinity
                    }
                    double deltaTInDay = ((double)deltaT / Time.DAY_MILLISECEND_RATIO);
                    double h = 1d/24d;
                    if (deltaTInDay == 0) {
                        deltaTInDay = h;
                    }
                    Log.d(TAG, "delta t in day: " + deltaTInDay + " day");
                    double subsidenceSpeed = deltaZ / deltaTInDay;
                    Log.d(TAG, "沉降速率: " + subsidenceSpeed + " mm/d");
                    if (subsidenceSpeed >= SPEED_THRESHOLD) {
                        subsidenceSpeed = Math.round(subsidenceSpeed);
                        int uType = type == 1 ? GONGDINGI_XIACHEN_SULV_EXCEEDING
                                : DIBIAO_XIACHEN_SULV_EXCEEDING;
                        ret.sulvType = uType;
                        ret.sulvValue = (long) subsidenceSpeed;
                        int alertId = -1;
                        if (type == 1) {
                            alertId = AlertListDao.defaultDao().insertOrUpdate((TunnelSettlementTotalData) point, 3/* default */,
                                    uType, subsidenceSpeed, SPEED_THRESHOLD, originalDataID);
                        } else {
                            alertId = AlertListDao.defaultDao().insertOrUpdate((SubsidenceTotalData) point, 3/* default */,
                                    uType, subsidenceSpeed, SPEED_THRESHOLD, originalDataID);
                        }
                        AlertHandlingInfoDao.defaultDao().insertIfNotExist(alertId, null,
                                thisTime, String.valueOf(chainageId) + pntType, ALERT_STATUS_OPEN/*报警*/, 1/*true*/);
                    }
                }
            }

        }
        return ret;
    }

    /**
     *
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

        if (s_1 == null || s_2 == null) {
            return null;
        }

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
            
            float sumOfCorrections = calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(s_1InfoList);

            TunnelSettlementTotalData s_1First = s_1InfoList.get(0);
            TunnelSettlementTotalData s_2First = TunnelSettlementTotalDataDao.defaultDao()
                    .queryOppositePointOfALine(s_1First, s_2.getPntType());
            double lineFirstLength = getLineLength(s_1First, s_2First);
            double convergence = lineThisLength - lineFirstLength;
            convergence *= 1000; //CHANGE TO MILLIMETER
            convergence += sumOfCorrections;
            convergence = Math.abs(convergence);
            if (convergence >= ACCUMULATIVE_THRESHOLD) {
                convergence = Math.round(convergence);
                int uType = SHOULIAN_LEIJI_EXCEEDING;
                ret.leijiType = uType;
                ret.leijiValue = (long) convergence;
                int alertId = AlertListDao.defaultDao().insertOrUpdate(s_1, 3/* default */,
                        uType, convergence, ACCUMULATIVE_THRESHOLD, originalDataID);
                AlertHandlingInfoDao.defaultDao().insertIfNotExist(alertId, null,
                        thisTimeDate, String.valueOf(chainageId) + s_1.getPntType(), ALERT_STATUS_OPEN/*报警*/, 1/*true*/);
            }

            TunnelSettlementTotalData s_1Last = s_1InfoList.get(s_1InfoList.size() - 1);
            TunnelSettlementTotalData s_2Last = TunnelSettlementTotalDataDao.defaultDao()
                    .queryOppositePointOfALine(s_1Last, s_2.getPntType());
            float correction = s_1Last.getDataCorrection();
            double lineLastLength = getLineLength(s_1Last, s_2Last);
            double deltaLenth = lineThisLength - lineLastLength;
            deltaLenth *= 1000;
            deltaLenth += correction;
            deltaLenth = Math.abs(deltaLenth);
            Date s_1LastTime = s_1Last.getSurveyTime();
            Date s_2LastTime = s_2Last.getSurveyTime();
            double lastTime = s_1LastTime.getTime();
            if (s_2LastTime.getTime() > lastTime) {
                lastTime = s_2LastTime.getTime();
            }
            double deltaTime = Math.abs((thisTime - lastTime));
            if (deltaTime < Time.ONE_HOUR) {
                deltaTime = Time.ONE_HOUR;//ONE HOUR at least to avoid infinity
            }
            double deltaTInDay = (deltaTime / Time.DAY_MILLISECEND_RATIO);
            double h = 1d/24d;
            if (deltaTInDay < h) {
                deltaTInDay = h;
            }
            double shoulianSpeed = deltaLenth / deltaTInDay;
            if (shoulianSpeed >= SPEED_THRESHOLD) {
                shoulianSpeed = Math.round(shoulianSpeed);
                int uType = SHOULIAN_SULV_EXCEEDING;
                ret.sulvType = uType;
                ret.sulvValue = (long) shoulianSpeed;
                int alertId = AlertListDao.defaultDao().insertOrUpdate(s_1, 3/* default */, uType, shoulianSpeed,
                        SPEED_THRESHOLD, originalDataID);
                AlertHandlingInfoDao.defaultDao().insertIfNotExist(alertId, null,
                        thisTimeDate, String.valueOf(chainageId) + s_1.getPntType(), ALERT_STATUS_OPEN/*报警*/, 1/*true*/);
            }
        }

        return ret;
    }

    /**
     * @param s_1 测线的一个点
     * @param s_2 测线的另一个点
     * @return 测线长度，单位：米
     */
    public static double getLineLength(TunnelSettlementTotalData s_1, TunnelSettlementTotalData s_2) {

        if (s_1 == null || s_2 == null) {
            return 0;
        }

        String cs1 = s_1.getCoordinate();
        String cs2 = s_2.getCoordinate();
        if (TextUtils.isEmpty(cs1) || TextUtils.isEmpty(cs2)) {
            return 0;
        }

        String[] s_1cs = cs1.split(",");
        String[] s_2cs = cs2.split(",");
        if (s_1cs == null || s_1cs.length != 3 || s_2cs == null || s_2cs.length != 3) {
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

        ArrayList<AlertInfo> l = new ArrayList<AlertInfo>();
        String sql = "SELECT"
                + " AlertList.ID AS alertId,"
                + " AlertHandlingList.ID AS alertHandlingId,"
                + " TunnelCrossSectionIndex.sectionName AS sectionName,"
                + " AlertList.AlertTime AS date,"
                + " AlertList.PntType AS pntType,"
                + " AlertList.Utype AS utype,"
                + " AlertHandlingList.AlertStatus AS status,"
                + " AlertList.SheetID AS sheetId,"
                + " AlertList.CrossSectionID AS sectionId"
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
                    ai.setSheetId(c.getInt(7));
                    ai.setSectionId(c.getInt(8));
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
     *            要更新的TunnelSettlementTotalData表或SubsidenceTotalData表的DataCorrection列, 只有dataStatus为3时有意义
     * @param alertStatus
     *            要新插入AlertHandlingList表的AlertStatus列
     * @param handling
     *            要新插入AlertHandlingList表的Handling列
     * @param handlingTime
     *            要新插入AlertHandlingList表的HandlingTime列
     */
    public static void handleAlert(int alertId, int dataStatus, float correction, int alertStatus,
            String handling, Date handlingTime) {
        Log.d(TAG, "handleAlert");
        AlertList al = AlertListDao.defaultDao().queryOneById(alertId);
        String originalID = al.getOriginalDataID();
        int chainageId = al.getCrossSectionID();
        String pntType = al.getPntType();
        String duePerson = String.valueOf(chainageId) + pntType;

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

            if (ids.size() == 1) {//测点
                int measNo = -1;
                if (pntType.contains("A")) {//隧道内断面
                    TunnelSettlementTotalDataDao.defaultDao().updateDataStatus(ids.get(0), dataStatus, correction);

                    if (dataStatus == POINT_DATASTATUS_CORRECTION && correction != 0) {
                        //STEP 4
                        TunnelSettlementTotalData p = TunnelSettlementTotalDataDao.defaultDao().queryOneById(ids.get(0));
                        if (p != null) {
                            measNo = p.getMEASNo();
                        }
                    }
                } else {//地表沉降
                    SubsidenceTotalDataDao.defaultDao().updateDataStatus(ids.get(0), dataStatus, correction);

                    if (dataStatus == POINT_DATASTATUS_CORRECTION && correction != 0) {
                        // STEP 4
                        SubsidenceTotalData p = SubsidenceTotalDataDao.defaultDao().queryOneById(
                                ids.get(0));
                        if (p != null) {
                            measNo = p.getMEASNo();
                        }
                    }
                }

                if (measNo != -1) {
                    updatePointSubsidenceAlertsAfterCorrection(chainageId, pntType, measNo);
                }
            } else {//测线
                for (int id : ids) {
                    TunnelSettlementTotalDataDao.defaultDao().updateDataStatus(id, dataStatus, correction);
                }
                //测线的DataCorrection存在第一个点中，即SX_1
                if (dataStatus == POINT_DATASTATUS_CORRECTION && correction != 0) {
                    // STEP 4
                    TunnelSettlementTotalData s_1 = TunnelSettlementTotalDataDao.defaultDao().queryOneById(ids.get(0));
                    updateLineConvergenceAlertsAfterCorrection(chainageId, s_1.getPntType(), s_1.getMEASNo());
                }
            }
        }

        //step 2
        AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, handlingTime, duePerson,
                alertStatus, 1/* true */);

        //TODO: step 3 ignored for now, XXXARCHING（桌面版使用，android可预留设计）
    }

    public static void updatePointSubsidenceAlertsAfterCorrection(int chainageId, String pntType,
            int MEASNo) {
        Log.d(TAG, "updatePointSubsidenceAlertsAfterCorrection, pntType: " + pntType);
        if (pntType.contains("A")) {//隧道内断面
            List<TunnelSettlementTotalData> ls = TunnelSettlementTotalDataDao.defaultDao()
                    .queryInfoAfterMEASNo(chainageId, pntType, MEASNo);
            if (ls != null && ls.size() > 0) {
                for (TunnelSettlementTotalData p : ls) {
                    checkPointSubsidenceExceed(p);
                }
            }
        } else {//地表沉降
            List<SubsidenceTotalData> ls = SubsidenceTotalDataDao.defaultDao()
                    .queryInfoAfterMEASNo(chainageId, pntType, MEASNo);
            if (ls != null && ls.size() > 0) {
                for (SubsidenceTotalData p : ls) {
                    checkPointSubsidenceExceed(p);
                }
            }
        }
    }

    public static void updateLineConvergenceAlertsAfterCorrection(int chainageId, String pntType,
            int MEASNo) {
        Log.d(TAG, "updateLineConvergenceAlertsAfterCorrection, pntType: " + pntType);
        if (pntType.contains("S") && pntType.endsWith("1")) {//测线左点
            String oppositePntType = pntType.substring(0, pntType.length() - 1) + "2";
            List<TunnelSettlementTotalData> ls = TunnelSettlementTotalDataDao.defaultDao()
                    .queryInfoAfterMEASNo(chainageId, pntType, MEASNo);
            if (ls != null && ls.size() > 0) {
                for (TunnelSettlementTotalData s_1 : ls) {
                    TunnelSettlementTotalData s_2 = TunnelSettlementTotalDataDao.defaultDao()
                    .queryOppositePointOfALine(s_1, oppositePntType);//拿到测线右点
                    checkLineConvergenceExceed(s_1, s_2);
                }
            }
        }
    }

    /**
     * @param pastPointList
     * @return 所有pastPointList中的测点数据的DataCorrection的和值，单位：毫米
     */
    public static float calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(
            List<TunnelSettlementTotalData> pastPointList) {
        float s = 0f;
        if (pastPointList != null && pastPointList.size() > 0) {
            for (TunnelSettlementTotalData p : pastPointList) {
                if (p != null) {
                    s += p.getDataCorrection();
                }
            }
        }
        return s;
    }

    /**
     * @param pastPointList
     * @return 所有pastPointList中的测点数据的DataCorrection的和值，单位：毫米
     */
    public static float calculateSumOfDataCorrectionsOfSubsidenceTotalDatas(
            List<SubsidenceTotalData> pastPointList) {
        float s = 0f;
        if (pastPointList != null && pastPointList.size() > 0) {
            for (SubsidenceTotalData p : pastPointList) {
                if (p != null) {
                    s += p.getDataCorrection();
                }
            }
        }
        return s;
    }

    public static class Exceeding {
        int leijiType = -1;
        int sulvType = -1;
        long leijiValue = -1;
        long sulvValue = -1;
    }
}
