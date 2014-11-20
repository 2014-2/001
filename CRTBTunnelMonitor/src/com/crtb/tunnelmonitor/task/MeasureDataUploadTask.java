package com.crtb.tunnelmonitor.task;

import java.util.List;

import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class MeasureDataUploadTask extends SequenceUploadTask {
	private static final String TAG = "MeasureDataUploadTask：";
	private List<MeasureData> mMeasureDataList;
	private String mSectionCode;
	private int mPosition;
	private DataCounter mUploadCounter;
	private TaskCallback mCallback;
	
	public MeasureDataUploadTask(String sectionCode, List<MeasureData> measureDataList) {
		if (measureDataList != null && measureDataList.size() > 0) {
			mSectionCode = sectionCode;
			mMeasureDataList = measureDataList;
			mUploadCounter = new DataCounter("MeasureDataUploadCounter", mMeasureDataList.size(), new CounterListener() {
				@Override
				public void done(boolean success) {
					Log.d(Constant.LOG_TAG_SERVICE, TAG + "finished: " + success);
				}
			});
		} else {
			mCallback.done(true);
			Log.w(Constant.LOG_TAG_SERVICE, TAG + "empty data.");
		}
	}

	@Override
	public void start() {
		mPosition = 0;
		MeasureData data = getNextData();
		if (data != null) {
			uploadData(data);
		} else {
			mCallback.done(true);
		}
	}
	
	public void onProgress(boolean success) {
		MeasureData measureData = getNextData();
		if (measureData != null) {
			uploadData(measureData);
		}
	}
	
	public void uploadData(MeasureData measureData) {
		
		
	}
	
	private MeasureData getNextData() {
		MeasureData data = null;
		if (mPosition < mMeasureDataList.size()) {
			data = mMeasureDataList.get(mPosition++);
		}
		return data;
	}

	private void uploadMeasureData(final TunnelMeasureData measureData) {
		PointUploadParameter parameter = new PointUploadParameter();
        parameter.setSectionCode(mSectionCode);
        parameter.setPointCodeList(measureData.getPointCodeList(mSectionCode));
        parameter.setTunnelFaceDistance(50.0f);
        parameter.setProcedure("02");
        parameter.setMonitorModel("xxx");
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
                        Log.d(Constant.LOG_TAG_SERVICE, TAG + "upload test data success.");
                        onProgress(true);
                    }

                    @Override
                    public void onFailed(String reason) {
                        Log.d(Constant.LOG_TAG_SERVICE, TAG + "confirm test data failed: " + reason);
                        onProgress(true);
                    }
                });
            }

            @Override
            public void onFailed(String reason) {
                Log.d(Constant.LOG_TAG_SERVICE, TAG + "upload test data failed.");
                onProgress(false);
            }
        });
	}
}
