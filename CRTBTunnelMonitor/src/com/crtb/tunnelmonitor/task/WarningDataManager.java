package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertListDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.MergedAlert;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.WarningUploadParameter;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;
import com.crtb.tunnelmonitor.utils.SectionInterActionManager;

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
			ArrayList<MergedAlert> mal = AlertUtils.getMergedAlerts();
			Collections.sort(mal);
			if (mal != null && mal.size() > 0) {
			    for (MergedAlert ma : mal) {
			    	UploadWarningData warningData = new UploadWarningData();
			        warningData.setLeijiAlert(ma.getLeijiAlert());
			        warningData.setSulvAlert(ma.getSulvAlert());
					warningData.setSectionCode(warningData.getAlertInfo().getSECTCODE());
					uploadWarningDataList.add(warningData);
			    }
			}
			
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
    	final AlertInfo alertInfo = warningData.getAlertInfo();
    	WarningUploadParameter parameter = new WarningUploadParameter();
    	parameter.setSectionCode(warningData.getSectionCode());
    	parameter.setPointCode(warningData.getPointCode());
    	int level = alertInfo.getAlertLevel();
//YX 本地2级转换成服务器上的1，1->2   
//YX 服务器预警级别1：黄色预警;2:红色    	
		if ((level == 2)) {
			parameter.setWarningLevel(1);
		} else if (level == 1) {
			parameter.setWarningLevel(2);
		}
    	parameter.setWarningLevel(level);
    	
		float originalSpeedAlert = (float)alertInfo.getOriginalSulvAlertValue();
		if(originalSpeedAlert == 0){
			originalSpeedAlert = 0.001f;
		}
			
		float originalAccumulateAlertVale = (float)alertInfo.getOriginalLeiJiAlertValue();
		if(originalAccumulateAlertVale == 0){
			originalAccumulateAlertVale = 0.001f;
		}
		//YX 速率键对中存变形值
		parameter.setTransformSpeed(originalAccumulateAlertVale);
		//YX 变形键对中存速率值
		parameter.setWarningPointValue(originalSpeedAlert);
    	parameter.setWarningDate(CrtbUtils.parseDate(alertInfo.getDate()));
    	String duePerson = alertInfo.getDuePerson();
    	if (TextUtils.isEmpty(duePerson)) {
    	    duePerson = AppCRTBApplication.mUserName;
    	}
    	parameter.setWarningPerson(duePerson);
    	parameter.setWarningDescription(alertInfo.getHandling());
    	parameter.setWarningEndTime(CrtbUtils.parseDate(alertInfo.getHandlingTime()));
    	// On website, 1 means alert, 0 means closed. but local database & windows has the different define.
    	parameter.setWarningResult(alertInfo.getAlertStatus() == 1 ? 0 : 1);
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
    	private AlertInfo mLeijiAlert;
    	private AlertInfo mSulvAlert;
    	private String mSectionCode;
    	
    	public ArrayList<AlertInfo> getAlertInfos() {
    	    ArrayList<AlertInfo> as = new ArrayList<AlertInfo>();
    	    as.add(mLeijiAlert);
    	    as.add(mSulvAlert);
    	    return as;
    	}

    	public AlertInfo getAlertInfo() {
    	    if (mLeijiAlert != null) {
    	        return mLeijiAlert;
    	    } else {
    	        return mSulvAlert;
    	    }
    	}
	
    	public void setLeijiAlert(AlertInfo alertInfo) {
    		mLeijiAlert = alertInfo;
    	}
    	
    	public AlertInfo getLeijiAlert() {
    		return mLeijiAlert;
    	}
    	
    	public void setSectionCode(String sectionCode) {
    		mSectionCode = sectionCode;
    	}
    	
    	public String getSectionCode() {
    		return mSectionCode;
    	}
    	
    	public AlertInfo getSulvAlert() {
            return mSulvAlert;
        }

        public void setSulvAlert(AlertInfo sulvAlert) {
            mSulvAlert = sulvAlert;
        }

        public MergedAlert getMergedAlert() {
            MergedAlert ma = new MergedAlert();
            ma.setLeijiAlert(mLeijiAlert);
            ma.setSulvAlert(mSulvAlert);
            return ma;
        }

        
        public String getPointCode() {
        	//YX  根据开挖方法，获取点或线对应的测点上传序列    	
        	AlertInfo ai = getAlertInfo();
        	return SectionInterActionManager.getOneLineDetailsByPointType(ai.getSECTCODE(),ai.getPntType());
    	}
    }
}
