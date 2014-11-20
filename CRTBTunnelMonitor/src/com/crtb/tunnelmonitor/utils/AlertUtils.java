
package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
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
import com.crtb.tunnelmonitor.infors.Exceeding;
import com.crtb.tunnelmonitor.infors.OffsetLevel;
import com.crtb.tunnelmonitor.utils.WarningUtils.UploadCallBack;
import com.crtb.tunnelmonitor.common.Constant;


public class AlertUtils {

    private static final String TAG = "AlertUtils";  

    /**
     * @param point
     * @param readOnly 为true时该方法不会对数据库做出任何修改，只返回预警信息
     * @return
     */
    public static void getPointSubsidenceExceedMsg(Object point, boolean readOnly, String rockGrade,Context context,UploadFinishCallBack pCaller) {
        checkPointSubsidenceAlert(point, -1, -1, null, null, readOnly,rockGrade,context,pCaller);
    }
    
    /**
     * @param s_1
     * @param s_2
     * @param readOnly 为true时该方法不会对数据库做出任何修改，只返回预警信息
     * @return
     */
	public static void getLineConvergenceExceedMsg(TunnelSettlementTotalData s_1, TunnelSettlementTotalData s_2, boolean readOnly, String rockGrade,Context context,UploadFinishCallBack pCaller) {
		checkLineConvergenceAlert(s_1, s_2, -1, -1, null, null, readOnly, rockGrade,context,pCaller);
	}

