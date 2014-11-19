package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.util.StringUtils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.infors.UploadWarningEntity;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public abstract class AsyncUploadTask extends AsyncTask<List<SheetRecord>, Void, Void> {
	private static final String LOG_TAG = "AsyncUploadTask";

	protected WarningDataManager warningDataManager = null;
	/**
	 * 上传测量数据统计
	 */
	protected int actualUploadTestDataCount = 0;
	
	/**
	 * 上传预警数据统计
	 */
	protected int actualUploadAarmDataCount = 0;
	
	/**
	 * 上传测量数据统计
	 */
	protected int exceptedUploadTestDataCount = 0;
	
	/**
	 * 上传预警数据统计
	 */
	protected int exceptedUploadAarmDataCount = 0;
	
	protected String notice;
	
	public String getNotice(){
		String testNotice;
		String alarmNotice;
		boolean errorAllTest = false;
		boolean errorAllAlarm = false;
		if(notice != null && !StringUtils.isEmpty(notice.trim())){
			return notice;
		}
		
		if(exceptedUploadTestDataCount == 0){
			testNotice = "无有效观测数据";
		} else {
			if(actualUploadTestDataCount == exceptedUploadTestDataCount){
				testNotice = "观测数据上传成功";
			} else if(actualUploadTestDataCount > 0){
				testNotice = "部分观测数据上传成功";
			} else {
				testNotice = "观测数据上传失败";
				errorAllTest = true;
			}
		}
		
		if(exceptedUploadAarmDataCount == 0){
			alarmNotice = "无有效预警数据";
		} else {
			if(actualUploadAarmDataCount == exceptedUploadAarmDataCount){
				alarmNotice = "预警数据上传成功";
			} else if(actualUploadAarmDataCount > 0){
				alarmNotice = "部分预警数据上传成功";
			} else {
				alarmNotice = "预警数据上传失败";
				errorAllAlarm = true;
			}
		}
		
		notice = testNotice + "," + alarmNotice + "!";
				
		if(errorAllTest && errorAllAlarm){
			notice += "连接超时，请检查网络连接!" ;
		}
		
		return notice;
	}
	
	
	/** 数据上传成功 */
	public static final int CODE_SUCCESS = 100;
	/** 记录单中无有效的观测数据 */
	public static final int CODE_NO_MEASURE_DATA = 101;

	public interface UploadListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success, int code,String notice);
	}

	private UploadListener mListener;
	private Handler mHandler;

	public AsyncUploadTask(UploadListener listener) {
		mListener = listener;
		mHandler = new Handler(Looper.getMainLooper());
		warningDataManager = new WarningDataManager();
	}

	@Override
	protected Void doInBackground(List<SheetRecord>... params) {		
		if (params != null && params.length > 0) {
			List<SheetRecord> sheetRecords = params[0];
			if (sheetRecords != null && sheetRecords.size() > 0) {
				List<Section> allUploadSection = new ArrayList<Section>();
				for (SheetRecord record : sheetRecords) {
					List<Section> sections = record.getUnUploadSections();
					if (sections != null && sections.size() > 0) {
						for (Section section : sections) {
							int loc = contains(allUploadSection, section);
							if (loc == -1) {
								allUploadSection.add(section);
							} else {
								allUploadSection.get(loc).addMeasureData(section.getMeasureData());
							}
						}
					}
				}
				if (allUploadSection.size() > 0) {
					DataCounter sectionUploadCounter = new DataCounter("SectionUploadCounter", allUploadSection.size(),
							new CounterListener() {
								@Override
								public void done(boolean success) {
									notifyDone(success, CODE_SUCCESS);
								}
							});
					for (Section section : allUploadSection) {
						if (!section.isUpload()) {
							uploadSection(section, sectionUploadCounter);
						} else {
							Log.d(LOG_TAG, "section is already uploaded: section_code: " + section.getSectionCode());
							uploadMeasureDataList(section.getSectionCode(), section.getMeasureData(),
									sectionUploadCounter);
						}
					}
				} else {
					notifyDone(false, CODE_NO_MEASURE_DATA);
				}
			} else {
				Log.w(LOG_TAG, "empty data.");
			}
		}
		return null;
	}

	/**
	 * 上传断面数据
	 * 
	 * @param section
	 * @param sheetId
	 * @param sectionUploadCounter
	 */
	protected abstract void uploadSection(Section section, DataCounter sectionUploadCounter);

	/**
	 * 上传断面的测量数据
	 * 
	 * @param section
	 * @param sectionUploadCounter
	 */
	protected abstract void uploadMeasureDataList(String sectionCode, List<MeasureData> measureDataList,
			DataCounter sectionUploadCounter);

	private void notifyDone(final boolean flag, final int code) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.done(flag, code,getNotice());
				}
			}
		});
	}

	private static int contains(List<Section> sections, Section section) {
		int result = -1;
		if (section != null && sections != null && sections.size() > 0) {
			for (int i = 0; i < sections.size(); i++) {
				Section s = sections.get(i);
				if (s.getRowId() == section.getRowId()) {
					result = i;
					break;
				}
			}
		}
		return result;
	}

	protected void uploadWarnWrapper(final AlertInfo curAlertInfo,final DataCounter pointUploadCounter){
		if(curAlertInfo == null) {
			pointUploadCounter.increase(true);
			return;
		}
		
		if (!AlertUtils.hasUnhandledPreviousWarningData(curAlertInfo)) {
			pointUploadCounter.increase(true);
			return;
		}
		
		final WarningDataManager.WarningUploadListener lister = new WarningDataManager.WarningUploadListener() {

			@Override
			public void done(boolean success,boolean hasData) {
				if (success) {
					if(!hasData){
						exceptedUploadAarmDataCount--;
					} else {
						actualUploadAarmDataCount++;
					}
					pointUploadCounter.increase(true);
					if(curAlertInfo != null){
						int alertId = curAlertInfo.getAlertId();
						AlertList bean = AlertListDao.defaultDao().queryOneById(alertId);
						if (bean != null) {
							bean.setUploadStatus(2);
							AlertListDao.defaultDao().update(bean);
						}
					}
				} else {
					Log.d("CrtbWebService","upload warn data failed.");
					pointUploadCounter.increase(false);
				}
			}

		};

		UploadWarningEntity originalData = new UploadWarningEntity();
		originalData.setLeijiAlert(curAlertInfo);
		originalData.setSectionCode(curAlertInfo.getSECTCODE());
		exceptedUploadAarmDataCount++;
		warningDataManager.uploadWarning(curAlertInfo,originalData, lister);
		
	}
	
	protected boolean hasUnhandledAlert(List<AlertInfo> alerts){
		for(AlertInfo alertInfo : alerts){
			if(alertInfo != null) {
				if(alertInfo.getAlertStatus() != Constant.ALERT_STATUS_HANDLED){
					return true;
				}
			}
		}
		return false;
	}

	protected void uploadMeasureData(String sectionCode,final MeasureData  measureData,final AlertInfo curAlertInfo,final DataCounter pointUploadCounter) {
				
		if(Constant.getNoUploadDataWhenWarningUnHandled()){
			if(curAlertInfo != null) {
				if(curAlertInfo.getAlertStatus() != Constant.ALERT_STATUS_HANDLED){
					pointUploadCounter.increase(false);
					exceptedUploadTestDataCount--;
					return;
				}
			}
		}
		
		exceptedUploadTestDataCount++;
		PointUploadParameter parameter = new PointUploadParameter();
        parameter.setSectionCode(sectionCode);
        parameter.setPointCodeList(measureData.getPointCodeList(sectionCode));
        parameter.setTunnelFaceDistance(measureData.getFaceDistance());
        parameter.setProcedure(measureData.getFaceDescription());
        parameter.setMonitorModel(measureData.getMonitorModel());
        parameter.setMeasureDate(measureData.getMeasureDate());
        parameter.setPointValueList(measureData.getValueList());
        parameter.setPointCoordinateList(measureData.getCoordinateList());
		String sheetGuid = measureData.getSheetGuid();
		if (sheetGuid != null) {
			SurveyerInformation surveyer = CrtbUtils.getSurveyerInfoBySheetGuid(sheetGuid);
			parameter.setSurveyorName(surveyer.getSurveyerName());
			parameter.setSurveyorId(surveyer.getCertificateID());
		}
        parameter.setRemark("");
        CrtbWebService.getInstance().uploadTestResult(parameter, new RpcCallback() {
            
            @Override
			public void onSuccess(Object[] data) {
            	measureData.markAsUploaded();
            	actualUploadTestDataCount++;
            	uploadWarnWrapper(curAlertInfo,pointUploadCounter);
			}

			@Override
			public void onFailed(String reason) {
				Log.d("CrtbWebService", "upload test data failed.");
				pointUploadCounter.increase(false);
			}
        });
	}
}
