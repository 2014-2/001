package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

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
		public void done(List<UploadWarningData> uploadDataList);
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

	public void uploadData(List<UploadWarningData> warningDataList, WarningUploadListener uploadListener) {
		mUploadListener = uploadListener;
		new DataUploadTask().execute(warningDataList);
	}

	private class DataLoadTask extends AsyncTask<Void, Void, List<UploadWarningData>> {

		@Override
		protected List<UploadWarningData> doInBackground(Void... params) {
			List<UploadWarningData> uploadWarningDataList = new ArrayList<UploadWarningData>();
			// TODO Auto-generated method stub
			return uploadWarningDataList;
		}

		@Override
		protected void onPostExecute(List<UploadWarningData> result) {
			if (mLoadListener != null) {
				mLoadListener.done(result);
			}
		}

	}

	private class DataUploadTask extends AsyncTask<List<UploadWarningData>, Void, Void> {

		@Override
		protected Void doInBackground(List<UploadWarningData>... params) {
			if (params != null && params.length > 0) {
				List<UploadWarningData> warningDataList = params[0];
				if (warningDataList != null && warningDataList.size() > 0) {
					DataCounter warningUploadCounter = new DataCounter("WarningUploadCounter", warningDataList.size(), new CounterListener() {
						@Override
						public void done(boolean success) {
							if (mUploadListener != null) {
								mUploadListener.done(success);
							}
						}
					});
					for(UploadWarningData warningData : warningDataList) {
						uploadWarningData(warningData, warningUploadCounter);
					}
				}
			}
			return null;
		}
	}

    private void uploadWarningData(UploadWarningData warningData, final DataCounter warningUploadCounter) {
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
    
	public class UploadWarningData {

	}
}
