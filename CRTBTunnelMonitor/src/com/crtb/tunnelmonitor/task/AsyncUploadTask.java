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
	private static final String TAG = "AsyncUploadTask: ";

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

	public String getNotice() {
		String testNotice;
		String alarmNotice;
		if (notice != null && !StringUtils.isEmpty(notice.trim())) {
			return notice;
		}

		if (exceptedUploadTestDataCount == 0) {
			testNotice = "无有效观测数据";
		} else {
			if (actualUploadTestDataCount == exceptedUploadTestDataCount) {
				testNotice = "观测数据上传成功";
			} else if (actualUploadTestDataCount > 0) {
				testNotice = "部分观测数据上传成功";
			} else {
				testNotice = "观测数据上传失败";
			}
		}

		if (exceptedUploadAarmDataCount == 0) {
			alarmNotice = "无有效预警数据";
		} else {
			if (actualUploadAarmDataCount == exceptedUploadAarmDataCount) {
				alarmNotice = "预警数据上传成功";
			} else if (actualUploadAarmDataCount > 0) {
				alarmNotice = "部分预警数据上传成功";
			} else {
				alarmNotice = "预警数据上传失败";
			}
		}

		notice = testNotice + "," + alarmNotice + "!";

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
		public void done(boolean success, int code, String notice);
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
					final DataCounter sectionUploadCounter = new DataCounter("SectionUploadCounter", allUploadSection.size(), new CounterListener() {
						@Override
						public void done(boolean success) {
							notifyDone(success, CODE_SUCCESS);
						}
					});
					List<UploadSet> uploadSetList = new ArrayList<UploadSet>();
					for (Section section : allUploadSection) {
						UploadSet uploadSet = queryUploadSheet(section);
						if (uploadSet == null) {
							notice = "该记录单存在未关闭的预警，请先处理";
							notifyDone(false, CODE_NO_MEASURE_DATA);
							return null;
						} else {
							uploadSetList.add(uploadSet);
						}
					}

					int count = uploadSetList.size();
					for (int i = 0; i < count; i++) {
						UploadSet uploadSet = uploadSetList.get(i);
						if (!uploadSet.section.isUpload()) {
							uploadSection(uploadSet, sectionUploadCounter);
						} else {
							Log.d(Constant.LOG_TAG_SERVICE,TAG + "section is already uploaded: section_code: " + uploadSet.section.getSectionCode());
							uploadMeasureDataList(uploadSet, sectionUploadCounter);
						}
					}
				} else {
					notifyDone(false, CODE_NO_MEASURE_DATA);
				}
			} else {
				Log.w(TAG, "empty data.");
			}
		}
		return null;
	}

	/**
	 * 判断能否上传数据，
	 * 
	 * @return null:不能上传数据，非null:能上传数据
	 */
	protected abstract UploadSet queryUploadSheet(Section section);

	
	/**
	 * 上传断面数据
	 * 
	 * @param section
	 * @param sheetId
	 * @param sectionUploadCounter
	 */
	protected abstract void uploadSection(UploadSet uploadSet, DataCounter sectionUploadCounter);

	/**
	 * 上传断面的测量数据
	 * 
	 * @param section
	 * @param sectionUploadCounter
	 */
	protected abstract void uploadMeasureDataList(UploadSet uploadSet, DataCounter sectionUploadCounter);

	private void notifyDone(final boolean flag, final int code) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.done(flag, code, getNotice());
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

	protected void uploadWarnWrapper(final AlertInfo curAlertInfo, final DataCounter pointUploadCounter) {
		if (curAlertInfo == null) {
			pointUploadCounter.increase(true);
			return;
		}

		if (!AlertUtils.hasUnhandledPreviousWarningData(curAlertInfo)) {
			pointUploadCounter.increase(true);
			return;
		}

		WarningDataManager.WarningUploadListener lister = new WarningDataManager.WarningUploadListener() {

			@Override
			public void done(boolean success, boolean hasData) {
				if (success) {
					if (!hasData) {
						exceptedUploadAarmDataCount--;
					} else {
						actualUploadAarmDataCount++;
					}
					pointUploadCounter.increase(true);
					if (curAlertInfo != null) {
						int alertId = curAlertInfo.getAlertId();
						AlertList bean = AlertListDao.defaultDao().queryOneById(alertId);
						if (bean != null) {
							bean.setUploadStatus(2);
							AlertListDao.defaultDao().update(bean);
						}
					}
				} else {
					Log.d("CrtbWebService", "upload warn data failed.");
					pointUploadCounter.increase(false);
				}
			}

		};

		UploadWarningEntity originalData = new UploadWarningEntity();
		originalData.setLeijiAlert(curAlertInfo);
		originalData.setSectionCode(curAlertInfo.getSECTCODE());
		exceptedUploadAarmDataCount++;
		warningDataManager.uploadWarning(curAlertInfo, originalData, lister);

	}
	
	protected boolean hasUnhandledAlert(List<AlertInfo> alerts) {
		for (AlertInfo alertInfo : alerts) {
			if (alertInfo != null) {
				if (alertInfo.getAlertStatus() != Constant.ALERT_STATUS_HANDLED) {
					return true;
				}
			}
		}
		return false;
	}

	protected void uploadMeasureData(String sectionCode, final MeasureData measureData, final AlertInfo curAlertInfo, final DataCounter pointUploadCounter) {

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
				uploadWarnWrapper(curAlertInfo, pointUploadCounter);
			}

			@Override
			public void onFailed(String reason) {
				Log.e(Constant.LOG_TAG_SERVICE, TAG+"upload test data failed.");
				pointUploadCounter.increase(false);
			}
		});
	}

	class UploadSet {
		Section section;
		List<TunnelMeasureData> tunnelMeasureList;
		List<SubsidenceMeasureData> subMeasureList;
		List<String> orginalDataList;
		List<AlertInfo> alertList;
	}
}
