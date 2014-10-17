package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertInfo;
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

	public void uploadWarningData(UploadWarningData originalData, List<AlertHandlingList> handlings,WarningUploadListener uploadListener){
		mUploadListener = uploadListener;
		WarningDataSet dataSet = new WarningDataSet();
		dataSet.orginalData = originalData;
		dataSet.handlings = handlings;
		new DataUploadTask().execute(dataSet);
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
	
	private class DataUploadTask extends AsyncTask<WarningDataSet, Void, Void> {
		
		protected Void doInBackground(WarningDataSet...params) {
			if (params != null && params.length > 0) {
				WarningDataSet dataSet = params[0];
				if (dataSet != null && dataSet.handlings.size() > 0) {
					DataCounter warningUploadCounter = new DataCounter("WarningUploadCounter", dataSet.handlings.size(), new CounterListener() {
						@Override
						public void done(boolean success) {
							if (mUploadListener != null) {
								mUploadListener.done(success);
							}
						}
					});
					upload(dataSet.orginalData,dataSet.handlings,warningUploadCounter);
				}
			}
			return null;
		}
	}

    private void upload(final UploadWarningData warningData,final List<AlertHandlingList> handlings, final DataCounter warningUploadCounter) {
    	
    	final AlertInfo alertInfo = warningData.getAlertInfo();
    	String sectionCode = warningData.getSectionCode();
    	String pointCode = warningData.getPointCode();
    	int level = alertInfo.getAlertLevel();
    	int serverLevel = 0;
    	// On website, 1 means alert, 0 means closed. but local database & windows has the different define.
    	int result = alertInfo.getAlertStatus() == 1 ? 0 : 1;
    	Date warningDate = CrtbUtils.parseDate(alertInfo.getDate());

		float originalSpeedAlert = (float)alertInfo.getOriginalSulvAlertValue();
		if(originalSpeedAlert == 0){
			originalSpeedAlert = 0.001f;
		}
			
		float originalAccumulateAlertVale = (float)alertInfo.getOriginalLeiJiAlertValue();
		if(originalAccumulateAlertVale == 0){
			originalAccumulateAlertVale = 0.001f;
		}
		
		//速率报警GONGDINGI_XIACHEN_SULV_EXCEEDING == 1,SHOULIAN_SULV_EXCEEDING == 3, DIBIAO_XIACHEN_SULV_EXCEEDING == 5
		int uType = alertInfo.getUType();
		if(uType == 1 || uType == 3 || uType == 5){
			originalAccumulateAlertVale = 0.001f;
			//速率报警时，默认设置为本地二级，服务器为黄色
			serverLevel = 2;
		}
		
		//YX 本地2级转换成服务器上的1，1->2   
		//YX 服务器预警级别1：黄色预警;2:红色    	
		if ((level == 2)) {
			serverLevel = 1;
			
		} else if (level == 1) {
			serverLevel = 2;
		}
		
    	int count = handlings.size();
    	for(int i = 0; i< count; i++){
    		AlertHandlingList curHandle = handlings.get(i);
        	String duePerson = curHandle.getDuePerson();
        	if (TextUtils.isEmpty(duePerson)) {
        	    duePerson = AppCRTBApplication.mUserName;
        	}
        	WarningUploadParameter parameter = new WarningUploadParameter();
        	parameter.setSectionCode(sectionCode);
        	parameter.setPointCode(pointCode);
        	parameter.setWarningLevel(serverLevel);
        	//YX 速率键对中存变形值
    		parameter.setTransformSpeed(originalAccumulateAlertVale);
    		//YX 变形键对中存速率值
    		parameter.setWarningPointValue(originalSpeedAlert);
        	parameter.setWarningDate(warningDate);
        	parameter.setWarningPerson(duePerson);
        	parameter.setWarningDescription(curHandle.getInfo());
        	parameter.setWarningEndTime(curHandle.getHandlingTime());
        	parameter.setWarningResult(result);
    		parameter.setRemark(curHandle.getInfo() == null ? "" : curHandle.getInfo());
    		final int curHandingID = curHandle.getID();
            CrtbWebService.getInstance().uploadWarningData(parameter, new RpcCallback() {

                @Override
                public void onSuccess(Object[] data) {
                	//设置处理详情的上传状态:2表示已上传,默认为1
                	AlertHandlingInfoDao.defaultDao().updateUploadStatus(curHandingID, 2);
                	warningUploadCounter.increase(true);	
                    Log.d(LOG_TAG, "AlertHandlingID = "+ curHandingID + "upload success.");
                }

                @Override
                public void onFailed(String reason) {
                	warningUploadCounter.increase(false);
                	if(reason == null){
                		reason = "empty";
                	}
                	Log.d(LOG_TAG, "AlertHandlingID = "+ curHandingID + "upload failed." + reason);
                }
            });
    	}
    }
    
    private class WarningDataSet{
    	public UploadWarningData orginalData;
    	public List<AlertHandlingList> handlings;
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
