package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AbstractDao;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.MergedAlert;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.infors.UploadWarningEntity;
import com.crtb.tunnelmonitor.task.AsyncUploadTask.UploadSet;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class SubsidenceAsyncUploadTask extends AsyncUploadTask {
	private final String TAG = "SubsidenceAsyncUploadTask: ";
	
	public SubsidenceAsyncUploadTask(UploadListener listener) {
		super(listener);
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
		List<SubsidenceMeasureData> measureList = new ArrayList<SubsidenceMeasureData>();
		for (MeasureData measureData : measureDataList) {
			SubsidenceMeasureData SubsidenceMeasureData = (SubsidenceMeasureData) measureData;
			originalDataList.add(SubsidenceMeasureData.getOriginalDataId());
			measureList.add(SubsidenceMeasureData);
		}
		uploadSet.subMeasureList = measureList;
		
		final List<AlertInfo> alerts = AlertUtils.getWarnDataList(originalDataList);
		if(alerts == null || alerts.size() < 1){
			return uploadSet;
		}
		uploadSet.alertList = alerts;
		
		if (Constant.getStrictRestrictedUpload()) {
			if (hasUnhandledAlert(alerts)) {
				return null;
			}
		}
		return uploadSet;
	}
	
	@Override
	protected void uploadSection(final UploadSet uploadSet, final DataCounter sectionUploadCounter) {
		final Section section = uploadSet.section;
		final SubsidenceCrossSectionIndex sectionIndex = ((SubsidenceSection)section).getSection();
        SectionUploadParamter paramter = new SectionUploadParamter();
        CrtbUtils.fillSectionParamter(sectionIndex, paramter);
        CrtbWebService.getInstance().uploadSection(paramter, new RpcCallback() {
            @Override
            public void onSuccess(Object[] data) {
                final String sectionCode = (String) data[0];
                section.setSectionCode(sectionCode);
                //将断面状态设置为已上传
				new Thread(new Runnable() {
					@Override
					public void run() {
						SubsidenceCrossSectionIndexDao dao = SubsidenceCrossSectionIndexDao.defaultDao();
						// sectionIndex.setInfo("2");
						sectionIndex.setUploadStatus(2);//表示该断面已上传
						dao.update(sectionIndex);
						SubsidenceCrossSectionExIndexDao sectionExIndexDao = SubsidenceCrossSectionExIndexDao.defaultDao();
						SubsidenceCrossSectionExIndex sectionExIndex = new SubsidenceCrossSectionExIndex();
						sectionExIndex.setSECT_ID(sectionIndex.getID());
						sectionExIndex.setSECTCODE(sectionCode);
						int code = sectionExIndexDao.insert(sectionExIndex);
						if (code != AbstractDao.DB_EXECUTE_SUCCESS) {
							 Log.e(Constant.LOG_TAG_ACTIVITY,TAG + "insert SubsidenceCrossSectionExIndex failed.");
						}
					}
				}).start();
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
	protected void uploadMeasureDataList(UploadSet uploadSet,  final DataCounter sectionUploadCounter) {
		final List<SubsidenceMeasureData> measureDataList = uploadSet.subMeasureList;
		final List<AlertInfo> alerts = uploadSet.alertList;
		final String sectionCode = uploadSet.section.getSectionCode();
		
		if (measureDataList != null && measureDataList.size() > 0) {
			DataCounter pointUploadCounter = new DataCounter("MeasureDataUploadCounter", measureDataList.size(), new CounterListener() {
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
	
	private void uploadMeasureDataWrapper(String sectionCode, final SubsidenceMeasureData measureData,final AlertInfo curAlertInfo, final DataCounter pointUploadCounter) {
		//final AlertInfo curAlertInfo = AlertUtils.getWarnData(measureData.getOriginalDataId());
		if (measureData.uploaded) {
			uploadWarnWrapper(curAlertInfo,pointUploadCounter);
		} else {
			uploadMeasureData(sectionCode,measureData,curAlertInfo,pointUploadCounter);
		}
	}
}
