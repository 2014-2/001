package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AbstractDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.dao.impl.v2.CrossSectionStopSurveyingDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.MergedAlert;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;
import com.crtb.tunnelmonitor.infors.UploadWarningEntity;
import com.crtb.tunnelmonitor.task.AsyncUploadTask.UploadSet;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class TunnelAsyncUploadTask extends AsyncUploadTask {
	private static final String TAG = "TunnelAsyncUploadTask：";

	public TunnelAsyncUploadTask(UploadListener listener) {
		super(listener);
		warningDataManager = new WarningDataManager();
	}

	@Override
	protected UploadSet queryUploadSheet(Section section){
		UploadSet uploadSet = new UploadSet();
		uploadSet.section = section;
		List<MeasureData> measureDataList = section.getMeasureData();
		if (measureDataList == null || measureDataList.size() < 1) {
			return uploadSet;
		}
			
		List<String> originalDataList = new ArrayList<String>();
		List<TunnelMeasureData> measureList = new ArrayList<TunnelMeasureData>();
		for (MeasureData measureData : measureDataList) {
			TunnelMeasureData tunnelMeasureData = (TunnelMeasureData) measureData;
			originalDataList.add(tunnelMeasureData.getOriginalDataId());
			measureList.add(tunnelMeasureData);
		}
		
		
		final List<AlertInfo> alerts = AlertUtils.getWarnDataList(originalDataList);
		if (Constant.getStrictRestrictedUpload()) {
			if (hasUnhandledAlert(alerts)) {
				return null;
			}
		}
		uploadSet.alertList = alerts;
		uploadSet.tunnelMeasureList = measureList;
		
		return uploadSet;
	} 
	
	@Override
	protected void uploadSection(final UploadSet uploadSet, final DataCounter sectionUploadCounter) {
		final Section section = uploadSet.section;
		final TunnelCrossSectionIndex sectionIndex = ((TunnelSection) section).getSection();
		SectionUploadParamter paramter = new SectionUploadParamter();
		CrtbUtils.fillSectionParamter(sectionIndex, paramter);
		CrtbWebService.getInstance().uploadSection(paramter, new RpcCallback() {
			@Override
			public void onSuccess(Object[] data) {
				final String sectionCode = (String) data[0];
				section.setSectionCode(sectionCode);
				// 将断面状态设置为已上传
				new Thread(new Runnable() {
					@Override
					public void run() {
						TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
						// sectionIndex.setInfo("2");
						sectionIndex.setUploadStatus(2); // 表示该断面已上传
						dao.update(sectionIndex);
						TunnelCrossSectionExIndexDao sectionExIndexDao = TunnelCrossSectionExIndexDao.defaultDao();
						TunnelCrossSectionExIndex sectionExIndex = new TunnelCrossSectionExIndex();
						sectionExIndex.setSECT_ID(sectionIndex.getID());
						sectionExIndex.setSECTCODE(sectionCode);
						int code = sectionExIndexDao.insert(sectionExIndex);
						if (code != AbstractDao.DB_EXECUTE_SUCCESS) {
							Log.e(Constant.LOG_TAG_SERVICE,TAG + "insert TunnelCrossSectionExIndex failed.");
						}
					}
				}).start();
				Log.d(Constant.LOG_TAG_SERVICE, TAG + "upload section success: section_code: " + sectionCode);
				uploadMeasureDataList(uploadSet, sectionUploadCounter);
			}

			@Override
			public void onFailed(String reason) {
				Log.d(Constant.LOG_TAG_SERVICE, TAG + "upload section faled: " + reason);
				sectionUploadCounter.increase(false);
			}
		});
	}

	@Override
	protected void uploadMeasureDataList(UploadSet uploadSet, final DataCounter sectionUploadCounter) {
        final List<TunnelMeasureData> measureDataList = uploadSet.tunnelMeasureList;
        final List<AlertInfo> alerts = uploadSet.alertList;
		final String sectionCode = uploadSet.section.getSectionCode();
        if (measureDataList != null && measureDataList.size() > 0) {
			DataCounter pointUploadCounter = new DataCounter("MeasureDataUploadCounter"+sectionCode, measureDataList.size(), new CounterListener() {
				@Override
				public void done(boolean success) {
					sectionUploadCounter.increase(success);
				}
			});
			
			int count = measureDataList.size();
			for (int i = 0; i < count; i++) {
				uploadMeasureDataWrapper(sectionCode, measureDataList.get(i), alerts.get(i), pointUploadCounter);
			}
		} else {
			// 如果没有测点数据，则直接判断为上传断面成功
			sectionUploadCounter.increase(true);
		}
	}

	private void uploadMeasureDataWrapper(String sectionCode, final TunnelMeasureData measureData,final AlertInfo curAlertInfo, DataCounter pointUploadCounter) {
		if (measureData.uploaded) {
			uploadWarnWrapper(curAlertInfo,pointUploadCounter);
		} else {
			uploadMeasureData(sectionCode,measureData,curAlertInfo,pointUploadCounter);
		}
	}
}
