package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.WarningUploadParameter;

public class WarningDataManager {
	private static final String LOG_TAG = "WarningDataManager";

	public interface WarningLoadListener {
		/**
		 * 数据加载完毕
		 * 
		 * @param uploadDataList
		 *            等待上传的数据
		 */
		public void done(List<AlertInfo> uploadDataList);
	}

	public interface WarningUploadListener {
		/**
		 * 
		 * @param success
		 */
		public void done(boolean success);
	}

	private WarningLoadListener mLoadListener;
	private WarningUploadListener mUploadListener;

	public void loadData(WarningLoadListener loadListener) {
		mLoadListener = loadListener;
		new DataLoadTask().execute();
	}

	public void uploadData(List<AlertInfo> warningDataList, WarningUploadListener uploadListener) {
		mUploadListener = uploadListener;
		new DataUploadTask().execute(warningDataList);
	}

	private class DataLoadTask extends AsyncTask<Void, Void, List<AlertInfo>> {

		@Override
		protected List<AlertInfo> doInBackground(Void... params) {
			List<AlertInfo> uploadWarningDataList = new ArrayList<AlertInfo>();
			ArrayList<AlertInfo> alertInfoList = AlertUtils.getAlertInfoList();
			if (alertInfoList != null) {
				uploadWarningDataList = alertInfoList;
			}
			if (uploadWarningDataList.size() == 0) {
				AlertInfo alertInfo = new AlertInfo();
				alertInfo.setPntType("A");
				alertInfo.setAlertStatus(0);
				alertInfo.setAlertStatusMsg("已销警");
				alertInfo.setDate(CrtbUtils.formatDate(new Date()));
				uploadWarningDataList.add(alertInfo);
			}
			return uploadWarningDataList;
		}

		@Override
		protected void onPostExecute(List<AlertInfo> result) {
			if (mLoadListener != null) {
				mLoadListener.done(result);
			}
		}

	}

	private class DataUploadTask extends AsyncTask<List<AlertInfo>, Void, Void> {

		@Override
		protected Void doInBackground(List<AlertInfo>... params) {
			if (params != null && params.length > 0) {
				List<AlertInfo> warningDataList = params[0];
				if (warningDataList != null && warningDataList.size() > 0) {
					DataCounter warningUploadCounter = new DataCounter("WarningUploadCounter", warningDataList.size(), new CounterListener() {
						@Override
						public void done(boolean success) {
							if (mUploadListener != null) {
								mUploadListener.done(success);
							}
						}
					});
					for(AlertInfo warningData : warningDataList) {
						uploadWarningData(warningData, warningUploadCounter);
					}
				}
			}
			return null;
		}
	}

    private void uploadWarningData(AlertInfo warningData, final DataCounter warningUploadCounter) {
    	WarningUploadParameter parameter = new WarningUploadParameter();
    	parameter.setSectionCode("XPCL01SD00010001");
    	parameter.setPointCode("XPCL01SD00010001GD01");
    	parameter.setWarningLevel(1);
    	parameter.setTransformSpeed(6.0f);
    	parameter.setWarningPointValue(1.0f);
    	parameter.setWarningDate(new Date());
    	parameter.setWarningPerson("杨工");
    	parameter.setWarningDescription("abc");
    	parameter.setWarningEndTime(new Date());
    	parameter.setWarningResult(0);
    	parameter.setRemark("kkk");
        CrtbWebService.getInstance().uploadWarningData(parameter, new RpcCallback() {

            @Override
            public void onSuccess(Object[] data) {
            	warningUploadCounter.increase(true);
                Log.d(LOG_TAG, "upload warning data success.");
            }

            @Override
            public void onFailed(String reason) {
            	warningUploadCounter.increase(false);
                Log.d(LOG_TAG, "upload warning data failed.");
            }
        });
    }
}
