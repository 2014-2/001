package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelSettlementTotalDataDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.WarningUploadParameter;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

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
			ArrayList<AlertInfo> alertInfoList = AlertUtils.getAlertInfoList();
			if (alertInfoList != null && alertInfoList.size() > 0) {
				for(AlertInfo alertInfo : alertInfoList) {
					UploadWarningData warningData = new UploadWarningData();
					warningData.setAlertInfo(alertInfo);
					warningData.setSectionCode(getSectionCodeById(alertInfo.getSectionId()));
					uploadWarningDataList.add(warningData);
				}
			}
//			if (uploadWarningDataList.size() == 0) {
//				UploadWarningData fakeWarningData = new UploadWarningData();
//				AlertInfo fakeAlertInfo = new AlertInfo();
//				fakeAlertInfo.setPntType("A");
//				fakeAlertInfo.setAlertStatus(0);
//				fakeAlertInfo.setAlertStatusMsg("已销警");
//				fakeAlertInfo.setDate(CrtbUtils.formatDate(new Date()));
//				fakeWarningData.setAlertInfo(fakeAlertInfo);
//				fakeWarningData.setSectionCode("XPCL01SD00010001");
//				uploadWarningDataList.add(fakeWarningData);
//			}
			return uploadWarningDataList;
		}

		@Override
		protected void onPostExecute(List<UploadWarningData> result) {
			if (mLoadListener != null) {
				mLoadListener.done(result);
			}
		}

	}

	private String getSectionCodeById(int sectionId) {
		String sectionCode = "";
		TunnelCrossSectionExIndexDao sectionExIndexDao = TunnelCrossSectionExIndexDao.defaultDao();
		TunnelCrossSectionExIndex sectionExIndex = sectionExIndexDao.querySectionById(sectionId);
		if (sectionExIndex != null) {
			sectionCode = sectionExIndex.getSECTCODE();
		}
		return sectionCode;
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
    	final AlertInfo alertInfo = warningData.getAlertInfo();
    	WarningUploadParameter parameter = new WarningUploadParameter();
    	parameter.setSectionCode(warningData.getSectionCode());
    	parameter.setPointCode(warningData.getPointCode());
		int level = alertInfo.getAlertLevel();
		if ((level == 2) || (level == 3)) {
			parameter.setWarningLevel(1);
		} else if (level == 1) {
			parameter.setWarningLevel(2);
		}
    	parameter.setTransformSpeed((float)alertInfo.getUValue());
    	String originalID = alertInfo.getOriginalDataID();
    	List<Integer> ids = new ArrayList<Integer>();
        if (originalID.contains(AlertUtils.ORIGINAL_ID_DIVIDER)) {
            String[] idStrs = originalID.split(AlertUtils.ORIGINAL_ID_DIVIDER);
            for (String idStr : idStrs) {
                ids.add(Integer.valueOf(idStr));
            }
        } else {
            ids.add(Integer.valueOf(originalID));
        }
        float pointValue = 0.0f;
        TunnelSettlementTotalDataDao dao = TunnelSettlementTotalDataDao.defaultDao();
        if (ids.size() == 1) {
        	TunnelSettlementTotalData point = dao.queryOneById(ids.get(0));
        	pointValue = Float.parseFloat(point.getCoordinate().split(",")[2]);
        } else {
        	TunnelSettlementTotalData point1 = dao.queryOneById(ids.get(0));
        	TunnelSettlementTotalData point2 = dao.queryOneById(ids.get(1));
        	pointValue = (float) AlertUtils.getLineLength(point1, point2);
        }
    	parameter.setWarningPointValue(pointValue);
    	parameter.setWarningDate(CrtbUtils.parseDate(alertInfo.getDate()));
    	parameter.setWarningPerson("杨工");
    	parameter.setWarningDescription(alertInfo.getAlertStatusMsg());
    	parameter.setWarningEndTime(CrtbUtils.parseDate(alertInfo.getHandlingTime()));
    	parameter.setWarningResult(alertInfo.getAlertStatus());
    	parameter.setRemark(alertInfo.getHandling());
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
    	private AlertInfo mAlertInfo;
    	private String mSectionCode;
    	
    	public void setAlertInfo(AlertInfo alertInfo) {
    		mAlertInfo = alertInfo;
    	}
    	
    	public AlertInfo getAlertInfo() {
    		return mAlertInfo;
    	}
    	
    	public void setSectionCode(String sectionCode) {
    		mSectionCode = sectionCode;
    	}
    	
    	public String getSectionCode() {
    		return mSectionCode;
    	}
    	
    	public String getPointCode() {
    		String pointCode = "";
    		if ("A".equals(mAlertInfo.getPntType())) {
    			pointCode = mSectionCode + "GD01";
    		}
    		if ("S1".equals(mAlertInfo.getPntType())) {
    			pointCode = mSectionCode + "SL01" + "#" + mSectionCode + "SL02";
    		}
    		if ("S2".equals(mAlertInfo.getPntType())) {
    			pointCode = mSectionCode + "SL03" + "#" + mSectionCode + "SL04";
    		}
    		if ("S3".equals(mAlertInfo.getPntType())) {
    			pointCode = mSectionCode + "SL05" + "#" + mSectionCode + "SL06";
    		}
    		return pointCode;
    	}
    }
}