    public static String getAlertValueUnit(int alertType) {
        switch (alertType) {
        case Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING:
        case Constant.SHOULIAN_LEIJI_EXCEEDING:
        case Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING:
            return "毫米";
        case Constant.GONGDINGI_XIACHEN_SULV_EXCEEDING:
        case Constant.SHOULIAN_SULV_EXCEEDING:
        case Constant.DIBIAO_XIACHEN_SULV_EXCEEDING:
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
    private static Exceeding getPointSubsidenceExceed(Object point, int curHandlingAlertId, int handling, String info, Date handlingTime,boolean needOriginalData) {
    	Log.d(Constant.LOG_TAG,TAG + "checkPointSubsidenceExceed");
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

        int dataStatus = Constant.POINT_DATASTATUS_NONE;

        CrownSettlementARCHING cArc = null;
        SubsidenceSettlementARCHING sArc = null;

        if (point instanceof TunnelSettlementTotalData) {
            TunnelSettlementTotalData tPoint = (TunnelSettlementTotalData) point;
            dataStatus = tPoint.getDataStatus();
            if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE && !needOriginalData) {
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
            if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE && !needOriginalData) {
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

        if (curHandlingAlertId > 0 && dataStatus == Constant.POINT_DATASTATUS_DISCARD) {
            List<AlertList> l = AlertListDao.defaultDao().queryByOrigionalDataId(sheetId,
                    chainageId, originalDataID);
            for (AlertList al : l) {
                if (al != null) {
                    int aid = al.getId();
                    AlertHandlingInfoDao
                            .defaultDao()
                            .insertIfNotExist(aid, handling, info, handlingTime,
                                    AppCRTBApplication.getInstance().mUserName, Constant.ALERT_STATUS_HANDLED, 0/*false*/);
                }
            }
            return ret;
        }

        if (pastInfoList != null && pastInfoList.size() > 0) {

            Object firstInfo = pastInfoList.get(0);

            if (!TextUtils.isEmpty(thisCoords[2])) {
                double thisZ = Double.valueOf(thisCoords[2]);
                Log.d(Constant.LOG_TAG,TAG +  "this z: " + thisZ + " m");
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
                    Log.d(Constant.LOG_TAG,TAG +  "first z:" + firstZ + " m");
                    double accumulativeSubsidence = firstZ - thisZ;
                    accumulativeSubsidence *= 1000;//CHANGE TO MILLIMETER
                    Log.d(Constant.LOG_TAG,TAG +  "累计沉降: " + accumulativeSubsidence + " mm");
                    accumulativeSubsidence += sumOfDataCorrection;
//                    accumulativeSubsidence = Math.abs(accumulativeSubsidence);
                    int uType = type == 1 ? Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING
                            : Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING;
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
                    Log.d(Constant.LOG_TAG,TAG +  "last z: " + lastZ + " m");
                    double deltaZ = lastZ - thisZ;
                    Log.d(Constant.LOG_TAG,TAG +  "delta z: " + deltaZ + " m");
                    deltaZ *= 1000;//CHANGE TO MILLIMETER
                    ret.originalSulvAlertValue = deltaZ;
                    deltaZ += thisDataCorrection;
//                    deltaZ = Math.abs(deltaZ);
                    long deltaT = Math.abs(thisTime.getTime() - lastTime.getTime());
                    Log.d(Constant.LOG_TAG,TAG +  "delta t: " + deltaT + " ms");
                    if (deltaT < Time.ONE_SECOND) {
                        deltaT = Time.ONE_SECOND;//ONE SECOND at least to avoid infinity
                    }
                    double deltaTInDay = ((double)deltaT / Time.DAY_MILLISECEND_RATIO);
                    Log.d(Constant.LOG_TAG,TAG +  "delta t in day: " + deltaTInDay + " day");
                    double subsidenceSpeed = deltaZ / deltaTInDay;
                    Log.d(Constant.LOG_TAG,TAG +  "沉降速率: " + subsidenceSpeed + " mm/d");
                    int uType = type == 1 ? Constant.GONGDINGI_XIACHEN_SULV_EXCEEDING
                            : Constant.DIBIAO_XIACHEN_SULV_EXCEEDING;
                    subsidenceSpeed = CrtbUtils.formatDouble(subsidenceSpeed, 1);
                    ret.sulvType = uType;
                    ret.sulvValue = subsidenceSpeed;
                    ret.originalSulvAlertValue = ret.originalSulvAlertValue / deltaTInDay;
					ret.originalSulvAlertValue = CrtbUtils.formatDouble(ret.originalSulvAlertValue,1);
                }
            }

        }
        
        if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE && needOriginalData) {
        	ret.sulvValue = 0;
        	ret.leijiValue = 0;
        }
        
        if (dataStatus == Constant.POINT_DATASTATUS_DISCARD && needOriginalData) {
        	ret.sulvValue = 0;
        	ret.leijiValue = 0;
        }
        
        ret.originalDataID = originalDataID;
        return ret;
    }

    public static OffsetLevel[] checkPointSubsidenceAlert(final Object point, final int curHandlingAlertId, final int handling, final String info,final Date handlingTime,boolean readOnly,String rockGrade,final Context context,final UploadFinishCallBack pCaller){
		final Exceeding excecding = getPointSubsidenceExceed(point, curHandlingAlertId, handling, info, handlingTime, false);
		if (excecding != null) {
			final OffsetLevel[] offsetLevel = WarningUtils.checkOffsetLevel(excecding, rockGrade);
			
			if (!readOnly) {
				if (context != null && offsetLevel[Constant.LEI_JI_INDEX].IsLargerThanMaxValue) {
					WarningUtils.judgeTransfinite(context, new UploadCallBack() {

						@Override
						public void done(boolean isUploading) {
							if (isUploading && offsetLevel != null) {
								updatePointSubsidence(excecding.tunnelData, excecding.subData, offsetLevel, excecding.originalDataID, handlingTime, handling, info, curHandlingAlertId);
							}
							pCaller.Finish(offsetLevel, isUploading);
						}
					});
				} else {
					updatePointSubsidence(excecding.tunnelData, excecding.subData, offsetLevel, excecding.originalDataID, handlingTime, handling, info, curHandlingAlertId);
					if (pCaller != null) {
						pCaller.Finish(offsetLevel, false);
					}
				}
			}
			else {
				pCaller.Finish(offsetLevel, false);
			}
			return offsetLevel;
		}
		
		return null;
    }
    
	private static void updatePointSubsidence(TunnelSettlementTotalData tunnelData, SubsidenceTotalData subData,OffsetLevel[] offsetLevel, String originalDataID, Date handlingTime, int handling, String info, int curHandlingAlertId) {
		int alertLevelLeiJi = offsetLevel[Constant.LEI_JI_INDEX].TransfiniteLevel;
		boolean isTransfiniteLeiJi = offsetLevel[Constant.LEI_JI_INDEX].IsTransfinite;
		double accumulativeSubsidence = offsetLevel[Constant.LEI_JI_INDEX].Value;
		int uTypeLeiJi = offsetLevel[Constant.LEI_JI_INDEX].PreType;
		
		int alertLevelSuLv = offsetLevel[Constant.SU_LV_INDEX].TransfiniteLevel;
		boolean isTransfiniteSuLv = offsetLevel[Constant.SU_LV_INDEX].IsTransfinite;
		double speedSubsidence = offsetLevel[Constant.SU_LV_INDEX].Value;
		int uTypeSuLv = offsetLevel[Constant.SU_LV_INDEX].PreType;
		
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

		//累积超限
		if (isTransfiniteLeiJi) {
			if (tunnelData != null) {
				alertId = AlertListDao.defaultDao().insertOrUpdate(tunnelData, alertLevelLeiJi, uTypeLeiJi, accumulativeSubsidence, Constant.ACCUMULATIVE_THRESHOLD, originalDataID);
			} else {
				alertId = AlertListDao.defaultDao().insertOrUpdate(subData, alertLevelLeiJi, uTypeLeiJi, accumulativeSubsidence, Constant.ACCUMULATIVE_THRESHOLD, originalDataID);
			}
			
			String handlingRemark = "";
			if (curHandlingAlertId >= 0 /* && alertId == curHandlingAlertId */) {
				handlingRemark = info;
			}

			if (curHandlingAlertId < 0) {// 重测，删除原来数据的报警信息
				AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
			}

			AlertHandlingInfoDao.defaultDao().insertItem(alertId,handling, handlingRemark, new Date(System.currentTimeMillis()), duePerson, Constant.ALERT_STATUS_OPEN/* 报警 */, 0/* false */);

		} 
		//没有超限
		else {
			AlertList alert = AlertListDao.defaultDao().queryOne(sheetId, chainageId, originalDataID, uTypeLeiJi);
			if (alert != null) {
				alertId = alert.getId();
				if (curHandlingAlertId >= 0) {
					AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, info, handlingTime, duePerson, Constant.ALERT_STATUS_HANDLED, 1/* true */);
					if (tunnelData != null) {
						alertId = AlertListDao.defaultDao().insertOrUpdate(tunnelData, alertLevelLeiJi, uTypeLeiJi, accumulativeSubsidence, Constant.ACCUMULATIVE_THRESHOLD, originalDataID);
					} else {
						alertId = AlertListDao.defaultDao().insertOrUpdate(subData, alertLevelLeiJi, uTypeLeiJi, accumulativeSubsidence, Constant.ACCUMULATIVE_THRESHOLD, originalDataID);
					}
				} else if (alert.getUploadStatus() != 2) {
					AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
					AlertListDao.defaultDao().deleteById(alertId);
				}
			}
		}
		
		// 速率超限
		if (isTransfiniteSuLv) {
			AlertList al = AlertListDao.defaultDao().queryOne(sheetId, chainageId, originalDataID, uTypeSuLv);
			if (tunnelData != null) {
				alertId = AlertListDao.defaultDao().insertOrUpdate(tunnelData, alertLevelSuLv, uTypeSuLv, speedSubsidence, Constant.SPEED_THRESHOLD, originalDataID);
			} else {
				alertId = AlertListDao.defaultDao().insertOrUpdate(subData, alertLevelSuLv, uTypeSuLv, speedSubsidence, Constant.SPEED_THRESHOLD, originalDataID);
			}
			String handlingRemark = "";
			if (curHandlingAlertId >= 0 ) {
				handlingRemark = info;
			}

			if (curHandlingAlertId < 0) {// 重测
				AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
			}

			AlertHandlingInfoDao.defaultDao().insertItem(alertId,handling, handlingRemark, new Date(System.currentTimeMillis()), duePerson, Constant.ALERT_STATUS_OPEN/* 报警 */, 0/* false */);
		}
		// 没有超限
		else {

			AlertList al = AlertListDao.defaultDao().queryOne(sheetId, chainageId, originalDataID, uTypeSuLv);
			if (al != null) {
				alertId = al.getId();
				if (curHandlingAlertId >= 0) {// 处理
					AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, info, handlingTime, duePerson, Constant.ALERT_STATUS_HANDLED, 1/* true */);
					if (tunnelData != null) {
						alertId = AlertListDao.defaultDao().insertOrUpdate(tunnelData, alertLevelSuLv, uTypeSuLv, speedSubsidence, Constant.SPEED_THRESHOLD, originalDataID);
					} else {
						alertId = AlertListDao.defaultDao().insertOrUpdate(subData, alertLevelSuLv, uTypeSuLv, speedSubsidence, Constant.SPEED_THRESHOLD, originalDataID);
					}
				} else if (al.getUploadStatus() != 2) {// 重测
					AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
					AlertListDao.defaultDao().deleteById(alertId);
				}
			}
		}
	}
    
	public static OffsetLevel[] checkLineConvergenceAlert(final TunnelSettlementTotalData s_1,final TunnelSettlementTotalData s_2,final int curHandlingAlertId,final int handling, final String info,final Date handlingTime,final boolean readOnly,String rockGrade,final Context context,final UploadFinishCallBack pCaller){
        
		final Exceeding excecding = getLineConvergenceExceed(s_1, s_2, curHandlingAlertId, handling, info, handlingTime, false);
		if (excecding != null) {
			final OffsetLevel[] offsetLevel = WarningUtils.checkOffsetLevel(excecding, rockGrade);
			
			if (!readOnly) {
				if (context != null && offsetLevel[Constant.LEI_JI_INDEX].IsLargerThanMaxValue) {
					WarningUtils.judgeTransfinite(context, new UploadCallBack() {

						@Override
						public void done(boolean isUploading) {
							if (isUploading && offsetLevel != null) {
								updateLineConvergence(s_1, s_2, offsetLevel, excecding.originalDataID, handlingTime, handling, info, curHandlingAlertId);
							}
							pCaller.Finish(offsetLevel, isUploading);
						}
					});
				} else {
					updateLineConvergence(s_1, s_2, offsetLevel, excecding.originalDataID, handlingTime, handling, info, curHandlingAlertId);
					if (pCaller != null) {
						pCaller.Finish(offsetLevel, false);
					}
				}
			} else {
				pCaller.Finish(offsetLevel, false);
			}
			return offsetLevel;
		}
		
		return null;
    }
		
	private static void updateLineConvergence(TunnelSettlementTotalData s_1, TunnelSettlementTotalData s_2, OffsetLevel[] offsetLevel, String originalDataID, Date handlingTime, int handling, String info, int curHandlingAlertId) {
		int alertLevelLeiJi = offsetLevel[Constant.LEI_JI_INDEX].TransfiniteLevel;
		boolean isTransfiniteLeiJi = offsetLevel[Constant.LEI_JI_INDEX].IsTransfinite;
		double convergence = offsetLevel[Constant.LEI_JI_INDEX].Value;
		int uTypeLeiJi = offsetLevel[Constant.LEI_JI_INDEX].PreType;
		
		int alertLevelSuLv = offsetLevel[Constant.SU_LV_INDEX].TransfiniteLevel;
		boolean isTransfiniteSuLv = offsetLevel[Constant.SU_LV_INDEX].IsTransfinite;
		double shoulianSpeed = offsetLevel[Constant.SU_LV_INDEX].Value;
		int uTypeSuLv = offsetLevel[Constant.SU_LV_INDEX].PreType;
		
		int alertId = -1;
		String sheetId = null;
		String chainageId = null;
		String duePerson = AppCRTBApplication.getInstance().mUserName;
		sheetId = s_1.getSheetId();
		chainageId = s_1.getChainageId();
		
		if (s_1.getSurveyTime().getTime() <  s_2.getSurveyTime().getTime()) {
        	s_1.setSurveyTime(s_2.getSurveyTime());
        }
		
		// 累积超限
		if (isTransfiniteLeiJi) {
			alertId = AlertListDao.defaultDao().insertOrUpdate(s_1, alertLevelLeiJi, uTypeLeiJi, convergence, Constant.ACCUMULATIVE_THRESHOLD, originalDataID);

			String handlingRemark = "";
			if (curHandlingAlertId >= 0) {
				handlingRemark = info;
			}

			if (curHandlingAlertId < 0) {// 重测
				AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
			}

			AlertHandlingInfoDao.defaultDao().insertItem(alertId,handling, handlingRemark, new Date(System.currentTimeMillis()), duePerson, Constant.ALERT_STATUS_OPEN/* 报警 */, 0/* false */);
		}
		// 没有超限
		else {
			AlertList al = AlertListDao.defaultDao().queryOne(sheetId, chainageId, originalDataID, uTypeLeiJi);
			if (al != null) {
				alertId = al.getId();
				if (curHandlingAlertId >= 0) {
					AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling,info, handlingTime, duePerson, Constant.ALERT_STATUS_HANDLED, 1/* true */);
					AlertListDao.defaultDao().insertOrUpdate(s_1, alertLevelLeiJi, uTypeLeiJi, convergence, Constant.ACCUMULATIVE_THRESHOLD, originalDataID);
				} else if (al.getUploadStatus() != 2) {
					AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
					AlertListDao.defaultDao().deleteById(alertId);
				}
			}
		}
		
		// 速率超限
		if (isTransfiniteSuLv) {
			alertId = AlertListDao.defaultDao().insertOrUpdate(s_1, alertLevelSuLv, uTypeSuLv, shoulianSpeed, Constant.SPEED_THRESHOLD, originalDataID);
			String handlingRemark = "";
			if (curHandlingAlertId >= 0) {
				handlingRemark = info;
			}

			if (curHandlingAlertId < 0) {// 重测
				AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
			}

			AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, handlingRemark, new Date(System.currentTimeMillis()), duePerson, Constant.ALERT_STATUS_OPEN/* 报警 */, 0/* false */);
		}
		// 没有超限
		else {
			AlertList al = AlertListDao.defaultDao().queryOne(sheetId, chainageId, originalDataID, uTypeSuLv);
			if (al != null) {
				alertId = al.getId();
				if (curHandlingAlertId >= 0) {
					AlertHandlingInfoDao.defaultDao().insertItem(alertId, handling, info, handlingTime, duePerson, Constant.ALERT_STATUS_HANDLED, 1/* true */);
					AlertListDao.defaultDao().insertOrUpdate(s_1, alertLevelSuLv, uTypeSuLv, shoulianSpeed, Constant.SPEED_THRESHOLD, originalDataID);
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
           TunnelSettlementTotalData s_2, int curHandlingAlertId, int handling,String info, Date handlingTime,boolean needOriginalData) {
       Log.d(Constant.LOG_TAG,TAG +  "getLineConvergenceExceed");
       Exceeding ret = new Exceeding();

       if (s_1 == null || s_2 == null) {
           return null;
       }

       if (s_1.getDataStatus() == Constant.POINT_DATASTATUS_AS_FIRSTLINE && !needOriginalData) {
           return ret;
       }
       String sheetId = s_1.getSheetId();
       String originalDataID = s_1.getGuid() + Constant.ORIGINAL_ID_DIVIDER + s_2.getGuid();
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
       if (curHandlingAlertId > 0 && dataStatus == Constant.POINT_DATASTATUS_DISCARD) {
           List<AlertList> l = AlertListDao.defaultDao().queryByOrigionalDataId(sheetId,
                   chainageId, originalDataID);
           for (AlertList al : l) {
               if (al != null) {
                   int aid = al.getId();
                   AlertHandlingInfoDao
                           .defaultDao()
                           .insertIfNotExist(aid, handling,info, handlingTime,
                                   AppCRTBApplication.getInstance().mUserName, Constant.ALERT_STATUS_HANDLED, 0/*false*/);
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
               int uType = Constant.SHOULIAN_LEIJI_EXCEEDING;
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
               ret.originalSulvAlertValue = deltaLenth;
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
               int uType = Constant.SHOULIAN_SULV_EXCEEDING;
               shoulianSpeed = CrtbUtils.formatDouble(shoulianSpeed, 1);
               ret.sulvType = uType;
               ret.sulvValue = shoulianSpeed;
               ret.originalSulvAlertValue = ret.originalSulvAlertValue / deltaTInDay;
               ret.originalSulvAlertValue = CrtbUtils.formatDouble(ret.originalSulvAlertValue,1);
           }
       }

		if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE && needOriginalData) {
			ret.sulvValue = 0;
			ret.leijiValue = 0;
		}
		
        if (dataStatus == Constant.POINT_DATASTATUS_DISCARD && needOriginalData) {
        	ret.sulvValue = 0;
        	ret.leijiValue = 0;
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
            if (((TunnelSettlementTotalData) point).getDataStatus() == Constant.POINT_DATASTATUS_AS_FIRSTLINE) {
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
            if (((SubsidenceTotalData) point).getDataStatus() == Constant.POINT_DATASTATUS_AS_FIRSTLINE) {
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
        Log.d(Constant.LOG_TAG,TAG +  "getLineConvergenceValues");
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
        	//YX 取消判断上传状态
        	//if (!m.isHandled() || !m.isUploaded()) {
            if (!m.isHandled()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 存在未处理的往期预警数据
     * @param alertInfo
     * @return
     */
    public static boolean hasUnhandledPreviousWarningData(AlertInfo alertInfo){
		if(alertInfo == null){
			Log.e(Constant.LOG_TAG, TAG + ":NO Alert Info");
			return false;
		}
		
		// 当前报警时间小于报警的截止日期,不需要判断前期记录单
		if (CrtbUtils.parseDate(alertInfo.getDate()).compareTo(Constant.WaringDeadTime) < 0) {
			Log.i(Constant.LOG_TAG, TAG + "AlertId= "+alertInfo.getAlertId()+",AlertDate less than"+Constant.WaringDeadTime);
			return true;
		}
		
		MergedAlert mergedAlert = new MergedAlert();
		mergedAlert.setLeijiAlert(alertInfo);
		// 判断当前报警是否能够上传
		if (!AlertUtils.mergedAlertCanBeUploaded(mergedAlert)) {
			Log.i(Constant.LOG_TAG, TAG + " Exsit unhandled alarm information,Alert Id=" + alertInfo.getAlertId());
			return false;
		}
		
		return true;
    }
    
    /**
     * 获取AlertList中相同断面、相同监测位置的MergeAlertList
     * @param mergedAlert 当前 AlertList
     * @return 
     */
    private static ArrayList<MergedAlert> getMergedAlertsBefore(MergedAlert mergedAlert) {
        ArrayList<MergedAlert> malb = null;
        String sectionGuid = mergedAlert.getSectionGuid();
        String pntType = mergedAlert.getPntType();
        Date date = mergedAlert.getSheetDate();
        //YX 把获取MergeAlerts
        //ArrayList<MergedAlert> mal = getMergedAlerts();
        ArrayList<MergedAlert> mal = getMergedAlertsBySectionGuidAndPntType(sectionGuid,pntType);
        
        if (date != null) {
            if (mal != null && mal.size() > 0) {
                malb = new ArrayList<MergedAlert>();
                for (MergedAlert ma : mal) {
                    Date d = ma.getSheetDate();
                    //YX 取消判断断面Guid和PntType;
                    //if (secId != null && secId.equals(sectionGuid) && d != null && d.before(date) && pt != null && pt.equals(pntType))
                    if (d != null && d.after(Constant.WaringDeadTime) && d.before(date) ) {
                        malb.add(ma);
                    }
                }
            }
        }

        return malb;
    }
        
    /**
     * 根据断面Guid和监测点类型获取MergedAlert
     * @param sectionGuid 断面Guid
     * @param pntType 监测点类型
     * @return MergedAlert List
     */
    public static ArrayList<MergedAlert> getMergedAlertsBySectionGuidAndPntType(String sectionGuid,String pntType) {
        ArrayList<MergedAlert> mal = new ArrayList<MergedAlert>();
        HashMap<String, MergedAlert> mm = new HashMap<String, MergedAlert>();
        ArrayList<AlertInfo> ais = getAlertInfoListBySectionGuidAndPntType(sectionGuid,pntType);
        if (ais != null && ais.size() > 0) {
            for (AlertInfo ai : ais) {
                String key = ai.getOriginalDataID() + ai.getPntType();
                if (mm.containsKey(key)) {
                    MergedAlert ma = mm.get(key);
                    int utype = ai.getUType();
                    if (utype == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING || utype == Constant.SHOULIAN_LEIJI_EXCEEDING) {
                        ma.setLeijiAlert(ai);
                    } else {
                        ma.setSulvAlert(ai);
                    }
                } else {
                    MergedAlert ma = new MergedAlert();
                    int utype = ai.getUType();
                    if (utype == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING || utype == Constant.SHOULIAN_LEIJI_EXCEEDING) {
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

    /**
     * 根据断面类型和监测点类型获取MergedAlert
     * @param sectionGuid 断面Guid
     * @param pntType 监测点类型
     * @return MergedAlert List
     */
    public static ArrayList<MergedAlert> getMergedAlertsBySectionType(String sectionType){
    	ArrayList<MergedAlert> mal = new ArrayList<MergedAlert>();
        ArrayList<AlertInfo> ais = getAlertInfoListBySectionType(sectionType);
        if (ais != null && ais.size() > 0) {
            for (AlertInfo ai : ais) {
            	MergedAlert ma = new MergedAlert();
            	ma.setLeijiAlert(ai);
            	mal.add(ma);
            }
        }
        return mal;
    }
    
    /**
     * 根据断面的Guid获取MergedAlert
     * @param sectionGuid 断面Guid
     * @return MergedAlert List
     */
    public static ArrayList<MergedAlert> getMergedAlertsBySectionGuid(String sectionGuid){
    	ArrayList<MergedAlert> mal = new ArrayList<MergedAlert>();
        ArrayList<AlertInfo> ais = getAlertInfoListBySectionGuid(sectionGuid);
        if (ais != null && ais.size() > 0) {
            for (AlertInfo ai : ais) {
            	MergedAlert ma = new MergedAlert();
            	ma.setLeijiAlert(ai);
            	mal.add(ma);
            }
        }
        return mal;
    }
    
    /**
     * 根据断面的类型获取AlertInfo
     * 取消掉之前的计算预警值，延迟到显示的时候获取数据
     * @param sectionGuid 断面Guid
     * @return MergedAlert List
     */
    public static ArrayList<AlertInfo> getAlertInfoListBySectionType(String sectionType) {
        Log.d(Constant.LOG_TAG,TAG +  "getAlertInfoListBySectionType");

        String prefix = CrtbUtils.getSectionPrefix();
        String conditionWhere = "";
        
        ArrayList<AlertInfo> l = new ArrayList<AlertInfo>();
        
        String sql = "SELECT"
                + " AlertList.ID AS alertId,"
                + " AlertList.AlertTime AS date,"
                + " AlertList.PntType AS pntType,"
                + " AlertList.Utype AS utype,"
                + " AlertList.SheetID AS sheetId,"
                + " AlertList.CrossSectionID AS sectionId,"
                + " AlertList.AlertLevel AS alertLevel,"
                + " AlertList.UValue AS uvalue,"
                + " AlertList.OriginalDataID AS originalDataID,"
                + " AlertList.Info AS info,"
                + " AlertList.UploadStatus AS uploadStatus"
                + " FROM AlertList";
		String conditionTail = " ORDER BY AlertList.AlertTime DESC";
		
		// 判断断面的类型
		if (sectionType.equals("SUB")) {
			conditionWhere = " WHERE NOT (PntType like 'S%' or PntType like 'A%')";
		} else if (sectionType.equals("TUNNEL")) {
			conditionWhere = " WHERE (PntType like 'S%' or PntType like 'A%')";
		}
		
        sql += conditionWhere + conditionTail;
        Cursor c = null;
        try {
            c = AlertListDao.defaultDao().executeQuerySQL(sql, null);

            if (c != null) {
                Log.d(Constant.LOG_TAG,TAG +  "cursor count: " + c.getCount());
                while (c != null && c.moveToNext()) {
                    AlertInfo ai = new AlertInfo();
                    ai.setAlertId(c.getInt(0));
                    ai.setPntType(c.getString(2));
                    int uType = c.getInt(3);
                    ai.setUType(uType);
                    ai.setUTypeMsg((uType >= Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING && uType <= Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) ? Constant.U_TYPE_MSGS[uType] : "");
                    ai.setSheetId(c.getString(4));
                    ai.setSectionId(c.getString(5));
                    ai.setAlertLevel(c.getInt(6));
                    
                    //YX 获取原始的速率报警
//                    Exceeding info = getUVaule(c.getString(8), uType);
//					if (info != null) {
//						ai.setOriginalSulvAlertValue(info.originalSulvAlertValue);
//						//YX 兼容老版本的单速率报警
//						//ai.setUValue(info.leijiValue);
//						if(uType == GONGDINGI_XIACHEN_SULV_EXCEEDING || uType == SHOULIAN_SULV_EXCEEDING || uType == DIBIAO_XIACHEN_SULV_EXCEEDING){
//							ai.setUValue(info.sulvValue);
//						} else {
//							ai.setUValue(info.leijiValue);
//						}
//						
//					}
					//YX 直接获取原始的累积报警值
					ai.setOriginalLeiJiAlertValue(c.getDouble(7));
					//Yx 报警时间，这个时间在修改报警数据的时候，会随着更改，所以直接用原始的时间即可
					ai.setDate(c.getString(1));
                    ai.setOriginalDataID(c.getString(8));
                    ai.setAlertInfo(c.getString(9));
                    ai.setUploadStatus(c.getInt(10));
                    l.add(ai);
                    
					AlertHandlingList ah = getLatestHandling(ai.getAlertId());
					if (ah != null) {
						ai.setAlertHandlingId(ah.getID());
						int alertStatus = ah.getAlertStatus();
						ai.setAlertStatus(alertStatus);
						ai.setAlertStatusMsg((alertStatus >= Constant.ALERT_STATUS_HANDLED && alertStatus <= Constant.ALERT_STATUS_HANDLING) ? Constant.ALERT_STATUS_MSGS[alertStatus] : "");
						ai.setHandling(ah.getInfo());
						ai.setHandlingTime(CrtbUtils.formatDate(ah.getHandlingTime()));
						ai.setDuePerson(ah.getDuePerson());
					}
                    
                    int utype = ai.getUType();
                    if (utype == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) {
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
                    if (ah != null) {
                        ai.setChuliFangshi(ah.getHandling()< Constant.ALERT_HANDLING.length ? Constant.ALERT_HANDLING[ah.getHandling()] : "自由处理");
                    } else {
                        ai.setChuliFangshi(((ai.getAlertStatus() == Constant.ALERT_STATUS_OPEN) && (ai.getCorrection() == 0)) ? "未作任何处理" : "自由处理");
                    }
                }
            }
        } catch (SQLiteException e) {
            Log.e(Constant.LOG_TAG,TAG +  "getAlertInfoList", e);
        }  finally {
            if (c != null) {
                c.close();
            }
        }
        return l;
    }

    /**
     * 根据断面的类型获取AlertInfo
     * 取消掉之前的计算预警值，延迟到显示的时候获取数据
     * @param sectionGuid 断面Guid
     * @return
     */
    public static ArrayList<AlertInfo> getAlertInfoListBySectionGuid(String sectionGuid) {
        Log.d(Constant.LOG_TAG,TAG +  "getAlertInfoList");

        String prefix = CrtbUtils.getSectionPrefix();

        ArrayList<AlertInfo> l = new ArrayList<AlertInfo>();
        String sql = "SELECT"
                + " AlertList.ID AS alertId,"
                + " AlertList.AlertTime AS date,"
                + " AlertList.PntType AS pntType,"
                + " AlertList.Utype AS utype,"
                + " AlertList.SheetID AS sheetId,"
                + " AlertList.CrossSectionID AS sectionId,"
                + " AlertList.AlertLevel AS alertLevel,"
                + " AlertList.UValue AS uvalue,"
                + " AlertList.OriginalDataID AS originalDataID,"
                + " AlertList.Info AS info,"
                + " AlertList.UploadStatus AS uploadStatus"
                + " FROM AlertList"
                + " WHERE sectionId = ?"
                + " ORDER BY AlertList.AlertTime DESC";
        
        Cursor c = null;
        try {
            c = AlertListDao.defaultDao().executeQuerySQL(sql, new String[]{sectionGuid});

            if (c != null) {
                Log.d(Constant.LOG_TAG,TAG +  "cursor count: " + c.getCount());
                while (c != null && c.moveToNext()) {
                    AlertInfo ai = new AlertInfo();
                    ai.setAlertId(c.getInt(0));
                    ai.setPntType(c.getString(2));
                    int uType = c.getInt(3);
                    ai.setUType(uType);
                    ai.setUTypeMsg((uType >= Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING && uType <= Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) ? Constant.U_TYPE_MSGS[uType] : "");
                    ai.setSheetId(c.getString(4));
                    ai.setSectionId(c.getString(5));
                    ai.setAlertLevel(c.getInt(6));
                    
                    //YX 获取原始的速率报警
//                    Exceeding info = getUVaule(c.getString(8), uType);
//					if (info != null) {
//						ai.setOriginalSulvAlertValue(info.originalSulvAlertValue);
//						//YX 兼容老版本的单速率报警
//						//ai.setUValue(info.leijiValue);
//						if(uType == GONGDINGI_XIACHEN_SULV_EXCEEDING || uType == SHOULIAN_SULV_EXCEEDING || uType == DIBIAO_XIACHEN_SULV_EXCEEDING){
//							ai.setUValue(info.sulvValue);
//						} else {
//							ai.setUValue(info.leijiValue);
//						}
//						
//					}
					//YX 直接获取原始的累积报警值
					ai.setOriginalLeiJiAlertValue(c.getDouble(7));
					//Yx 报警时间，这个时间在修改报警数据的时候，会随着更改，所以直接用原始的时间即可
					ai.setDate(c.getString(1));
                    ai.setOriginalDataID(c.getString(8));
                    ai.setAlertInfo(c.getString(9));
                    ai.setUploadStatus(c.getInt(10));
                    l.add(ai);
                    
					AlertHandlingList ah = getLatestHandling(ai.getAlertId());
					if (ah != null) {
						ai.setAlertHandlingId(ah.getID());
						int alertStatus = ah.getAlertStatus();
						ai.setAlertStatus(alertStatus);
						ai.setAlertStatusMsg((alertStatus >= Constant.ALERT_STATUS_HANDLED && alertStatus <= Constant.ALERT_STATUS_HANDLING) ? Constant.ALERT_STATUS_MSGS[alertStatus] : "");
						ai.setHandling(ah.getInfo());
						ai.setHandlingTime(CrtbUtils.formatDate(ah.getHandlingTime()));
						ai.setDuePerson(ah.getDuePerson());
					}
                    
                    int utype = ai.getUType();
                    if (utype == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) {
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
                    if (ah != null) {
                        ai.setChuliFangshi(ah.getHandling()< Constant.ALERT_HANDLING.length ? Constant.ALERT_HANDLING[ah.getHandling()] : "自由处理");
                    } else {
                        ai.setChuliFangshi(((ai.getAlertStatus() == Constant.ALERT_STATUS_OPEN) && (ai.getCorrection() == 0)) ? "未作任何处理" : "自由处理");
                    }
                }
            }
        } catch (SQLiteException e) {
            Log.e(Constant.LOG_TAG,TAG +  "getAlertInfoList", e);
        }  finally {
            if (c != null) {
                c.close();
            }
        }
        return l;
    }

    /**
     * 根据断面的类型和断面的Guid获取AlertInfo
     * 取消掉之前的计算预警值，延迟到显示的时候获取数据
     * @param sectionGuid 断面Guid
     * @param pntType 监测点的类型
     * @return
     */
    public static ArrayList<AlertInfo> getAlertInfoListBySectionGuidAndPntType(String sectionGuid,String pntType) {
        Log.d(Constant.LOG_TAG,TAG +  "getAlertInfoList");

        String prefix = CrtbUtils.getSectionPrefix();

        ArrayList<AlertInfo> l = new ArrayList<AlertInfo>();
        String sql = "SELECT"
                + " AlertList.ID AS alertId,"
                + " AlertList.AlertTime AS date,"
                + " AlertList.PntType AS pntType,"
                + " AlertList.Utype AS utype,"
                + " AlertList.SheetID AS sheetId,"
                + " AlertList.CrossSectionID AS sectionId,"
                + " AlertList.AlertLevel AS alertLevel,"
                + " AlertList.UValue AS uvalue,"
                + " AlertList.OriginalDataID AS originalDataID,"
                + " AlertList.Info AS info,"
                + " AlertList.UploadStatus AS uploadStatus"
                + " FROM AlertList"
                + " WHERE sectionId = ? AND pntType = ?"
                + " ORDER BY AlertList.AlertTime DESC";
        Cursor c = null;
        try {
			c = AlertListDao.defaultDao().executeQuerySQL(sql, new String[] { sectionGuid, pntType });

            if (c != null) {
                Log.d(Constant.LOG_TAG,TAG +  "cursor count: " + c.getCount());
                while (c != null && c.moveToNext()) {
                    AlertInfo ai = new AlertInfo();
                    ai.setAlertId(c.getInt(0));
                    ai.setPntType(c.getString(2));
                    int uType = c.getInt(3);
                    ai.setUType(uType);
                    ai.setUTypeMsg((uType >= Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING && uType <= Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) ? Constant.U_TYPE_MSGS[uType] : "");
                    ai.setSheetId(c.getString(4));
                    ai.setSectionId(c.getString(5));
                    ai.setAlertLevel(c.getInt(6));
                    
					ai.setDate(c.getString(1));
                    ai.setOriginalDataID(c.getString(8));
                    ai.setAlertInfo(c.getString(9));
                    ai.setUploadStatus(c.getInt(10));
                    l.add(ai);
                    
					AlertHandlingList ah = getLatestHandling(ai.getAlertId());
					if (ah != null) {
						ai.setAlertHandlingId(ah.getID());
						int alertStatus = ah.getAlertStatus();
						ai.setAlertStatus(alertStatus);
						ai.setAlertStatusMsg((alertStatus >= Constant.ALERT_STATUS_HANDLED && alertStatus <= Constant.ALERT_STATUS_HANDLING) ? Constant.ALERT_STATUS_MSGS[alertStatus] : "");
						ai.setHandling(ah.getInfo());
						ai.setHandlingTime(CrtbUtils.formatDate(ah.getHandlingTime()));
						ai.setDuePerson(ah.getDuePerson());
					}
                    
                }
            }
        } catch (SQLiteException e) {
            Log.e(Constant.LOG_TAG,TAG +  "getAlertInfoList", e);
        }  finally {
            if (c != null) {
                c.close();
            }
        }
        return l;
    }

    /**
     * 获取AlertInfo具体的沉降值、处理详 
     * @param ai
     */
    public static void getAlertDetailInfo(AlertInfo ai){
    	int uType = ai.getUType();
    	Exceeding info = getUVaule(ai.getOriginalDataID(), uType);
		if (info != null) {
			ai.setOriginalSulvAlertValue(info.originalSulvAlertValue);
			//YX 兼容老版本的单速率报警
			//ai.setUValue(info.leijiValue);
			if(isSpeed(uType)){
				ai.setUValue(info.sulvValue);
			} else {
				ai.setUValue(info.leijiValue);
			}
		}
		String prefix = CrtbUtils.getSectionPrefix();
		AlertHandlingList ah = getLatestHandling(ai.getAlertId());
        if (ah != null) {
            ai.setAlertHandlingId(ah.getID());
            int alertStatus = ah.getAlertStatus();
            ai.setAlertStatus(alertStatus);
            ai.setAlertStatusMsg((alertStatus >= Constant.ALERT_STATUS_HANDLED && alertStatus <= Constant.ALERT_STATUS_HANDLING) ? Constant.ALERT_STATUS_MSGS[alertStatus]
                    : "");
            ai.setHandling(ah.getInfo());
            ai.setHandlingTime(CrtbUtils.formatDate(ah.getHandlingTime()));
            ai.setDuePerson(ah.getDuePerson());
        }

        String oriId = ai.getOriginalDataID();
        String dataId = null;
        if (TextUtils.isEmpty(oriId)) {
            return;
        }
        if (oriId.contains(Constant.ORIGINAL_ID_DIVIDER)) {
            String[] ids = oriId.split(Constant.ORIGINAL_ID_DIVIDER);
            if (ids != null && ids.length > 0)
            dataId = ids[0];
        } else {
            dataId = oriId;
        }

        int utype = ai.getUType();

        if (!TextUtils.isEmpty(dataId)) {
            if (utype == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING || utype == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) {
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
            ai.setChuliFangshi(ah.getHandling()< Constant.ALERT_HANDLING.length ? Constant.ALERT_HANDLING[ah.getHandling()] : "自由处理");
        } else {
            ai.setChuliFangshi(((ai.getAlertStatus() == Constant.ALERT_STATUS_OPEN) && (ai.getCorrection() == 0)) ? "未作任何处理" : "自由处理");
        }
    }

    /**
     * 获取AlertInfo的累计超限
     * @param ai
     */
    public static void getAlertTransfiniteInfo(AlertInfo ai){
    	int uType = ai.getUType();
    	Exceeding info = getUVaule(ai.getOriginalDataID(), uType);
		if (info != null) {
			ai.setOriginalSulvAlertValue(info.originalSulvAlertValue);
			//YX 兼容老版本的单速率报警
			//ai.setUValue(info.leijiValue);
			if (AlertUtils.isSpeed(uType)) {
				ai.setUValue(info.sulvValue);
			} else {
				ai.setUValue(info.leijiValue);
			}
		}
    }
    
    
    /**
     * 获取报警的最后一次处理详情
     * @param alertId 报警ID
     * @return
     */
    private static AlertHandlingList getLatestHandling(int alertId) {
        List<AlertHandlingList> l = AlertHandlingInfoDao.defaultDao().queryByAlertIdOrderByHandlingTimeDesc(alertId);
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        return null;
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
    
    /**
     * 
     * @param alertId 处理的Alert在数据库中的ID
     * @param dataStatus 要更新的TunnelSettlementTotalData表或SubsidenceTotalData表的DataStatus列
     * @param isRebury
     * @param correction
     * @param curAlertStatus
     * @param alertStatus
     * @param handling
     * @param info
     * @param handlingTime
     * @param rockGrade
     */
    public static void handleAlert(int alertId, int dataStatus, boolean isRebury, float correction,
            int curAlertStatus, int alertStatus, int handling, String info, Date handlingTime,String rockGrade) {
        Log.d(Constant.LOG_TAG,TAG +  "handleAlert");

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
            if (originalID.contains(Constant.ORIGINAL_ID_DIVIDER)) {
                String[] idStrs = originalID.split(Constant.ORIGINAL_ID_DIVIDER);
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
                            if (uType == Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING) {
                                thisCorrection = (float) (0d - uValue);
                            } else if (uType == Constant.GONGDINGI_XIACHEN_SULV_EXCEEDING) {
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

                        if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE
                                || dataStatus == Constant.POINT_DATASTATUS_CORRECTION) {
                            // STEP 4
                            id = p.getID();
//                            AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);

//                            if (dataStatus == POINT_DATASTATUS_CORRECTION && correction != 0) {
//                                handleSameTunnelPointOtherAlertsByCorrection(p, alertId, correction, handlingTime);
//                            }
                        } else if (dataStatus == Constant.POINT_DATASTATUS_DISCARD) {
                            checkPointSubsidenceAlert(p, alertId, handling, info, handlingTime, false,rockGrade,null,null);
                        	//checkPointSubsidenceExceed(p, alertId, handling, handlingTime, false,rockGrade);
                        }
                    }
                } else {//地表沉降
                    SubsidenceTotalData p = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(guid);
                    if (p != null) {
                        float thisCorrection = correction;
                        if (isRebury) {
                            if (uType == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING) {
                                thisCorrection = (float) (0d - uValue);
                            } else if (uType == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) {
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

                        if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE
                                || dataStatus == Constant.POINT_DATASTATUS_CORRECTION) {
                            // STEP 4
                            id = p.getID();
//                            AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);

//                            if (dataStatus == POINT_DATASTATUS_CORRECTION && correction != 0) {
//                                handleSameGroundPointOtherAlertsByCorrection(p, alertId, correction, handlingTime);
//                            }
                        } else if (dataStatus == Constant.POINT_DATASTATUS_DISCARD) {
                            checkPointSubsidenceAlert(p, alertId, handling, info, handlingTime, false,rockGrade,null,null);
                        	//checkPointSubsidenceExceed(p, alertId, handling, handlingTime, false,rockGrade);
                        }
                    }
                }

                if (id != -1) {
                    if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE) {
//                        tarAlertStatus = alertStatus;
                        handleAsFirstLine(alertId, alertStatus, handling, info, duePerson, handlingTime,
                                chainageId, pntType, id);
                    }
                    updatePointSubsidenceAlertsAfterCorrection(chainageId, pntType, id, alertId, handling, info, handlingTime,rockGrade);
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
                    if (uType == Constant.SHOULIAN_LEIJI_EXCEEDING) {
                        thisCorrection = (float) (0d - uValue);
                    } else if (uType == Constant.SHOULIAN_SULV_EXCEEDING) {
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
                if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE
                        || dataStatus == Constant.POINT_DATASTATUS_CORRECTION
                        || dataStatus == Constant.POINT_DATASTATUS_DISCARD) {
                    // STEP 4

                    if (dataStatus == Constant.POINT_DATASTATUS_AS_FIRSTLINE) {
//                        tarAlertStatus = alertStatus;
                        String s1Type = s_1.getPntType();
                        handleAsFirstLine(alertId, alertStatus, handling, info, duePerson, handlingTime,
                                chainageId, s1Type, s_1.getID());
                        String oppositeType = s1Type.substring(0, s1Type.length() - 1) + "2";
                        handleAsFirstLine(alertId, alertStatus, handling, info, duePerson, handlingTime,
                                chainageId, oppositeType, s_1.getID());
                    }

//                    AlertHandlingInfoDao.defaultDao().deleteByAlertId(alertId);
                    
                    s_1.setDataCorrection(totalCorrection);
                    s_2.setDataCorrection(totalCorrection);
                    updateLineConvergenceAlertsAfterCorrection(chainageId, s_1.getPntType(),s_1.getID(), alertId, handling, info, handlingTime, rockGrade);
                } else if (dataStatus == Constant.POINT_DATASTATUS_DISCARD) {
                	checkLineConvergenceAlert(s_1, s_2, alertId, handling, info, handlingTime, false,rockGrade,null,null);
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
    public static void handleAsFirstLine(int alertId, int alertStatus, int handling,String info,
            String duePerson, Date handlingTime, String chainageId, String pntType, int measDataId) {
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
                                        handlingRemark = info;
                                    }
                                    AlertHandlingInfoDao.defaultDao()
                                            .insertIfNotExist(curAlertId,handling, handlingRemark, handlingTime,
                                                    duePerson, alertStatus, 0/*false*/);
                                }
                            }
                        }
                    }
                    if (i < size - 1) {
                        p.setDataStatus(Constant.POINT_DATASTATUS_DISCARD);
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
                                            .insertItem(curAlertId, handling,info, handlingTime,
                                                    duePerson, alertStatus, 0/*false*/);
                                }
                            }
                        }
                    }
                    if (i < size - 1) {
                        p.setDataStatus(Constant.POINT_DATASTATUS_DISCARD);
                        SubsidenceTotalDataDao.defaultDao().update(p);
                    }
                }
            }
        }
    }
    
    private static void updatePointSubsidenceAlertsAfterCorrection(String chainageId, String pntType,
            int measId, int curHandlingAlertId, int handling, String info, Date handlingTime,String rockGrade) {
        Log.d(Constant.LOG_TAG,TAG +  "updatePointSubsidenceAlertsAfterCorrection, pntType: " + pntType);
        if (pntType.contains("A")) {//隧道内断面
            List<TunnelSettlementTotalData> ls = TunnelSettlementTotalDataDao.defaultDao()
                    .queryInfoAfterMeasId(chainageId, pntType, measId);
            if (ls != null && ls.size() > 0) {
                for (TunnelSettlementTotalData p : ls) {
                    String currentHandling = "";
                    if (p.getID() == measId) {
                        currentHandling = info;
                    } else {
                        AlertListDao.defaultDao().deleteAlert(p.getSheetId(), chainageId, p.getGuid());
                        TunnelSettlementTotalDataDao.defaultDao().updateDataStatus(p.getGuid(), Constant.POINT_DATASTATUS_NONE, 0);
                        p = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(p.getGuid());
                    }
                    checkPointSubsidenceAlert(p, curHandlingAlertId, handling, info, handlingTime, false,rockGrade,null,null);
                    //checkPointSubsidenceExceed(p, curHandlingAlertId, handling, handlingTime, false,rockGrade);
                }
            }
        } else {//地表沉降
            List<SubsidenceTotalData> ls = SubsidenceTotalDataDao.defaultDao()
                    .queryInfoAfterMeasId(chainageId, pntType, measId);
            if (ls != null && ls.size() > 0) {
                for (SubsidenceTotalData p : ls) {
                    if (p.getID() != measId) {
                        AlertListDao.defaultDao().deleteAlert(p.getSheetId(), chainageId, p.getGuid());
                        SubsidenceTotalDataDao.defaultDao().updateDataStatus(p.getGuid(), Constant.POINT_DATASTATUS_NONE, 0);
					}
					p = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(p.getGuid());
                    checkPointSubsidenceAlert(p, curHandlingAlertId, handling, info, handlingTime, false,rockGrade,null,null);
                    //checkPointSubsidenceExceed(p, curHandlingAlertId, handling, handlingTime, false, rockGrade);
                }
            }
        }
    }

   
    private static void updateLineConvergenceAlertsAfterCorrection(String chainageId, String pntType,
            int measId, int curHandlingAlertId, int handling, String info, Date handlingTime,String rockGrade) {
        Log.d(Constant.LOG_TAG,TAG +  "updateLineConvergenceAlertsAfterCorrection, pntType: " + pntType);
        if (pntType.contains("S") && pntType.endsWith("1")) {//测线左点
            String oppositePntType = pntType.substring(0, pntType.length() - 1) + "2";
            List<TunnelSettlementTotalData> ls = TunnelSettlementTotalDataDao.defaultDao()
                    .queryInfoAfterMeasId(chainageId, pntType, measId);
            if (ls != null && ls.size() > 0) {
                for (TunnelSettlementTotalData s_1 : ls) {     
                	TunnelSettlementTotalData s_2 = TunnelSettlementTotalDataDao.defaultDao().queryOppositePointOfALine(s_1, oppositePntType);//拿到测线右点
                	if(s_1.getID() != measId){
                		AlertListDao.defaultDao().deleteAlert(s_1.getSheetId(), chainageId, s_1.getGuid()+','+s_2.getGuid());
                	}
                    checkLineConvergenceAlert(s_1, s_2, curHandlingAlertId, handling, info, handlingTime, false, rockGrade,null,null);
                }
            }
        }
    }
    
    /**
     * @param pastPointList
     * @return 所有pastPointList中的测点数据的DataCorrection的和值，单位：毫米
     */
	public static float calculateSumOfDataCorrectionsOfTunnelSettlementTotalDatas(List<TunnelSettlementTotalData> pastPointList) {
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
		// AlertInfo currentAlert = alerts.get(clickedItem);
		Date thisTime = null;
		List pastInfoList = null;
		int type = 0;
		if (currentAlert == null) {
			return 1;
		}
		String originalID = currentAlert.getOriginalDataID();
		if (!TextUtils.isEmpty(originalID)) {
			ArrayList<String> guids = new ArrayList<String>();
			if (originalID.contains(Constant.ORIGINAL_ID_DIVIDER)) {
				String[] idStrs = originalID.split(Constant.ORIGINAL_ID_DIVIDER);
				for (String idStr : idStrs) {
					guids.add(idStr);
				}
			} else {
				guids.add(originalID);
			}

			if (guids.size() == 1) {// 测点
				String guid = guids.get(0);
				int measNo = -1;
				if (currentAlert.getPntType().contains("A")) {// 隧道内断面
					type = 1;
					TunnelSettlementTotalData tPoint = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guid);
					if (tPoint != null) {
						pastInfoList = TunnelSettlementTotalDataDao.defaultDao().queryInfoBeforeMeasId(tPoint.getChainageId(), tPoint.getPntType(), tPoint.getID());
						thisTime = tPoint.getSurveyTime();
					}
				} else {
					// Subsidence
					type = 2;
					SubsidenceTotalData sPoint = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(guid);
					if (sPoint != null) {
						pastInfoList = SubsidenceTotalDataDao.defaultDao().queryInfoBeforeMeasId(sPoint.getChainageId(), sPoint.getPntType(), sPoint.getID());
						thisTime = sPoint.getSurveyTime();
					}
				}
			} else {
				TunnelSettlementTotalData s_1 = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guids.get(0));
				String pnt1Type = s_1.getPntType();
				String oppositePntType = pnt1Type.substring(0, pnt1Type.length() - 1) + "2";
				TunnelSettlementTotalData s_2 = TunnelSettlementTotalDataDao.defaultDao().queryOppositePointOfALine(s_1, oppositePntType);
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
				// thisDataCorrection = ((TunnelSettlementTotalData)
				// lastInfo).getDataCorrection();
			} else if (type == 2) {
				lastCoords = ((SubsidenceTotalData) lastInfo).getCoordinate().split(",");
				lastTime = ((SubsidenceTotalData) lastInfo).getSurveyTime();
				// thisDataCorrection = ((SubsidenceTotalData)
				// lastInfo).getDataCorrection();
			}

			if (thisTime != null && lastTime != null) {
				long deltaT = Math.abs(thisTime.getTime() - lastTime.getTime());
				Log.d(Constant.LOG_TAG,TAG +  "delta t: " + deltaT + " ms");
				if (deltaT < Time.ONE_SECOND) {
					deltaT = Time.ONE_SECOND;// ONE SECOND at least to avoid
												// infinity
				}
				double deltaTInDay = ((double) deltaT / Time.DAY_MILLISECEND_RATIO);
				return deltaTInDay;
			}
		}
		return 1;
	}
    
    public static boolean isSpeed(int uType) {
        return uType == Constant.GONGDINGI_XIACHEN_SULV_EXCEEDING || uType == Constant.SHOULIAN_SULV_EXCEEDING || uType == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING;
    }

    private static Exceeding getUVaule(String dataGuid, int uType) {
        if (uType == Constant.GONGDINGI_XIACHEN_SULV_EXCEEDING || uType == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING
                || uType == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING
                || uType == Constant.GONGDING_LEIJI_XIACHEN_EXCEEDING) {
            Object point = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(dataGuid);
            if (point == null) {
                point = SubsidenceTotalDataDao.defaultDao().queryOneByGuid(dataGuid);
            } 
            return getPointSubsidenceExceed(point, -1, -1, null, null, true);
        }

        if (uType == Constant.SHOULIAN_LEIJI_EXCEEDING || uType == Constant.SHOULIAN_SULV_EXCEEDING) {
            TunnelSettlementTotalData s_1 = null;
            TunnelSettlementTotalData s_2 = null;
            if (dataGuid != null) {
                String[] guids = dataGuid.split(",");
                if (guids.length > 1) {
                    s_1 = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guids[0]);
                    s_2 = TunnelSettlementTotalDataDao.defaultDao().queryOneByGuid(guids[1]);
                }
            }
            return getLineConvergenceExceed(s_1, s_2, -1, -1, null, null, true);
        }
        return null;
    }


	public interface UploadFinishCallBack{
		public void Finish(OffsetLevel[] offsetList,boolean isCancel);
	}
	
    
	/**
     * 根据原始数据去获取、报警数据
     */
    
    public static AlertInfo getWarnData(String originalDataId){
		Log.d(Constant.LOG_TAG,TAG +  "getWarnData");

		String prefix = CrtbUtils.getSectionPrefix();
		AlertList orginalAlert = AlertListDao.defaultDao().queryOneOrigionalDataId(originalDataId);
		if (orginalAlert == null) {
			return null;
		}
		AlertInfo alertInfo = new AlertInfo();
		alertInfo.setAlertId(orginalAlert.getId());
		alertInfo.setPntType(orginalAlert.getPntType());
		int uType = orginalAlert.getUType();
		alertInfo.setUType(uType);
		// ai.setUTypeMsg((uType >= GONGDING_LEIJI_XIACHEN_EXCEEDING && uType <=
		// DIBIAO_XIACHEN_SULV_EXCEEDING) ? U_TYPE_MSGS[uType] : "");
		alertInfo.setSheetId(orginalAlert.getSheetId());
		alertInfo.setSectionId(orginalAlert.getCrossSectionId());
		alertInfo.setAlertLevel(orginalAlert.getAlertLevel());

		// YX 获取原始的速率报警
		Exceeding info = getUVaule(originalDataId, uType);
		if (info != null) {
			alertInfo.setOriginalSulvAlertValue(info.originalSulvAlertValue);
			// YX 兼容老版本的单速率报警
			// ai.setUValue(info.leijiValue);
			if (AlertUtils.isSpeed(uType)) {
				alertInfo.setUValue(info.sulvValue);
			} else {
				alertInfo.setUValue(info.leijiValue);
			}

		}
		// YX 直接获取原始的累积报警值
		alertInfo.setOriginalLeiJiAlertValue(orginalAlert.getUValue());
		// Yx 报警时间，这个时间在修改报警数据的时候，会随着更改，所以直接用原始的时间即可
		alertInfo.setDate(CrtbUtils.formatDate(orginalAlert.getAlertTime()));
		alertInfo.setOriginalDataID(originalDataId);
		alertInfo.setAlertInfo(orginalAlert.getInfo());
		alertInfo.setUploadStatus(orginalAlert.getUploadStatus());

		AlertHandlingList ah = getLatestHandling(alertInfo.getAlertId());
		if (ah != null) {
			alertInfo.setAlertHandlingId(ah.getID());
			int alertStatus = ah.getAlertStatus();
			alertInfo.setAlertStatus(alertStatus);
			alertInfo.setAlertStatusMsg((alertStatus >= Constant.ALERT_STATUS_HANDLED && alertStatus <= Constant.ALERT_STATUS_HANDLING) ? Constant.ALERT_STATUS_MSGS[alertStatus]
					: "");
			alertInfo.setHandling(ah.getInfo());
			alertInfo.setHandlingTime(CrtbUtils.formatDate(ah.getHandlingTime()));
			alertInfo.setDuePerson(ah.getDuePerson());
		}

		String dataId = null;
		if (originalDataId.contains(Constant.ORIGINAL_ID_DIVIDER)) {
			String[] ids = originalDataId.split(Constant.ORIGINAL_ID_DIVIDER);
			if (ids != null && ids.length > 0)
				dataId = ids[0];
		} else {
			dataId = originalDataId;
		}

		int utype = alertInfo.getUType();
		if (utype == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING
				|| utype == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) {
			String secid = alertInfo.getSectionId();
			SubsidenceCrossSectionIndex si = SubsidenceCrossSectionIndexDao
					.defaultDao().querySectionByGuid(secid);
			if (si != null) {
				alertInfo.setXinghao(CrtbUtils.formatSectionName(prefix,si.getChainage()));
				alertInfo.SetRockGrade(si.getROCKGRADE());
				SubsidenceCrossSectionExIndex siex = SubsidenceCrossSectionExIndexDao.defaultDao().querySectionById(si.getID());
				if (siex != null) {
					alertInfo.setSECTCODE(siex.getSECTCODE());
				}
			}

		} else {
			String secid = alertInfo.getSectionId();
			TunnelCrossSectionIndex si = TunnelCrossSectionIndexDao
					.defaultDao().querySectionByGuid(secid);
			if (si != null) {
				alertInfo.setXinghao(CrtbUtils.formatSectionName(prefix,
						si.getChainage()));
				alertInfo.SetRockGrade(si.getROCKGRADE());
				TunnelCrossSectionExIndex siex = TunnelCrossSectionExIndexDao
						.defaultDao().querySectionById(si.getID());
				if (siex != null) {
					alertInfo.setSECTCODE(siex.getSECTCODE());
				}
			}

		}

		if (!TextUtils.isEmpty(dataId)) {
			if (utype == Constant.DIBIAO_LEIJI_XIACHEN_EXCEEDING
					|| utype == Constant.DIBIAO_XIACHEN_SULV_EXCEEDING) {
				SubsidenceTotalData data = SubsidenceTotalDataDao.defaultDao()
						.queryOneByGuid(dataId);
				if (data != null) {
					alertInfo.setCorrection(data.getDataCorrection());
				}
			} else {
				TunnelSettlementTotalData data = TunnelSettlementTotalDataDao
						.defaultDao().queryOneByGuid(dataId);
				if (data != null) {
					alertInfo.setCorrection(data.getDataCorrection());
				}
			}
		}
		//
		// if (ah != null) {
		// alertInfo.setChuliFangshi(ah.getHandling()< ALERT_HANDLING.length ?
		// ALERT_HANDLING[ah.getHandling()] : "自由处理");
		// } else {
		// alertInfo.setChuliFangshi(((alertInfo.getAlertStatus() == ALERT_STATUS_OPEN) &&
		// (alertInfo.getCorrection() == 0)) ? "未作任何处理" : "自由处理");
		// }
		return alertInfo;
	}
    
    public static List<AlertInfo> getWarnDataList(List<String> originalDataIdList){
    	List<AlertInfo> alertList = new ArrayList<AlertInfo>();
    	for(String originalDataId : originalDataIdList){
    		alertList.add(getWarnData(originalDataId));
    	}
    	return alertList;
    }
}
