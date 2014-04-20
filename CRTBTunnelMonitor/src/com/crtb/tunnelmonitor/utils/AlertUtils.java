
package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;

import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class AlertUtils {

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

    /**
     * 检测 断面拱顶 或 地表测点 的 累计沉降 或 单次沉降速率 是否超限.
     * 
     * @param point
     *            本次测量得到的测量点信息.
     *            因为TunnelSettlementTotalData和SubsidenceTotalData有完全相同的类结构定义,
     *            故这里用一个方法以避免代码重复。
     * @return error list; if null or empty, it means no exceeding.
     */
    public static ArrayList<Integer> checkPointSubsidenceExceed(Object point) {
        ArrayList<Integer> errList = null;

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
            errList = new ArrayList<Integer>();

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
                    if (accumulativeSubsidence >= ACCUMULATIVE_THRESHOLD) {
                        int uType = type == 1 ? GONGDING_LEIJI_XIACHEN_EXCEEDING
                                : DIBIAO_LEIJI_XIACHEN_EXCEEDING;
                        errList.add(uType);
                        int alertId = -1;
                        if (type == 1) {
                            alertId = AlertListDao.defaultDao().insertItem((TunnelSettlementTotalData) point, 3/* default */,
                                    uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
                        } else {
                            alertId = AlertListDao.defaultDao().insertItem((SubsidenceTotalData) point, 3/* default */,
                                    uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
                        }
                        AlertHandlingInfoDao.defaultDao().insertItem(alertId, 0,
                                thisTime, String.valueOf(chainageId) + pntType, 1/*报警*/, 01/*true*/);
                    }
                }

                Object lastInfo = pastInfoList.get(pastInfoList.size());
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
                    if (subsidenceSpeed >= SPEED_THRESHOLD) {
                        int uType = type == 1 ? GONGDINGI_XIACHEN_SULV_EXCEEDING
                                : DIBIAO_XIACHEN_SULV_EXCEEDING;
                        errList.add(uType);
                        int alertId = -1;
                        if (type == 1) {
                            alertId = AlertListDao.defaultDao().insertItem((TunnelSettlementTotalData) point, 3/* default */,
                                    uType, subsidenceSpeed, SPEED_THRESHOLD, originalDataID);
                        } else {
                            alertId = AlertListDao.defaultDao().insertItem((SubsidenceTotalData) point, 3/* default */,
                                    uType, subsidenceSpeed, SPEED_THRESHOLD, originalDataID);
                        }
                        AlertHandlingInfoDao.defaultDao().insertItem(alertId, 0,
                                thisTime, String.valueOf(chainageId) + pntType, 1/*报警*/, 01/*true*/);
                    }
                }
            }

        }
        return errList;
    }

    /**
     * @param s_1
     *            测线的其中一个端点
     * @param s_2
     *            测线的另一个端点
     * @return error list; if null or empty, it means no exceeding.
     */
    public static ArrayList<Integer> checkLineConvergenceExceed(TunnelSettlementTotalData s_1,
            TunnelSettlementTotalData s_2) {
        ArrayList<Integer> errList = new ArrayList<Integer>();

        String originalDataID = s_1.getID() + "," + s_2.getID();

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
                errList.add(uType);
                int alertId = AlertListDao.defaultDao().insertItem(s_1, 3/* default */,
                        uType, convergence, ACCUMULATIVE_THRESHOLD, originalDataID);
                AlertHandlingInfoDao.defaultDao().insertItem(alertId, 0,
                        thisTimeDate, String.valueOf(chainageId) + s_1.getPntType(), 1/*报警*/, 01/*true*/);
            }

            TunnelSettlementTotalData s_1Last = s_1InfoList.get(s_1InfoList.size());
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
                errList.add(uType);
                int alertId = AlertListDao.defaultDao().insertItem(s_1, 3/* default */, uType, shoulianSpeed,
                        SPEED_THRESHOLD, originalDataID);
                AlertHandlingInfoDao.defaultDao().insertItem(alertId, 0,
                        thisTimeDate, String.valueOf(chainageId) + s_1.getPntType(), 1/*报警*/, 1/*true*/);
            }
        }

        return errList;
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

}
