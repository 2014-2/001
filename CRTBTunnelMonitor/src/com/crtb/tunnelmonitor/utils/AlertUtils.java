
package com.crtb.tunnelmonitor.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.dao.impl.v2.CrownSettlementARCHINGDao;
import com.crtb.tunnelmonitor.dao.impl.v2.RawSheetIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceTotalDataDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.CrownSettlementARCHING;
import com.crtb.tunnelmonitor.entity.MergedAlert;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceSettlementARCHING;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.common.Constant;


public class AlertUtils {

    private static final String TAG = "AlertUtils";

    // 对应  AlertList 表中的OriginalDataID列, 一条测线两测点数据id间的分隔符
    public static final String ORIGINAL_ID_DIVIDER = ",";

	//报警的最大值
	public static final int SULV_ALARM_MAX_VALUE = 3000;
    
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

    public static final String[] U_TYPE_MSGS = {"拱顶累计下沉值超限", "拱顶下沉速率超限", "累计收敛超限", "收敛速率超限", "地表累计下沉值超限", "地表下沉速率超限"};

    public static final String[] U_TYPE_MSGS_SAFE = {"拱顶累计下沉值", "拱顶下沉速率", "累计收敛", "收敛速率", "地表累计下沉值", "地表下沉速率"};

    public static final int ALERT_STATUS_HANDLED = 0;
    public static final int ALERT_STATUS_OPEN = 1;
    public static final int ALERT_STATUS_HANDLING = 2;
    public static final String[] ALERT_STATUS_MSGS = {"已消警", "开", "处理中"};

    public static final int POINT_DATASTATUS_NONE = 0;
    public static final int POINT_DATASTATUS_DISCARD = 1;
    public static final int POINT_DATASTATUS_AS_FIRSTLINE = 2;
    public static final int POINT_DATASTATUS_CORRECTION = 3;
    public static final int POINT_DATASTATUS_NORMAL = 4;
    public static final String[] ALERT_HANDLING = {"没有处理", "不参与计算", "作为首行", "添加改正值", "正常参与计算"};
    
    /**
     * 累积位移等级对应的颜色
     */
    private static int[] leijiOffsetLevelColor = new int[]{Color.GREEN,Color.YELLOW,Color.RED};
    
    /**
     * 累积位移等级对应的颜色
     */
    private static int[] sulvOffsetLevelColor = new int[]{Color.GREEN,Color.RED};
    
    /**
     * @param point
     * @param readOnly 为true时该方法不会对数据库做出任何修改，只返回预警信息
     * @return
     */
    public static OffsetLevel[] getPointSubsidenceExceedMsg(Object point, boolean readOnly, String rockGrade) {
        return checkPointSubsidenceAlert(point, -1, null, null, readOnly,rockGrade);
    }
    
    /**
     * @param s_1
     * @param s_2
     * @param readOnly 为true时该方法不会对数据库做出任何修改，只返回预警信息
     * @return
     */
	public static OffsetLevel[] getLineConvergenceExceedMsg(TunnelSettlementTotalData s_1, TunnelSettlementTotalData s_2, boolean readOnly, String rockGrade) {
		return checkLineConvergenceAlert(s_1, s_2, -1, null, null, readOnly, rockGrade);
	}

    public static String getAlertValueUnit(int alertType) {
        switch (alertType) {
        case GONGDING_LEIJI_XIACHEN_EXCEEDING:
        case SHOULIAN_LEIJI_EXCEEDING:
        case DIBIAO_LEIJI_XIACHEN_EXCEEDING:
            return "毫米";
        case GONGDINGI_XIACHEN_SULV_EXCEEDING:
        case SHOULIAN_SULV_EXCEEDING:
        case DIBIAO_XIACHEN_SULV_EXCEEDING:
            return "毫米/天";
        }
        return "";
    }


