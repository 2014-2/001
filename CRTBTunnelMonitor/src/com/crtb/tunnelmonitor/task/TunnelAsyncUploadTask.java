package com.crtb.tunnelmonitor.task;

import java.util.List;

import android.util.Log;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class TunnelAsyncUploadTask extends AsyncUploadTask {
	private static final String LOG_TAG = "TunnelAsyncUploadTask";
	
	public TunnelAsyncUploadTask(UploadListener listener) {
		super(listener);
	}

	@Override
	protected void uploadSection(final Section section, int sheetId, final DataCounter sectionUploadCounter) {
		final TunnelCrossSectionIndex sectionIndex = ((TunnelSection) section).getSection();
        SectionUploadParamter paramter = new SectionUploadParamter();
        CrtbUtils.fillSectionParamter(sectionIndex, paramter);
        CrtbWebService.getInstance().uploadSection(paramter, new RpcCallback() {
            @Override
            public void onSuccess(Object[] data) {
                final String sectionCode = (String) data[0];
                //将断面状态设置为已上传
				new Thread(new Runnable() {
					@Override
					public void run() {
						TunnelCrossSectionIndexDao dao = TunnelCrossSectionIndexDao.defaultDao();
						sectionIndex.setInfo("2");
						dao.update(sectionIndex);
						TunnelCrossSectionExIndexDao sectionExIndexDao = TunnelCrossSectionExIndexDao.defaultDao();
						TunnelCrossSectionExIndex sectionExIndex = new TunnelCrossSectionExIndex();
						sectionExIndex.setID(sectionIndex.getID());
						sectionExIndex.setSECTCODE(sectionCode);
						sectionExIndexDao.insert(sectionExIndex);
					}
				}).start();
				Log.d(LOG_TAG, "upload section success: section_code: " + sectionCode);
				uploadMeasureDataList(sectionCode, section.getMeasureData(), sectionUploadCounter);
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload section faled: " + reason);
                sectionUploadCounter.increase(false);
            }
        });
		
	}

	@Override
	protected void uploadMeasureDataList(String sectionCode, List<MeasureData> measureDataList, final DataCounter sectionUploadCounter) {
         if (measureDataList != null && measureDataList.size() > 0) {
        	 DataCounter pointUploadCounter = new DataCounter("MeasureDataUploadCounter", measureDataList.size(), new CounterListener() {
                 @Override
                 public void done(boolean success) {
                     sectionUploadCounter.increase(success);
                 }
             });
             for(MeasureData measureData : measureDataList) {
                 uploadMeasureData(sectionCode, (TunnelMeasureData)measureData, pointUploadCounter);
             }
         } else {
             // 如果没有测点数据，则直接判断为上传断面成功
             sectionUploadCounter.increase(true);
         }
	}

	private void uploadMeasureData(String sectionCode, final TunnelMeasureData measureData, final DataCounter pointUploadCounter) {
		PointUploadParameter parameter = new PointUploadParameter();
        parameter.setSectionCode(sectionCode);
        parameter.setPointCodeList(measureData.getPointCodeList(sectionCode));
        parameter.setTunnelFaceDistance(measureData.getFaceDistance());
        parameter.setProcedure("02");
        parameter.setMonitorModel(measureData.getMonitorModel());
        parameter.setMeasureDate(measureData.getMeasureDate());
        parameter.setPointValueList(measureData.getValueList());
        parameter.setPointCoordinateList(measureData.getCoordinateList());
        parameter.setSurveyorName(CrtbUtils.getSurveyorName());
        parameter.setSurveyorId(CrtbUtils.getSurveyorCertificateID());
        parameter.setRemark("yyy");
        CrtbWebService.getInstance().uploadTestResult(parameter, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
                CrtbWebService.getInstance().confirmSubmitData(new RpcCallback() {

                    @Override
                    public void onSuccess(Object[] data) {
                        measureData.markAsUploaded();
                        Log.d(LOG_TAG, "upload test data success.");
                        pointUploadCounter.increase(true);
                    }

                    @Override
                    public void onFailed(String reason) {
                        Log.d(LOG_TAG, "confirm test data failed: " + reason);
                        pointUploadCounter.increase(false);
                    }
                });
            }

            @Override
            public void onFailed(String reason) {
                Log.d(LOG_TAG, "upload test data failed.");
                pointUploadCounter.increase(false);
            }
        });
	}

}