    /**
     * 检测 断面拱顶 或 地表测点 的 累计沉降 或 单次沉降速率 是否超限.
     * 
     * @param point
     *            本次测量得到的测量点信息.
     *            因为TunnelSettlementTotalData和SubsidenceTotalData有完全相同的类结构定义,
     *            故这里用一个方法以避免代码重复。
     * @param readOnly 为true时该方法不会对数据库做出任何修改，只返回预警信息
     * @return error list; if null or empty, it means no exceeding.
     */
    private static Exceeding getPointSubsidenceExceed(Object point, int curHandlingAlertId, String handling, Date handlingTime) {
        Log.d(TAG, "checkPointSubsidenceExceed");
        Exceeding ret = new Exceeding();
        int type = 0;
        List pastInfoList = null;
        String[] thisCoords = null;
        Date thisTime = null;
        String sheetId = null;
        String chainageId = null;
        String pntType = null;
        String originalDataID = null;

        float sumOfDataCorrection = 0f;
        float thisDataCorrection = 0f;

        int dataStatus = POINT_DATASTATUS_NONE;

        CrownSettlementARCHING cArc = null;
        SubsidenceSettlementARCHING sArc = null;

        if (point instanceof TunnelSettlementTotalData) {
            TunnelSettlementTotalData tPoint = (TunnelSettlementTotalData) point;
            dataStatus = tPoint.getDataStatus();
            if (dataStatus == POINT_DATASTATUS_AS_FIRSTLINE) {
                return null;
            }
            cArc = new CrownSettlementARCHING();
            type = 1;
            cArc.setOriginalDataId(tPoint.getGuid());
            thisCoords = tPoint.getCoordinate().split(",");
            thisTime = tPoint.getSurveyTime();
            thisDataCorrection = tPoint.getDataCorrection();
            pastInfoList = TunnelSettlementTotalDataDao.defaultDao().queryInfoBeforeMeasId(
                    tPoint.getChainageId(), tPoint.getPntType(), tPoint.getID());
            sumOfDataCorrection = thisDataCorrection + calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(pastInfoList);
            chainageId = tPoint.getChainageId();
            sheetId = tPoint.getSheetId();
            cArc.setSheetId(sheetId);
            pntType = tPoint.getPntType();
            originalDataID = String.valueOf(tPoint.getGuid());
            ret.tunnelData = tPoint;
        } else if (point instanceof SubsidenceTotalData) {
            SubsidenceTotalData sPoint = (SubsidenceTotalData) point;
            dataStatus = sPoint.getDataStatus();
            if (dataStatus == POINT_DATASTATUS_AS_FIRSTLINE) {
                return null;
            }
            sArc = new SubsidenceSettlementARCHING();
            type = 2;
            sArc.setOriginalDataId(sPoint.getGuid());
            thisCoords = sPoint.getCoordinate().split(",");
            thisTime = sPoint.getSurveyTime();
            thisDataCorrection = sPoint.getDataCorrection();
            pastInfoList = SubsidenceTotalDataDao.defaultDao().queryInfoBeforeMeasId(
                    sPoint.getChainageId(), sPoint.getPntType(), sPoint.getID());
            sumOfDataCorrection = thisDataCorrection + calculateSumOfDataCorrectionsOfSubsidenceTotalDatas(pastInfoList);
            chainageId = sPoint.getChainageId();
            sheetId = sPoint.getSheetId();
            sArc.setSheetId(sheetId);
            pntType = sPoint.getPntType();
            originalDataID = String.valueOf(sPoint.getGuid());
            ret.subData = sPoint;
        } else {
            return null;
        }

        if (thisCoords == null || thisCoords.length < 3) {
            return null;
        }

        if (curHandlingAlertId > 0 && dataStatus == POINT_DATASTATUS_DISCARD) {
            List<AlertList> l = AlertListDao.defaultDao().queryByOrigionalDataId(sheetId,
                    chainageId, originalDataID);
            for (AlertList al : l) {
                if (al != null) {
                    int aid = al.getId();
                    AlertHandlingInfoDao
                            .defaultDao()
                            .insertIfNotExist(aid, handling, handlingTime,
                                    AppCRTBApplication.getInstance().mUserName, ALERT_STATUS_HANDLED, 0/*false*/);
                }
            }
            return ret;
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
                    double accumulativeSubsidence = firstZ - thisZ;
                    accumulativeSubsidence *= 1000;//CHANGE TO MILLIMETER
                    Log.d(TAG, "累计沉降: " + accumulativeSubsidence + " mm");
                    accumulativeSubsidence += sumOfDataCorrection;
//                    accumulativeSubsidence = Math.abs(accumulativeSubsidence);
                    int uType = type == 1 ? GONGDING_LEIJI_XIACHEN_EXCEEDING
                            : DIBIAO_LEIJI_XIACHEN_EXCEEDING;
                    accumulativeSubsidence = CrtbUtils.formatDouble(accumulativeSubsidence, 1);
                    if (type == 1) {
                        cArc.setTotalSettlement(accumulativeSubsidence);
                    } else if (type == 2) {
                        
                    }
                    
                    ret.leijiType = uType;
                    ret.leijiValue = accumulativeSubsidence;
                }

                Object lastInfo = pastInfoList.get(pastInfoList.size() - 1);
                String[] lastCoords = null;
                Date lastTime = null;
                if (type == 1) {
                    lastCoords = ((TunnelSettlementTotalData) lastInfo).getCoordinate().split(",");
                    lastTime = ((TunnelSettlementTotalData) lastInfo).getSurveyTime();
//                    thisDataCorrection = ((TunnelSettlementTotalData) lastInfo).getDataCorrection();
                } else if (type == 2) {
                    lastCoords = ((SubsidenceTotalData) lastInfo).getCoordinate().split(",");
                    lastTime = ((SubsidenceTotalData) lastInfo).getSurveyTime();
//                    thisDataCorrection = ((SubsidenceTotalData) lastInfo).getDataCorrection();
                }

                if (lastCoords != null && lastCoords.length == 3
                        && !TextUtils.isEmpty(lastCoords[2]) && thisTime != null && lastTime != null) {
                    double lastZ = Double.valueOf(lastCoords[2]);
                    Log.d(TAG, "last z: " + lastZ + " m");
                    double deltaZ = lastZ - thisZ;
                    Log.d(TAG, "delta z: " + deltaZ + " m");
                    deltaZ *= 1000;//CHANGE TO MILLIMETER
                    deltaZ += thisDataCorrection;
//                    deltaZ = Math.abs(deltaZ);
                    long deltaT = Math.abs(thisTime.getTime() - lastTime.getTime());
                    Log.d(TAG, "delta t: " + deltaT + " ms");
                    if (deltaT < Time.ONE_SECOND) {
                        deltaT = Time.ONE_SECOND;//ONE SECOND at least to avoid infinity
                    }
                    double deltaTInDay = ((double)deltaT / Time.DAY_MILLISECEND_RATIO);
                    Log.d(TAG, "delta t in day: " + deltaTInDay + " day");
                    double subsidenceSpeed = deltaZ / deltaTInDay;
                    Log.d(TAG, "沉降速率: " + subsidenceSpeed + " mm/d");
                    int uType = type == 1 ? GONGDINGI_XIACHEN_SULV_EXCEEDING
                            : DIBIAO_XIACHEN_SULV_EXCEEDING;
                    subsidenceSpeed = CrtbUtils.formatDouble(subsidenceSpeed, 1);
                    ret.sulvType = uType;
                    ret.sulvValue = subsidenceSpeed;
                }
            }

        }
        
        ret.originalDataID = originalDataID;
        return ret;
    }

    public static OffsetLevel[] checkPointSubsidenceAlert(Object point, int curHandlingAlertId, String handling, Date handlingTime,boolean readOnly,String rockGrade){
		if (handling == null) {
			handling = "";
		}
		OffsetLevel[] offsetLevel = null;
		Exceeding excecding = getPointSubsidenceExceed(point, curHandlingAlertId, handling, handlingTime);
		if (excecding != null) {
			offsetLevel = checkOffsetLevel(excecding, rockGrade);
		}
		if (!readOnly && offsetLevel != null && !offsetLevel[Constant.LEI_JI_INDEX].IsLargerThanMaxValue) {
			updatePointSubsidence(excecding.tunnelData, excecding.subData, offsetLevel[Constant.LEI_JI_INDEX].TransfiniteLevel, offsetLevel[Constant.LEI_JI_INDEX].IsTransfinite, offsetLevel[Constant.LEI_JI_INDEX].Value, offsetLevel[Constant.LEI_JI_INDEX].PreType, excecding.originalDataID, handlingTime, handling, curHandlingAlertId);
		}
		return offsetLevel;
    }
    
	private static void updatePointSubsidence(TunnelSettlementTotalData tunnelData, SubsidenceTotalData subData,int alertLevel, boolean isTransfinite, double accumulativeSubsidence, int uType, String originalDataID, Date handlingTime, String handling, int curHandlingAlertId) {
		//如果，以后又要恢复保存报警速率，则直接传递OffsetLevel[]进来，再转换就OK
		
		int alertId = -1;
		//int alertLevel = -1;
		String sheetId = null;
		String chainageId = null;
		String duePerson = AppCRTBApplication.getInstance().mUserName;
		if (tunnelData != null) {
			sheetId = tunnelData.getSheetId();
			chainageId = tunnelData.getChainageId();
			//alertLevel = GetManagementLevel(sheetId, chainageId, accumulativeSubsidence, uType);
		} else {
			sheetId = subData.getSheetId();
			chainageId = subData.getChainageId();
			//alertLevel = GetManagementLevel(sheetId, chainageId, accumulativeSubsidence, uType);
		}

		//超限
		if (isTransfinite) {
			if (tunnelData != null) {
				alertId = AlertListDao.defaultDao().insertOrUpdate(tunnelData, alertLevel, uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
			} else {
				alertId = AlertListDao.defaultDao().insertOrUpdate(subData, alertLevel, uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
			}
			
			String handlingRemark = "";
			if (curHandlingAlertId >= 0 /* && alertId == curHandlingAlertId */) {
				handlingRemark = handling;
			}

			if (curHandlingAlertId < 0) {// 重测，删除原来数据的报警信息
				AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
			}

			AlertHandlingInfoDao.defaultDao().insertItem(alertId, handlingRemark, new Date(System.currentTimeMillis()), duePerson, ALERT_STATUS_OPEN/* 报警 */, 0/* false */);

		} 
		//没有超限
		else {
			AlertList alert = AlertListDao.defaultDao().queryOne(sheetId, chainageId, originalDataID, uType);
			if (alert != null) {
				alertId = alert.getId();
				if (curHandlingAlertId >= 0) {
					AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, handlingTime, duePerson, ALERT_STATUS_HANDLED, 1/* true */);
					if (tunnelData != null) {
						alertId = AlertListDao.defaultDao().insertOrUpdate(tunnelData, alertLevel, uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
					} else {
						alertId = AlertListDao.defaultDao().insertOrUpdate(subData, alertLevel, uType, accumulativeSubsidence, ACCUMULATIVE_THRESHOLD, originalDataID);
					}
				} else if (alert.getUploadStatus() != 2) {
					AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
					AlertListDao.defaultDao().deleteById(alertId);
				}
			}
		}
	}
    
	public static OffsetLevel[] checkLineConvergenceAlert(TunnelSettlementTotalData s_1,TunnelSettlementTotalData s_2, int curHandlingAlertId, String handling, Date handlingTime,boolean readOnly,String rockGrade){
        if (handling == null) {
            handling = "";
        }
		OffsetLevel[] offsetLevel = null;
		Exceeding excecding = getLineConvergenceExceed(s_1, s_2, curHandlingAlertId, handling, handlingTime);
		if (excecding != null) {
			offsetLevel = checkOffsetLevel(excecding, rockGrade);
		}
		if (!readOnly && offsetLevel != null && !offsetLevel[Constant.LEI_JI_INDEX].IsLargerThanMaxValue) {
			updateLineConvergence(s_1, s_2, offsetLevel[Constant.LEI_JI_INDEX].TransfiniteLevel, offsetLevel[Constant.LEI_JI_INDEX].IsTransfinite, offsetLevel[Constant.LEI_JI_INDEX].Value, offsetLevel[Constant.LEI_JI_INDEX].PreType, excecding.originalDataID, handlingTime, handling, curHandlingAlertId);
		}
		return offsetLevel;
    }
	
	private static void updateLineConvergence(TunnelSettlementTotalData s_1, TunnelSettlementTotalData s_2,int alertLevel, boolean isTransfinite, double convergence, int uType, String originalDataID, Date handlingTime, String handling, int curHandlingAlertId) {
		int alertId = -1;
		//int alertLevel = -1;
		String sheetId = null;
		String chainageId = null;
		String duePerson = AppCRTBApplication.getInstance().mUserName;
		sheetId = s_1.getSheetId();
		chainageId = s_1.getChainageId();
		// 超限
		if (isTransfinite) {
			//alertLevel = GetManagementLevel(sheetId, chainageId, convergence, uType);
			alertId = AlertListDao.defaultDao().insertOrUpdate(s_1, alertLevel, uType, convergence, ACCUMULATIVE_THRESHOLD, originalDataID);

			String handlingRemark = "";
			if (curHandlingAlertId >= 0 /* && alertId == curHandlingAlertId */) {
				handlingRemark = handling;
			}

			if (curHandlingAlertId < 0) {// 重测
				AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
			}

			AlertHandlingInfoDao.defaultDao().insertItem(alertId, handlingRemark, new Date(System.currentTimeMillis()), duePerson, ALERT_STATUS_OPEN/* 报警 */, 0/* false */);
		}
		// 没有超限
		else {
			AlertList al = AlertListDao.defaultDao().queryOne(sheetId, chainageId, originalDataID, uType);
			if (al != null) {
				alertId = al.getId();
				if (curHandlingAlertId >= 0) {
					AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, handlingTime, duePerson, ALERT_STATUS_HANDLED, 1/* true */);
					//alertLevel = GetManagementLevel(sheetId, chainageId, convergence, uType);
					AlertListDao.defaultDao().insertOrUpdate(s_1, alertLevel, uType, convergence, ACCUMULATIVE_THRESHOLD, originalDataID);
				} else if (al.getUploadStatus() != 2) {
					AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
					AlertListDao.defaultDao().deleteById(alertId);
				}
			}
		}
	}
    
    /** 
    * 检测测线的累计沉降或速率是否超限
    * @param s_1
    *            测线的其中一个端点
    * @param s_2
    *            测线的另一个端点
    * @param readOnly 为true时该方法不会对数据库做出任何修改，只返回预警信息
    * @return error list; if null or empty, it means no exceeding.
    */
    private static Exceeding getLineConvergenceExceed(TunnelSettlementTotalData s_1,
           TunnelSettlementTotalData s_2, int curHandlingAlertId, String handling, Date handlingTime) {
       Log.d(TAG, "getLineConvergenceExceed");
       Exceeding ret = new Exceeding();

       if (s_1 == null || s_2 == null) {
           return null;
       }

       if (handling == null) {
           handling = "";
       }

       if (s_1.getDataStatus() == POINT_DATASTATUS_AS_FIRSTLINE) {
           return ret;
       }
       String sheetId = s_1.getSheetId();
       String originalDataID = s_1.getGuid() + ORIGINAL_ID_DIVIDER + s_2.getGuid();
       ret.originalDataID = originalDataID;
       float thisCorrection = s_1.getDataCorrection();

       double lineThisLength = getLineLength(s_1, s_2);
       Date s_1ThisTime = s_1.getSurveyTime();
       Date s_2ThisTime = s_2.getSurveyTime();
       double thisTime = -1;
       if (s_1ThisTime != null && s_2ThisTime != null) {

           Date thisTimeDate = s_1ThisTime;
           thisTime = s_1ThisTime.getTime();
           if (s_2ThisTime.getTime() > thisTime) {
               thisTime = s_2ThisTime.getTime();
               thisTimeDate = s_2ThisTime;
           }
       }

       String chainageId = s_1.getChainageId();

       int dataStatus = s_1.getDataStatus();
       if (curHandlingAlertId > 0 && dataStatus == POINT_DATASTATUS_DISCARD) {
           List<AlertList> l = AlertListDao.defaultDao().queryByOrigionalDataId(sheetId,
                   chainageId, originalDataID);
           for (AlertList al : l) {
               if (al != null) {
                   int aid = al.getId();
                   AlertHandlingInfoDao
                           .defaultDao()
                           .insertIfNotExist(aid, handling, handlingTime,
                                   AppCRTBApplication.getInstance().mUserName, ALERT_STATUS_HANDLED, 0/*false*/);
               }
           }
           return null;
       }

       List<TunnelSettlementTotalData> s_1InfoList = TunnelSettlementTotalDataDao.defaultDao()
               .queryInfoBeforeMeasId(chainageId, s_1.getPntType(), s_1.getID());
       if (s_1InfoList != null && s_1InfoList.size() > 0) {

           int s = s_1InfoList.size();
           String s_2pntType = s_2.getPntType();

           float sumOfCorrections = thisCorrection + calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(s_1InfoList);

           TunnelSettlementTotalData s_1First = null;
           TunnelSettlementTotalData s_2First = null;
           for (int i = 0; i < s; i++) {
               s_1First = s_1InfoList.get(i);
               s_2First = TunnelSettlementTotalDataDao.defaultDao()
                       .queryOppositePointOfALine(s_1First, s_2pntType);
               if (s_1First != null && s_2First != null) {
                   break;
               }
           }

           if (s_1First != null && s_2First != null) {
               double lineFirstLength = getLineLength(s_1First, s_2First);
               double convergence = lineFirstLength - lineThisLength;
               convergence *= 1000; //CHANGE TO MILLIMETER
               convergence += sumOfCorrections;
//               convergence = Math.abs(convergence);
               int uType = SHOULIAN_LEIJI_EXCEEDING;
               convergence = CrtbUtils.formatDouble(convergence, 1);
               ret.leijiType = uType;
               ret.leijiValue = convergence;
           }

           TunnelSettlementTotalData s_1Last = null;
           TunnelSettlementTotalData s_2Last = null;
           for (int i = 1; i <= s; i++) {
               s_1Last = s_1InfoList.get(s - i);
               s_2Last = TunnelSettlementTotalDataDao.defaultDao()
                       .queryOppositePointOfALine(s_1Last, s_2pntType);
               if (s_1Last != null && s_2Last != null) {
                   break;
               }
           }
           if (s_1Last != null && s_2Last != null && thisTime != -1) {
               double lineLastLength = getLineLength(s_1Last, s_2Last);
               double deltaLenth = lineLastLength - lineThisLength;
               deltaLenth *= 1000;
               deltaLenth += thisCorrection;
//               deltaLenth = Math.abs(deltaLenth);
               Date s_1LastTime = s_1Last.getSurveyTime();
               Date s_2LastTime = s_2Last.getSurveyTime();
               if (s_1LastTime == null || s_2LastTime == null) {
                   return null;
               }
               double lastTime = s_1LastTime.getTime();
               if (s_2LastTime.getTime() > lastTime) {
                   lastTime = s_2LastTime.getTime();
               }
               double deltaTime = Math.abs((thisTime - lastTime));
               if (deltaTime < Time.ONE_SECOND) {
                   deltaTime = Time.ONE_SECOND;//ONE SECOND at least to avoid infinity
               }
               double deltaTInDay = (deltaTime / Time.DAY_MILLISECEND_RATIO);
               double shoulianSpeed = deltaLenth / deltaTInDay;
               int uType = SHOULIAN_SULV_EXCEEDING;
               shoulianSpeed = CrtbUtils.formatDouble(shoulianSpeed, 1);
               ret.sulvType = uType;
               ret.sulvValue = shoulianSpeed;
           }
       }

       return ret;
   }
        
    /**
     * @param point
     * @return 长度为2的double型数组，第一个元素是累计沉降值，第二个是本次沉降值
     */
    private static double[] getSubsidenceValues(Object point) {
        double[] v = new double[]{0, 0};
        int type = 0;
        List pastInfoList = null;
        String[] thisCoords = null;

        float sumOfDataCorrection = 0f;
        float thisDataCorrection = 0f;

        if (point instanceof TunnelSettlementTotalData) {
            if (((TunnelSettlementTotalData) point).getDataStatus() == POINT_DATASTATUS_AS_FIRSTLINE) {
                return v;
            }

            type = 1;
            TunnelSettlementTotalData tPoint = (TunnelSettlementTotalData) point;
            thisCoords = tPoint.getCoordinate().split(",");
            thisDataCorrection = tPoint.getDataCorrection();
            pastInfoList = TunnelSettlementTotalDataDao.defaultDao().queryInfoBeforeMeasId(
                    tPoint.getChainageId(), tPoint.getPntType(), tPoint.getID());
            sumOfDataCorrection = thisDataCorrection
                    + calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(pastInfoList);
        } else if (point instanceof SubsidenceTotalData) {
            if (((SubsidenceTotalData) point).getDataStatus() == POINT_DATASTATUS_AS_FIRSTLINE) {
                return v;
            }

            type = 2;
            SubsidenceTotalData sPoint = (SubsidenceTotalData) point;
            thisCoords = sPoint.getCoordinate().split(",");
            thisDataCorrection = sPoint.getDataCorrection();
            pastInfoList = SubsidenceTotalDataDao.defaultDao().queryInfoBeforeMeasId(
                    sPoint.getChainageId(), sPoint.getPntType(), sPoint.getID());
            sumOfDataCorrection = thisDataCorrection
                    + calculateSumOfDataCorrectionsOfSubsidenceTotalDatas(pastInfoList);
        } else {
            return v;
        }

        if (thisCoords == null || thisCoords.length < 3) {
            return v;
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
                    double accumulativeSubsidence = firstZ - thisZ;
                    accumulativeSubsidence *= 1000;// CHANGE TO MILLIMETER
                    accumulativeSubsidence += sumOfDataCorrection;
                    v[0] = CrtbUtils.formatDouble(accumulativeSubsidence, 1);;
                }

                Object lastInfo = pastInfoList.get(pastInfoList.size() - 1);
                String[] lastCoords = null;
                if (type == 1) {
                    lastCoords = ((TunnelSettlementTotalData) lastInfo).getCoordinate().split(",");
                } else if (type == 2) {
                    lastCoords = ((SubsidenceTotalData) lastInfo).getCoordinate().split(",");
                }

                if (lastCoords != null && lastCoords.length == 3
                        && !TextUtils.isEmpty(lastCoords[2])) {
                    double lastZ = Double.valueOf(lastCoords[2]);
                    double deltaZ = lastZ - thisZ;
                    deltaZ *= 1000;// CHANGE TO MILLIMETER
                    deltaZ += thisDataCorrection;
                    v[1] = CrtbUtils.formatDouble(deltaZ, 1);
                }
            }
        }

        return v;
    }

    private static double[] getLineConvergenceValues(TunnelSettlementTotalData s_1,
            TunnelSettlementTotalData s_2) {
        Log.d(TAG, "getLineConvergenceValues");
        double[] ret = new double[]{0, 0};

        if (s_1 == null || s_2 == null) {
            return ret;
        }

        double lineThisLength = getLineLength(s_1, s_2);

        String chainageId = s_1.getChainageId();

        List<TunnelSettlementTotalData> s_1InfoList = TunnelSettlementTotalDataDao.defaultDao()
                .queryInfoBeforeMeasId(chainageId, s_1.getPntType(), s_1.getID());
        if (s_1InfoList != null && s_1InfoList.size() > 0) {

            int s = s_1InfoList.size();
            String s_2pntType = s_2.getPntType();

            float sumOfCorrections = calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(s_1InfoList);

            TunnelSettlementTotalData s_1First = null;
            TunnelSettlementTotalData s_2First = null;
            for (int i = 0; i < s; i++) {
                s_1First = s_1InfoList.get(i);
                s_2First = TunnelSettlementTotalDataDao.defaultDao()
                        .queryOppositePointOfALine(s_1First, s_2pntType);
                if (s_1First != null && s_2First != null) {
                    break;
                }
            }

            if (s_1First != null && s_2First != null) {
                double lineFirstLength = getLineLength(s_1First, s_2First);
                double convergence = lineFirstLength - lineThisLength;
                convergence *= 1000; //CHANGE TO MILLIMETER
                convergence += sumOfCorrections;
                ret[0] = convergence;
            }

            TunnelSettlementTotalData s_1Last = null;
            TunnelSettlementTotalData s_2Last = null;
            for (int i = 1; i <= s; i++) {
                s_1Last = s_1InfoList.get(s - i);
                s_2Last = TunnelSettlementTotalDataDao.defaultDao()
                        .queryOppositePointOfALine(s_1Last, s_2pntType);
                if (s_1Last != null && s_2Last != null) {
                    break;
                }
            }
            if (s_1Last != null && s_2Last != null) {
                float correction = s_1Last.getDataCorrection();
                double lineLastLength = getLineLength(s_1Last, s_2Last);
                double deltaLenth = lineLastLength - lineThisLength;
                deltaLenth *= 1000;
                deltaLenth += correction;
                ret[1] = deltaLenth;
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

    public static boolean mergedAlertCanBeUploaded(MergedAlert ma) {
        ArrayList<MergedAlert> l = getMergedAlertsBefore(ma);
        if (l == null || l.size() == 0) {
            return true;
        }

        for (MergedAlert m : l) {
            if (!m.isHandled() || !m.isUploaded()) {
                return false;
            }
        }

        return true;
    }

    private static ArrayList<MergedAlert> getMergedAlertsBefore(MergedAlert mergedAlert) {
        ArrayList<MergedAlert> malb = null;
        ArrayList<MergedAlert> mal = getMergedAlerts();
        Date date = mergedAlert.getSheetDate();
        String sectionGuid = mergedAlert.getSectionGuid();
        String pntType = mergedAlert.getPntType();
        if (date != null && sectionGuid != null && pntType != null) {
            if (mal != null && mal.size() > 0) {
                malb = new ArrayList<MergedAlert>();
                for (MergedAlert ma : mal) {
                    String secId = ma.getSectionGuid();
                    Date d = ma.getSheetDate();
                    String pt = ma.getPntType();
                    if (secId != null && secId.equals(sectionGuid) && d != null && d.before(date)
                            && pt != null && pt.equals(pntType)) {
                        malb.add(ma);
                    }
                }
            }
        }

        return malb;
    }

    public static ArrayList<MergedAlert> getMergedAlerts() {
        ArrayList<MergedAlert> mal = new ArrayList<MergedAlert>();
        HashMap<String, MergedAlert> mm = new HashMap<String, MergedAlert>();
        ArrayList<AlertInfo> ais = getAlertInfoList();
        if (ais != null && ais.size() > 0) {
            for (AlertInfo ai : ais) {
                String key = ai.getOriginalDataID() + ai.getPntType();
                if (mm.containsKey(key)) {
                    MergedAlert ma = mm.get(key);
                    int utype = ai.getUType();
                    if (utype == DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == GONGDING_LEIJI_XIACHEN_EXCEEDING || utype == SHOULIAN_LEIJI_EXCEEDING) {
                        ma.setLeijiAlert(ai);
                    } else {
                        ma.setSulvAlert(ai);
                    }
                } else {
                    MergedAlert ma = new MergedAlert();
                    int utype = ai.getUType();
                    if (utype == DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == GONGDING_LEIJI_XIACHEN_EXCEEDING || utype == SHOULIAN_LEIJI_EXCEEDING) {
                        ma.setLeijiAlert(ai);
                    } else {
                        ma.setSulvAlert(ai);
                    }
                    mm.put(key, ma);
                }
            }
        }
        
        Iterator iter = mm.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            MergedAlert val = (MergedAlert) entry.getValue();
            mal.add(val);
        }
        return mal;
    }

    public static ArrayList<AlertInfo> getAlertInfoList() {
        Log.d(TAG, "getAlertInfoList");

        String prefix = CrtbUtils.getSectionPrefix();

        ArrayList<AlertInfo> l = new ArrayList<AlertInfo>();
        String sql = "SELECT"
                + " AlertList.ID AS alertId,"
//                + " AlertHandlingList.ID AS alertHandlingId,"
//                + " TunnelCrossSectionIndex.Chainage AS chainage,"
                + " AlertList.AlertTime AS date,"
                + " AlertList.PntType AS pntType,"
                + " AlertList.Utype AS utype,"
//                + " AlertHandlingList.AlertStatus AS status,"
                + " AlertList.SheetID AS sheetId,"
                + " AlertList.CrossSectionID AS sectionId,"
                + " AlertList.AlertLevel AS alertLevel,"
                + " AlertList.UValue AS uvalue,"
                + " AlertList.OriginalDataID AS originalDataID,"
                + " AlertList.Info AS info,"
                + " AlertList.UploadStatus AS uploadStatus"
//                + ","
//                + " AlertHandlingList.Handling AS handling,"
//                + " AlertHandlingList.HandlingTime AS handlingTime"
                + " FROM AlertList"
//                + " LEFT JOIN AlertHandlingList"
//                + " ON AlertList.ID=AlertHandlingList.AlertID"
//                + " LEFT JOIN TunnelCrossSectionIndex"
//                + " ON AlertList.CrossSectionID=TunnelCrossSectionIndex.ID"
                + " ORDER BY"
//                + " CASE AlertHandlingList.AlertStatus"
//                + "   WHEN 1 THEN 0"
//                + "   WHEN 2 THEN 1"
//                + "   WHEN 0 THEN 2"
//                + " END DESC,"
                + " AlertList.AlertTime DESC";

        Cursor c = null;
        try {
            c = AlertListDao.defaultDao().executeQuerySQL(sql, null);

            if (c != null) {
                Log.d(TAG, "cursor count: " + c.getCount());
                while (c.moveToNext()) {
                    AlertInfo ai = new AlertInfo();
                    ai.setAlertId(c.getInt(0));
//                    ai.setAlertHandlingId(c.getInt(1));
//                    ai.setXinghao(CrtbUtils.formatSectionName(prefix, c.getDouble(1)));
                    ai.setPntType(c.getString(2));
                    int uType = c.getInt(3);
                    ai.setUType(uType);
                    ai.setUTypeMsg((uType >= GONGDING_LEIJI_XIACHEN_EXCEEDING && uType <= DIBIAO_XIACHEN_SULV_EXCEEDING) ? U_TYPE_MSGS[uType] : "");
//                    int alertStatus = c.getInt(5);
//                    ai.setAlertStatus(alertStatus);
//                    ai.setAlertStatusMsg((alertStatus >= 0 && alertStatus < 3) ? ALERT_STATUS_MSGS[alertStatus]
//                            : "");
                    ai.setSheetId(c.getString(4));
                    ai.setSectionId(c.getString(5));
                    ai.setAlertLevel(c.getInt(6));
                    // Yongdong: UValue should be calculated by raw data, not by AlertList.UValue
                    OtherInfo info = getUVaule(c.getString(8), uType);
                    ai.setUValue(info.mUVaule != null ? info.mUVaule : c.getDouble(7));
                    ai.setDate(info.mDate != null ? info.mDate : c.getString(1));
                    ai.setOriginalDataID(c.getString(8));
                    ai.setAlertInfo(c.getString(9));
                    ai.setUploadStatus(c.getInt(10));
//                    ai.setHandling(c.getString(12));
//                    ai.setHandlingTime(c.getString(13));
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

        for ( AlertInfo ai : l) {

            AlertHandlingList ah = getLatestHandling(ai.getAlertId());
            if (ah != null) {
                ai.setAlertHandlingId(ah.getID());
                int alertStatus = ah.getAlertStatus();
                ai.setAlertStatus(alertStatus);
                ai.setAlertStatusMsg((alertStatus >= ALERT_STATUS_HANDLED && alertStatus <= ALERT_STATUS_HANDLING) ? ALERT_STATUS_MSGS[alertStatus]
                        : "");
                ai.setHandling(ah.getInfo());
                ai.setHandlingTime(CrtbUtils.formatDate(ah.getHandlingTime()));
                ai.setDuePerson(ah.getDuePerson());
            }

            String oriId = ai.getOriginalDataID();
            String dataId = null;
            if (TextUtils.isEmpty(oriId)) {
                continue;
            }
            if (oriId.contains(ORIGINAL_ID_DIVIDER)) {
                String[] ids = oriId.split(ORIGINAL_ID_DIVIDER);
                if (ids != null && ids.length > 0)
                dataId = ids[0];
            } else {
                dataId = oriId;
            }

            int utype = ai.getUType();
            if (utype == DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == DIBIAO_XIACHEN_SULV_EXCEEDING) {
                String secid = ai.getSectionId();
                SubsidenceCrossSectionIndex si = SubsidenceCrossSectionIndexDao.defaultDao().querySectionByGuid(secid);
                if (si != null) {
                    ai.setXinghao(CrtbUtils.formatSectionName(prefix, si.getChainage()));
                    ai.SetRockGrade(si.getROCKGRADE());
                    SubsidenceCrossSectionExIndex siex = SubsidenceCrossSectionExIndexDao.defaultDao().querySectionById(si.getID());
                    if (siex != null) {
                        ai.setSECTCODE(siex.getSECTCODE());
                    }
                }

            } else {
                String secid = ai.getSectionId();
                TunnelCrossSectionIndex si = TunnelCrossSectionIndexDao.defaultDao().querySectionByGuid(secid);
                if (si != null) {
                    ai.setXinghao(CrtbUtils.formatSectionName(prefix, si.getChainage()));
                    ai.SetRockGrade(si.getROCKGRADE());
                    TunnelCrossSectionExIndex siex = TunnelCrossSectionExIndexDao.defaultDao().querySectionById(si.getID());
                    if (siex != null) {
                        ai.setSECTCODE(siex.getSECTCODE());
                    }
                }
                
            }

            if (!TextUtils.isEmpty(dataId)) {
                if (utype == DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == DIBIAO_XIACHEN_SULV_EXCEEDING) {
                    SubsidenceTotalData data = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(dataId);
                    if (data != null) {
                        ai.setCorrection(data.getDataCorrection());
                    }
                } else {
                    TunnelSettlementTotalData data = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(dataId);
                    if (data != null) {
                        ai.setCorrection(data.getDataCorrection());
                    }
                }
            }

            if (ah != null) {
                ai.setChuliFangshi(ah.getHandling()< ALERT_HANDLING.length ? ALERT_HANDLING[ah.getHandling()] : "自由处理");
            } else {
                ai.setChuliFangshi(((ai.getAlertStatus() == ALERT_STATUS_OPEN) && (ai.getCorrection() == 0)) ? "未作任何处理" : "自由处理");
            }

        }
        return l;
    }

    private static AlertHandlingList getLatestHandling(int alertId) {
        List<AlertHandlingList> l = AlertHandlingInfoDao.defaultDao()
                .queryByAlertIdOrderByHandlingTimeDesc(alertId);
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    public static int getAlertCountOfState(int state) {
        int count = 0;
        List<AlertInfo> ls = getAlertInfoList();
        if (ls != null) {
            for (AlertInfo ai : ls) {
                if (ai.getAlertStatus() == state) {
                    count++;
                }
            }
        }

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
     * @param curAlertStatus
     *            处理前的AlertStatus
     * @param alertStatus
     *            要新插入AlertHandlingList表的AlertStatus列
     * @param handling
     *            要新插入AlertHandlingList表的Handling列
     * @param handlingTime
     *            要新插入AlertHandlingList表的HandlingTime列
     */
    public static void handleAlert(int alertId, int dataStatus, boolean isRebury, float correction,
            int curAlertStatus, int alertStatus, String handling, Date handlingTime,String rockGrade) {
        Log.d(TAG, "handleAlert");

        AlertList al = AlertListDao.defaultDao().queryOneById(alertId);

//        double uValue = al.getUValue();
//        int uType = al.getUtype();
//        int tarAlertStatus = curAlertStatus;
        String originalID = al.getOriginalDataId();
        String chainageId = al.getCrossSectionId();
        String pntType = al.getPntType();
        int uType = al.getUType();
        double uValue = al.getUValue();
        String duePerson = AppCRTBApplication.getInstance().mUserName;

        if (!TextUtils.isEmpty(originalID)) {
            ArrayList<String> guids = new ArrayList<String>();
            if (originalID.contains(ORIGINAL_ID_DIVIDER)) {
                String[] idStrs = originalID.split(ORIGINAL_ID_DIVIDER);
                for (String idStr : idStrs) {
                    guids.add(idStr);
                }
            } else {
                guids.add(originalID);
            }

            if (guids.size() == 1) {//测点
                String guid = guids.get(0);
                int id = -1;
                if (pntType.contains("A")) {//隧道内断面
                    TunnelSettlementTotalData p = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guid);
                    if (p != null) {
                        float thisCorrection = correction;
                        if (isRebury) {
                            if (uType == GONGDING_LEIJI_XIACHEN_EXCEEDING) {
                                thisCorrection = (float) (0d - uValue);
                            } else if (uType == GONGDINGI_XIACHEN_SULV_EXCEEDING) {
                                double[] v = getSubsidenceValues(p);
                                if (v != null && v.length == 2) {
                                    thisCorrection = (float) (0d - v[1]);
                                }
                            }
                        }
                        float curCorrection = p.getDataCorrection();
                        // Yongdong: Only the latest correction work. 
//                        float totalCorrection = curCorrection + thisCorrection;
                        float totalCorrection = thisCorrection;
//                        if (!checkExceeding(uValue + totalCorrection, uType)) {
//                            tarAlertStatus = alertStatus;
//                        }
                        TunnelSettlementTotalDataDao.defaultDao().updateDataStatus(guid, dataStatus, totalCorrection);

                        if (dataStatus == POINT_DATASTATUS_AS_FIRSTLINE
                                || dataStatus == POINT_DATASTATUS_CORRECTION) {
                            // STEP 4
                            id = p.getID();
//                            AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);

//                            if (dataStatus == POINT_DATASTATUS_CORRECTION && correction != 0) {
//                                handleSameTunnelPointOtherAlertsByCorrection(p, alertId, correction, handlingTime);
//                            }
                        } else if (dataStatus == POINT_DATASTATUS_DISCARD) {
                            checkPointSubsidenceAlert(p, alertId, handling, handlingTime, false,rockGrade);
                        	//checkPointSubsidenceExceed(p, alertId, handling, handlingTime, false,rockGrade);
                        }
                    }
                } else {//地表沉降
                    SubsidenceTotalData p = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(guid);
                    if (p != null) {
                        float thisCorrection = correction;
                        if (isRebury) {
                            if (uType == DIBIAO_LEIJI_XIACHEN_EXCEEDING) {
                                thisCorrection = (float) (0d - uValue);
                            } else if (uType == DIBIAO_XIACHEN_SULV_EXCEEDING) {
                                double[] v = getSubsidenceValues(p);
                                if (v != null && v.length == 2) {
                                    thisCorrection = (float) (0d - v[1]);
                                }
                            }
                        }
                        float curCorrection = p.getDataCorrection();
                        // Yongdong: Only the latest correction work. 
//                        float totalCorrection = curCorrection + thisCorrection;
                        float totalCorrection = thisCorrection;
//                        if (!checkExceeding(uValue + totalCorrection, uType)) {
//                            tarAlertStatus = alertStatus;
//                        }
                        SubsidenceTotalDataDao.defaultDao().updateDataStatus(guid, dataStatus,
                                totalCorrection);

                        if (dataStatus == POINT_DATASTATUS_AS_FIRSTLINE
                                || dataStatus == POINT_DATASTATUS_CORRECTION) {
                            // STEP 4
                            id = p.getID();
//                            AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);

//                            if (dataStatus == POINT_DATASTATUS_CORRECTION && correction != 0) {
//                                handleSameGroundPointOtherAlertsByCorrection(p, alertId, correction, handlingTime);
//                            }
                        } else if (dataStatus == POINT_DATASTATUS_DISCARD) {
                            checkPointSubsidenceAlert(p, alertId, handling, handlingTime, false,rockGrade);
                        	//checkPointSubsidenceExceed(p, alertId, handling, handlingTime, false,rockGrade);
                        }
                    }
                }

                if (id != -1) {
                    if (dataStatus == POINT_DATASTATUS_AS_FIRSTLINE) {
//                        tarAlertStatus = alertStatus;
                        handleAsFirstLine(alertId, alertStatus, handling, duePerson, handlingTime,
                                chainageId, pntType, id);
                    }
                    updatePointSubsidenceAlertsAfterCorrection(chainageId, pntType, id, alertId, handling, handlingTime,rockGrade);
                }
            } else {//测线
                TunnelSettlementTotalData s_1 = TunnelSettlementTotalDataDao.defaultDao()
                        .queryOneByGuid(guids.get(0));
                String pnt1Type = s_1.getPntType();
                String oppositePntType = pnt1Type.substring(0, pnt1Type.length() - 1) + "2";
                TunnelSettlementTotalData s_2 = TunnelSettlementTotalDataDao.defaultDao()
                        .queryOppositePointOfALine(s_1, oppositePntType);
                //测线的DataCorrection存在第一个点中，即SX_1
//                float curCorrection = s_1.getDataCorrection();
//                float totalCorrection = curCorrection + correction;
                float thisCorrection = correction;
                if (isRebury) {
                    if (uType == SHOULIAN_LEIJI_EXCEEDING) {
                        thisCorrection = (float) (0d - uValue);
                    } else if (uType == SHOULIAN_SULV_EXCEEDING) {
                        double[] v = getLineConvergenceValues(s_1, s_2);
                        if (v != null && v.length == 2) {
                            thisCorrection = (float) (0d - v[1]);
                        }
                    }
                }
                float curCorrection = s_1.getDataCorrection();
                // Yongdong: Only the latest correction work.
//                float totalCorrection = curCorrection + thisCorrection;
                float totalCorrection = thisCorrection;
//                if (!checkExceeding(uValue + totalCorrection, uType)) {
//                    tarAlertStatus = alertStatus;
//                }
                for (String guid : guids) {
                    TunnelSettlementTotalDataDao.defaultDao().updateDataStatus(guid, dataStatus, totalCorrection);
                }
                if (dataStatus == POINT_DATASTATUS_AS_FIRSTLINE
                        || dataStatus == POINT_DATASTATUS_CORRECTION
                        || dataStatus == POINT_DATASTATUS_DISCARD) {
                    // STEP 4

                    if (dataStatus == POINT_DATASTATUS_AS_FIRSTLINE) {
//                        tarAlertStatus = alertStatus;
                        String s1Type = s_1.getPntType();
                        handleAsFirstLine(alertId, alertStatus, handling, duePerson, handlingTime,
                                chainageId, s1Type, s_1.getID());
                        String oppositeType = s1Type.substring(0, s1Type.length() - 1) + "2";
                        handleAsFirstLine(alertId, alertStatus, handling, duePerson, handlingTime,
                                chainageId, oppositeType, s_1.getID());
                    }

//                    AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
                    
					// 原方法是根据测线的一端是查找另一端，并检查数据状态。
					// 由于两个测线的两个端点都存在，没有必要再去查找
					// updateLineConvergenceAlertsAfterCorrection(chainageId,s_1.getPntType(),s_1.getID(), alertId, handling,handlingTime,rockGrade);
                    s_1.setDataCorrection(totalCorrection);
                    s_2.setDataCorrection(totalCorrection);
                    checkLineConvergenceAlert(s_1, s_2, alertId, handling, handlingTime, false, rockGrade);
                } else if (dataStatus == POINT_DATASTATUS_DISCARD) {
                	checkLineConvergenceAlert(s_1, s_2, alertId, handling, handlingTime, false,rockGrade);
                }
            }
        }

        //step 2
        //HANDLED IN updatePointSubsidenceAlertsAfterCorrection() OR updateLineConvergenceAlertsAfterCorrection()
//        AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, handlingTime, duePerson,
//                tarAlertStatus, 1/* true */);

        //TODO: step 3 ignored for now, XXXARCHING（桌面版使用，android可预留设计）
    }

    // 本条数据作为首行，即 将之前数据均设为不参与计算;然后删除本条（包括）之前的数据产生的所有预警信息
    public static void handleAsFirstLine(int alertId, int alertStatus, String handling,
            String duePerson, Date handlingTime, String chainageId, String pntType, int measDataId) {
        if (handling == null) {
            handling = "";
        }
        if (pntType.contains("A") || pntType.contains("S")) {// 隧道内断面
            List<TunnelSettlementTotalData> ls = TunnelSettlementTotalDataDao.defaultDao()
                    .queryInfoBeforeMeasId(chainageId, pntType, measDataId + 1);
            int size = ls.size();
            if (ls != null && size > 0) {
                for (int i = 0; i < size; i++) {
                    TunnelSettlementTotalData p = ls.get(i);
                    List<AlertList> alerts = AlertListDao.defaultDao()
                            .queryTunnelSettlementAlertsByOriginalDataId(p.getID());
                    if (alerts != null && alerts.size() > 0) {
                        for (AlertList alert : alerts) {
                            if (alert != null) {
                                int curAlertId = alert.getId();
                                if (i < size - 1) {
                                    AlertHandlingInfoDao.defaultDao().deleteByAlertId(curAlertId);
                                    AlertListDao.defaultDao().delete(alert);
                                } else {
                                    String handlingRemark = "";
                                    if (alertId >= 0 /*&& alertId == curAlertId*/) {
                                        handlingRemark = handling;
                                    }
                                    AlertHandlingInfoDao.defaultDao()
                                            .insertIfNotExist(curAlertId, handlingRemark, handlingTime,
                                                    duePerson, alertStatus, 0/*false*/);
                                }
                            }
                        }
                    }
                    if (i < size - 1) {
                        p.setDataStatus(POINT_DATASTATUS_DISCARD);
                        TunnelSettlementTotalDataDao.defaultDao().update(p);
                    }
                }
            }
        } else {// 地表沉降
            List<SubsidenceTotalData> ls = SubsidenceTotalDataDao.defaultDao()
                    .queryInfoBeforeMeasId(chainageId, pntType, measDataId + 1);
            int size = ls.size();
            if (ls != null && size > 0) {
                for (int i = 0; i < size; i++) {
                    SubsidenceTotalData p = ls.get(i);
                    List<AlertList> alerts = AlertListDao.defaultDao()
                            .queryGroundSubsidenceAlertsByOriginalDataId(p.getID());
                    if (alerts != null && alerts.size() > 0) {
                        for (AlertList alert : alerts) {
                            if (alert != null) {
                                int curAlertId = alert.getId();
                                AlertHandlingInfoDao.defaultDao().deleteByAlertId(curAlertId);
                                if (i < size - 1) {
                                    AlertListDao.defaultDao().delete(alert);
                                } else if (curAlertId != alertId) {//alertId 在handleAlert中已处理
                                    AlertHandlingInfoDao.defaultDao()
                                            .insertItem(curAlertId, handling, handlingTime,
                                                    duePerson, alertStatus, 0/*false*/);
                                }
                            }
                        }
                    }
                    if (i < size - 1) {
                        p.setDataStatus(POINT_DATASTATUS_DISCARD);
                        SubsidenceTotalDataDao.defaultDao().update(p);
                    }
                }
            }
        }
    }

    private static void updatePointSubsidenceAlertsAfterCorrection(String chainageId, String pntType,
            int measId, int curHandlingAlertId, String handling, Date handlingTime,String rockGrade) {
        Log.d(TAG, "updatePointSubsidenceAlertsAfterCorrection, pntType: " + pntType);
        if (pntType.contains("A")) {//隧道内断面
            List<TunnelSettlementTotalData> ls = TunnelSettlementTotalDataDao.defaultDao()
                    .queryInfoAfterMeasId(chainageId, pntType, measId);
            if (ls != null && ls.size() > 0) {
                for (TunnelSettlementTotalData p : ls) {
                    String currentHandling = "";
                    if (p.getID() == measId) {
                        currentHandling = handling;
                    } else {
                        AlertListDao.defaultDao().deleteAlert(p.getSheetId(), chainageId, p.getGuid());
                        TunnelSettlementTotalDataDao.defaultDao().updateDataStatus(p.getGuid(), AlertUtils.POINT_DATASTATUS_NONE, 0);
                        p = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(p.getGuid());
                    }
                    checkPointSubsidenceAlert(p, curHandlingAlertId, handling, handlingTime, false,rockGrade);
                    //checkPointSubsidenceExceed(p, curHandlingAlertId, handling, handlingTime, false,rockGrade);
                }
            }
        } else {//地表沉降
            List<SubsidenceTotalData> ls = SubsidenceTotalDataDao.defaultDao()
                    .queryInfoAfterMeasId(chainageId, pntType, measId);
            if (ls != null && ls.size() > 0) {
                for (SubsidenceTotalData p : ls) {
                    String currentHandling = "";
                    if (p.getID() == measId) {
                        currentHandling = handling;
                    } else {
                        AlertListDao.defaultDao().deleteAlert(p.getSheetId(), chainageId, p.getGuid());
                        SubsidenceTotalDataDao.defaultDao().updateDataStatus(p.getGuid(), AlertUtils.POINT_DATASTATUS_NONE, 0);
                        p = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(p.getGuid());
                    }
                    checkPointSubsidenceAlert(p, curHandlingAlertId, handling, handlingTime, false,rockGrade);
                    //checkPointSubsidenceExceed(p, curHandlingAlertId, handling, handlingTime, false, rockGrade);
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
                if (p != null && p.getDataStatus() == 3) {
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
                if (p != null && p.getDataStatus() == 3) {
                    s += p.getDataCorrection();
                }
            }
        }
        return s;
    }

    public static class Exceeding {
        int leijiType = -1;
        int sulvType = -1;
        double leijiValue = -1;
        double sulvValue = -1;
        
        TunnelSettlementTotalData tunnelData;
    	SubsidenceTotalData subData;
    	boolean isTransfinite;
    	double accumulativeSubsidence;
    	int uType;
    	String originalDataID;
    }

    public static double getLineConvergenceExceedTime(TunnelSettlementTotalData s_1,
            TunnelSettlementTotalData s_2) {
        double ret = 0;
        if (s_1 == null || s_2 == null) {
            return ret;
        }

        Date s_1ThisTime = s_1.getSurveyTime();
        Date s_2ThisTime = s_2.getSurveyTime();
        double thisTime = -1;
        if (s_1ThisTime != null && s_2ThisTime != null) {

            Date thisTimeDate = s_1ThisTime;
            thisTime = s_1ThisTime.getTime();
            if (s_2ThisTime.getTime() > thisTime) {
                thisTime = s_2ThisTime.getTime();
                thisTimeDate = s_2ThisTime;
            }
        }

        String chainageId = s_1.getChainageId();

        

        List<TunnelSettlementTotalData> s_1InfoList = TunnelSettlementTotalDataDao.defaultDao()
                .queryInfoBeforeMeasId(chainageId, s_1.getPntType(), s_1.getID());
        if (s_1InfoList != null && s_1InfoList.size() > 0) {

            int s = s_1InfoList.size();
            String s_2pntType = s_2.getPntType();

            TunnelSettlementTotalData s_1First = null;
            TunnelSettlementTotalData s_2First = null;
            for (int i = 0; i < s; i++) {
                s_1First = s_1InfoList.get(i);
                s_2First = TunnelSettlementTotalDataDao.defaultDao()
                        .queryOppositePointOfALine(s_1First, s_2pntType);
                if (s_1First != null && s_2First != null) {
                    break;
                }
            }

            TunnelSettlementTotalData s_1Last = null;
            TunnelSettlementTotalData s_2Last = null;
            for (int i = 1; i <= s; i++) {
                s_1Last = s_1InfoList.get(s - i);
                s_2Last = TunnelSettlementTotalDataDao.defaultDao()
                        .queryOppositePointOfALine(s_1Last, s_2pntType);
                if (s_1Last != null && s_2Last != null) {
                    break;
                }
            }
            if (s_1Last != null && s_2Last != null && thisTime != -1) {
//                deltaLenth = Math.abs(deltaLenth);
                Date s_1LastTime = s_1Last.getSurveyTime();
                Date s_2LastTime = s_2Last.getSurveyTime();
                if (s_1LastTime == null || s_2LastTime == null) {
                    return ret;
                }
                double lastTime = s_1LastTime.getTime();
                if (s_2LastTime.getTime() > lastTime) {
                    lastTime = s_2LastTime.getTime();
                }
                double deltaTime = Math.abs((thisTime - lastTime));
                if (deltaTime < Time.ONE_SECOND) {
                    deltaTime = Time.ONE_SECOND;//ONE SECOND at least to avoid infinity
                }
                double deltaTInDay = (deltaTime / Time.DAY_MILLISECEND_RATIO);
                return deltaTInDay;
            }
        }

        return ret;
    }

    public static double getDeltaTime(AlertInfo currentAlert) {
//      AlertInfo currentAlert = alerts.get(clickedItem);
      Date thisTime = null;
      List pastInfoList = null;
      int type = 0;
      if (currentAlert == null) {
          return 1;
      }
      String originalID = currentAlert.getOriginalDataID();
      if (!TextUtils.isEmpty(originalID)) {
          ArrayList<String> guids = new ArrayList<String>();
          if (originalID.contains(AlertUtils.ORIGINAL_ID_DIVIDER)) {
              String[] idStrs = originalID.split(AlertUtils.ORIGINAL_ID_DIVIDER);
              for (String idStr : idStrs) {
                  guids.add(idStr);
              }
          } else {
              guids.add(originalID);
          }

          if (guids.size() == 1) {//测点
              String guid = guids.get(0);
              int measNo = -1;
              if (currentAlert.getPntType().contains("A")) {//隧道内断面
                  type = 1;
                  TunnelSettlementTotalData tPoint = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guid);
                  if (tPoint != null) {
                      pastInfoList = TunnelSettlementTotalDataDao.defaultDao().queryInfoBeforeMeasId(
                              tPoint.getChainageId(), tPoint.getPntType(), tPoint.getID());
                      thisTime = tPoint.getSurveyTime();
                  }
              } else {
                  // Subsidence
                  type = 2;
                  SubsidenceTotalData sPoint = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(guid);
                  if (sPoint != null) {
                      pastInfoList = SubsidenceTotalDataDao.defaultDao().queryInfoBeforeMeasId(
                              sPoint.getChainageId(), sPoint.getPntType(), sPoint.getID());
                      thisTime = sPoint.getSurveyTime();
                  }
              }
          } else {
              TunnelSettlementTotalData s_1 = TunnelSettlementTotalDataDao.defaultDao()
                      .queryOneByGuid(guids.get(0));
              String pnt1Type = s_1.getPntType();
              String oppositePntType = pnt1Type.substring(0, pnt1Type.length() - 1) + "2";
              TunnelSettlementTotalData s_2 = TunnelSettlementTotalDataDao.defaultDao()
                      .queryOppositePointOfALine(s_1, oppositePntType);
              return AlertUtils.getLineConvergenceExceedTime(s_1, s_2);
          }
      } 

      if (pastInfoList != null && pastInfoList.size() > 0) {
          Object lastInfo = pastInfoList.get(pastInfoList.size() - 1);
          String[] lastCoords = null;
          Date lastTime = null;
          if (type == 1) {
              lastCoords = ((TunnelSettlementTotalData) lastInfo).getCoordinate().split(",");
              lastTime = ((TunnelSettlementTotalData) lastInfo).getSurveyTime();
//          thisDataCorrection = ((TunnelSettlementTotalData) lastInfo).getDataCorrection();
          } else if (type == 2) {
              lastCoords = ((SubsidenceTotalData) lastInfo).getCoordinate().split(",");
              lastTime = ((SubsidenceTotalData) lastInfo).getSurveyTime();
//          thisDataCorrection = ((SubsidenceTotalData) lastInfo).getDataCorrection();
          }
          
          if (thisTime != null && lastTime != null) {
              long deltaT = Math.abs(thisTime.getTime() - lastTime.getTime());
              Log.d(TAG, "delta t: " + deltaT + " ms");
              if (deltaT < Time.ONE_SECOND) {
                  deltaT = Time.ONE_SECOND;//ONE SECOND at least to avoid infinity
              }
              double deltaTInDay = ((double)deltaT / Time.DAY_MILLISECEND_RATIO);
              return deltaTInDay;
          }
      }
      return 1;
  }

    public static boolean isSpeed(int uType) {
        return uType == GONGDINGI_XIACHEN_SULV_EXCEEDING || uType == SHOULIAN_SULV_EXCEEDING || uType == DIBIAO_XIACHEN_SULV_EXCEEDING;
    }

    /**
     * 
     * @param sheetID 记录单GUID
     * @param chainageID 断面GUID
     * @param surveyValue 测量值
     * @param uType 累计or速率
     * @return 报警等级 只能返回正整数1,2,3；其中1表示管理等级为1级，依此类推
     */
    private static int GetManagementLevel(
            String sheetID, String chainageID, double surveyValue, int uType) {
        double maxU0 = -1;
        double currentChainage = 0;
        double excavationWidth = 0;
        double excavationChainage = RawSheetIndexDao.defaultDao().queryOneByGuid(sheetID)
                .getFACEDK();

        switch (uType) {
            case GONGDING_LEIJI_XIACHEN_EXCEEDING: {
                TunnelCrossSectionIndex sectionInfo = TunnelCrossSectionIndexDao.defaultDao()
                        .querySectionIndexByGuid(
                                chainageID);
                maxU0 = sectionInfo.getGDU0();
                currentChainage = sectionInfo.getChainage();
                excavationWidth = sectionInfo.getWidth();
            }
                break;
            case GONGDINGI_XIACHEN_SULV_EXCEEDING: {
                TunnelCrossSectionIndex sectionInfo = TunnelCrossSectionIndexDao.defaultDao()
                        .querySectionIndexByGuid(
                                chainageID);
                maxU0 = sectionInfo.getGDVelocity();
                currentChainage = sectionInfo.getChainage();
                excavationWidth = sectionInfo.getWidth();
            }
                break;
            case SHOULIAN_LEIJI_EXCEEDING: {
                TunnelCrossSectionIndex sectionInfo = TunnelCrossSectionIndexDao.defaultDao()
                        .querySectionIndexByGuid(
                                chainageID);
                maxU0 = sectionInfo.getSLU0();
                currentChainage = sectionInfo.getChainage();
                excavationWidth = sectionInfo.getWidth();
            }
                break;

            case SHOULIAN_SULV_EXCEEDING: {
                TunnelCrossSectionIndex sectionInfo = TunnelCrossSectionIndexDao.defaultDao()
                        .querySectionIndexByGuid(
                                chainageID);
                maxU0 = sectionInfo.getSLLimitVelocity();
                currentChainage = sectionInfo.getChainage();
                excavationWidth = sectionInfo.getWidth();
            }
                break;

            case DIBIAO_LEIJI_XIACHEN_EXCEEDING: {
                SubsidenceCrossSectionIndex sectionInfo = SubsidenceCrossSectionIndexDao
                        .defaultDao().querySectionByGuid(chainageID);
                maxU0 = sectionInfo.getDBU0();
                currentChainage = sectionInfo.getChainage();
                excavationWidth = sectionInfo.getWidth();
            }
                break;
            case DIBIAO_XIACHEN_SULV_EXCEEDING: {
                SubsidenceCrossSectionIndex sectionInfo = SubsidenceCrossSectionIndexDao
                        .defaultDao().querySectionByGuid(chainageID);
                maxU0 = sectionInfo.getDBLimitVelocity();
                currentChainage = sectionInfo.getChainage();
                excavationWidth = sectionInfo.getWidth();
            }
                break;

        }

        if (maxU0 < 0) {
            return 3;
        }
        surveyValue = Math.abs(surveyValue);
        int managementLevel = 1;
        double limitValue = maxU0; // mm
        double deltaChainage = Math.abs(currentChainage - excavationChainage);
        /*
         * <uint is meter; absolute distance between currentChainage and
         * ExcavationChainage>.
         */

        if (deltaChainage <= excavationWidth) {
            limitValue = 0.65 * maxU0;
        } else if (deltaChainage > excavationWidth && deltaChainage <= 2 * excavationWidth) {
            limitValue = 0.9 * maxU0;
        }

        if (surveyValue < limitValue / 3.0) {
            managementLevel = 3;
        } else if (surveyValue > (2.0 * limitValue / 3.0)) {
            managementLevel = 1;
        } else {
            managementLevel = 2;
        }
        return managementLevel;
    }

    public static class OtherInfo {
        Double mUVaule = null;
        String mDate = null;
    }

    private static OtherInfo getUVaule(String dataGuid, int uType) {
        OtherInfo info = new OtherInfo();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (uType == GONGDINGI_XIACHEN_SULV_EXCEEDING || uType == DIBIAO_LEIJI_XIACHEN_EXCEEDING
                || uType == DIBIAO_XIACHEN_SULV_EXCEEDING
                || uType == GONGDING_LEIJI_XIACHEN_EXCEEDING) {
            Object point = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(dataGuid);
            if (point == null) {
                point = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(dataGuid);
                info.mDate = format.format(((SubsidenceTotalData)point).getSurveyTime());
            } else {
                info.mDate = format.format(((TunnelSettlementTotalData)point).getSurveyTime());
            }
            
            Exceeding ex = getPointSubsidenceExceed(point, -1, null, null);
            if (uType == GONGDINGI_XIACHEN_SULV_EXCEEDING || uType == DIBIAO_XIACHEN_SULV_EXCEEDING) {
                info.mUVaule = ex.sulvValue;
            } else {
                info.mUVaule = ex.leijiValue;
            }
            return info;
        }

        if (uType == SHOULIAN_LEIJI_EXCEEDING || uType == SHOULIAN_SULV_EXCEEDING) {
            TunnelSettlementTotalData s_1 = null;
            TunnelSettlementTotalData s_2 = null;
            if (dataGuid != null) {
                String[] guids = dataGuid.split(",");
                if (guids.length > 1) {
                    s_1 = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guids[0]);
                    s_2 = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guids[1]);
                }
            }

            if (s_1.getSurveyTime().getTime() >  s_2.getSurveyTime().getTime()) {
                info.mDate = format.format(s_1.getSurveyTime());
            } else {
                info.mDate = format.format(s_2.getSurveyTime());
            }

            Exceeding ex2 = getLineConvergenceExceed(s_1, s_2, -1, null, null);
            if (uType == SHOULIAN_LEIJI_EXCEEDING){
                info.mUVaule = ex2.leijiValue;
            } else {
                info.mUVaule = ex2.sulvValue;
            }
            return info;
        }
        return info;
    }

    /**
     * 位移管理等级
     * @author xu
     *
     */
    public static class OffsetLevel{
    	public String Content;
    	public int TextColor;
    	public boolean IsLargerThanMaxValue;
    	/**
    	 * 类型值：用于构造内容的前缀
    	 */
    	public int PreType;
    	
    	/**
    	 * 测量值
    	 */
    	public double Value;
    	
    	/**
    	 * 是否超限
    	 */
    	public boolean IsTransfinite;
    	
    	/**
    	 * 超限等级
    	 */
    	public int TransfiniteLevel;
    }
    
    /**
     * 检查位移等级
     * @param value 测量值
     * @param rockGrade 围岩等级：I、II……
     * @param isLeiji 是否为累积
     * @return 位移等级
     */
    public static OffsetLevel[] checkOffsetLevel(Exceeding exceeding,String rockGrade){
		OffsetLevel[] offsetList = new OffsetLevel[2];
		OffsetLevel leiji = new OffsetLevel();
		OffsetLevel sulv = new OffsetLevel();
		StringBuilder sbLeiJi = new StringBuilder();
		StringBuilder sbSulv = new StringBuilder();
		double value;

		if (exceeding == null) {
			return null;
		}

		//累积
		
		leiji.Value = exceeding.leijiValue;
		leiji.PreType = exceeding.leijiType;
		value = Math.abs(exceeding.leijiValue);
		int levelBase = Constant.LEI_JI_OFFSET_LEVEL_BASE[CrtbUtils.getRockgrade(rockGrade)];
		if (value < levelBase) {
			leiji.IsTransfinite = false;
			leiji.TransfiniteLevel = 0;
		} else if (value <= 2 * levelBase) {
			leiji.IsTransfinite = true;
			leiji.TransfiniteLevel = 1;
		} else {
			leiji.IsTransfinite = true;
			leiji.TransfiniteLevel = 2;
		}

		if (value >= SULV_ALARM_MAX_VALUE) {
			leiji.IsLargerThanMaxValue = true;
		} else {
			leiji.IsLargerThanMaxValue = false;
		}

		if (leiji.PreType > -1) {
			leiji.TextColor = leijiOffsetLevelColor[leiji.TransfiniteLevel];
			if (leiji.IsTransfinite) {
				sbLeiJi.append(U_TYPE_MSGS[leiji.PreType]).append(" ").append(leiji.Value).append("毫米");
			} else {
				sbLeiJi.append(U_TYPE_MSGS_SAFE[leiji.PreType]).append(" ").append(leiji.Value).append("毫米");
			}
			leiji.Content = sbLeiJi.toString();
		}
		offsetList[0] = leiji;
		
		//速率
		sulv.Value = exceeding.sulvValue;
		sulv.PreType = exceeding.sulvType;
		if (Math.abs(exceeding.sulvValue) < SPEED_THRESHOLD) {
			sulv.IsTransfinite = false;
			sulv.TransfiniteLevel = 0;
		} else {
			sulv.IsTransfinite = true;
			sulv.TransfiniteLevel = 1;
		}
		if (sulv != null && sulv.PreType > 0) {
			sulv.TextColor = sulvOffsetLevelColor[sulv.TransfiniteLevel];
			if (sulv.IsTransfinite) {
				sbSulv.append(U_TYPE_MSGS[sulv.PreType]).append(" ").append(sulv.Value).append("毫米");
			} else {
				sbSulv.append(U_TYPE_MSGS_SAFE[sulv.PreType]).append(" ").append(sulv.Value).append("毫米");
			}
			sulv.Content = sbSulv.toString();
		}
		offsetList[1] = sulv;
		
        return offsetList;
    }
    
    public static String [] getPointAlertValue(Object point, int curHandlingAlertId, String handling, Date handlingTime){
    	String [] alertValue = null;
    	Exceeding exceccding = getPointSubsidenceExceed(point, curHandlingAlertId, handling, handlingTime);
        if(exceccding != null){
        	alertValue = decodeExceeding(exceccding);
        }
        return alertValue;
    }
    
    public static String [] getLineAlertValue(TunnelSettlementTotalData s_1,TunnelSettlementTotalData s_2,
    		int curHandlingAlertId, String handling, Date handlingTime){
    	String [] alertValue = null;
    	Exceeding exceccding = getLineConvergenceExceed(s_1,s_2, curHandlingAlertId, handling, handlingTime);
        if(exceccding != null){
        	alertValue = decodeExceeding(exceccding);
        }
        return alertValue;
    }
    
    /**
     * 解析超限值，获取报警信息
     * @param exceeding
     * @return
     */
     private static String[] decodeExceeding(Exceeding exceeding){
		String[] alertValue = new String[2];
		StringBuilder sbLeiJi = new StringBuilder();
		StringBuilder sbSulv = new StringBuilder();

		if (exceeding == null) {
			return null;
		}

		//累积
		if (exceeding.leijiType > -1) {
			sbLeiJi.append(U_TYPE_MSGS[exceeding.leijiType]).append(" ").append(exceeding.leijiValue).append("毫米");
			alertValue[0] = sbLeiJi.toString();
		}
		
		//速率
		if (exceeding.sulvType > 0) {
			sbSulv.append(U_TYPE_MSGS[exceeding.sulvType]).append(" ").append(exceeding.sulvValue).append("毫米");
			alertValue[1] = sbSulv.toString();
		}
		
        return alertValue;
    }
}
