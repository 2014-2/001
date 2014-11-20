package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.zw.android.framework.util.StringUtils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.AlertHandlingInfoDao;
import com.crtb.tunnelmonitor.entity.AlertHandlingList;
import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.MergedAlert;
import com.crtb.tunnelmonitor.infors.UploadWarningEntity;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.DataCounter;
import com.crtb.tunnelmonitor.network.DataCounter.CounterListener;
import com.crtb.tunnelmonitor.network.RpcCallback;
import com.crtb.tunnelmonitor.network.WarningUploadParameter;
import com.crtb.tunnelmonitor.utils.AlertUtils;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

public class WarningDataManager {
	private static final String TAG = "WarningDataManager：";
	
    /**
     * 预警数据集
     * @author xu
     * 
     */
    private class WarningDataSet{
    	/**
    	 * 预警数据
    	 */
    	public UploadWarningEntity orginalData;
    	
    	/**
    	 * 预警对应的处理详情
    	 */
    	public List<AlertHandlingList> handlings;
    	
    	public WarningUploadListener uploadListener;
    } 

	private WarningLoadListener mLoadListener;
	
	/**
	 * 上传预警数据统计
	 */
	public int actualUploadAarmDataCount = 0;
	
	/**
	 * 上传预警数据统计
	 */
	public int exceptedUploadAarmDataCount = 0;
	
	private String notice = "";
	public String getNotice(){
		String alarmNotice;
		if(!StringUtils.isEmpty(notice)){
			return notice;
		}
		
		if(exceptedUploadAarmDataCount == 0){
			alarmNotice = "无有效预警数据";
		} else {
			if(actualUploadAarmDataCount == exceptedUploadAarmDataCount){
				alarmNotice = "预警数据上传成功";
			} else if(actualUploadAarmDataCount > 0){
				alarmNotice = "部分预警数据上传成功";
			} else {
				alarmNotice = "预警数据上传失败";
			}
		}
		
		notice = alarmNotice + "! ";
		
		return notice;
	}
	
	/**
	 * 预警加载 
	 * @author xu
	 *
	 */
	public interface WarningLoadListener {
		/**
		 * 数据加载完毕
		 * 
		 * @param uploadDataList
		 *            等待上传的数据
		 */
		public void done(List<UploadWarningEntity> uploadDataList);
	}

	/**
	 * 数据上传
	 *
	 */
	public interface WarningUploadListener {
		/**
		 * 
		 * @param success
		 * @param hasData 是否上传了数据
		 */
		public void done(boolean success,boolean hasData);
	}
	
	/**
	 * 根据断面类型加载预警
	 * @param sectionType 断面类型
	 * @param loadListener
	 */
	public void loadDataSortBySectionType(String sectionType,WarningLoadListener loadListener) {
		mLoadListener = loadListener;
		new WaringSortBySectionTypeDataLoadTask().execute(sectionType);
	}
	
	/**
	 * 根据断面的Guid加载预警
	 * @param sectionGuid 断面Guid
	 * @param loadListener
	 */
	public void loadDataSortBySectionGuid(String sectionGuid,WarningLoadListener loadListener) {
		mLoadListener = loadListener;
		new WaringSortBySectionGuidDataLoadTask().execute(sectionGuid);
	}
	
	/**
     * 上传预警
     * @param alertInfo 报警信息
     * @param listener 回调
     * @param originalData 包装后的预警数据
     */
    public void uploadWarning(AlertInfo alertInfo,UploadWarningEntity originalData,final WarningUploadListener listener){

    	int alertId = alertInfo.getAlertId();
		List<AlertHandlingList> handlings = AlertHandlingInfoDao.defaultDao().queryByAlertIdOrderByHandlingTimeAscAndNoUpload(alertId);
		if(handlings == null || handlings.size() < 1){
			AlertHandlingList ahl = AlertHandlingInfoDao.defaultDao().queryNoHandlingInfoByAlertId(alertInfo);
			if (ahl != null) {
				handlings = new ArrayList<AlertHandlingList>();
				handlings.add(ahl);
			} else {
				// 所有处理详情已经上传了，且没有没有处理详情的情况
				listener.done(true,false);
				return;
			}
    	}
		
       	if(handlings.size() > 1){
       		//排除第一条处理详情为null的数据
       		String handlingInfo = handlings.get(0).getInfo();
       		if(handlingInfo == null || handlingInfo.equals("")){
       			handlings.remove(0);
       		}
       	}
       	
		uploadWarningData(originalData,handlings,listener);
    }
	
	/**
	 * 根据断面类型加载预警Task
	 *
	 */
	
	private class WaringSortBySectionTypeDataLoadTask extends AsyncTask<String, Void, List<UploadWarningEntity>> {

		@Override
		protected List<UploadWarningEntity> doInBackground(String... params) {
			String sectionType = params[0];
			List<UploadWarningEntity> uploadWarningDataList = new ArrayList<UploadWarningEntity>();
			ArrayList<MergedAlert> mal = AlertUtils.getMergedAlertsBySectionType(sectionType);
			Collections.sort(mal);
			if (mal != null && mal.size() > 0) {
			    for (MergedAlert ma : mal) {
			    	UploadWarningEntity warningData = new UploadWarningEntity();
			        warningData.setLeijiAlert(ma.getLeijiAlert());
			        warningData.setSulvAlert(ma.getSulvAlert());
					warningData.setSectionCode(warningData.getAlertInfo().getSECTCODE());
					uploadWarningDataList.add(warningData);
			    }
			}
			
			return uploadWarningDataList;
		}

		@Override
		protected void onPostExecute(List<UploadWarningEntity> result) {
			if (mLoadListener != null) {
				mLoadListener.done(result);
			}
		}

	}
	
	
	/**
	 * 根据断面Guid加载预警Task
	 *
	 */
	private class WaringSortBySectionGuidDataLoadTask extends AsyncTask<String, Void, List<UploadWarningEntity>> {

		@Override
		protected List<UploadWarningEntity> doInBackground(String... params) {
			String sectionGuid = params[0];
			List<UploadWarningEntity> uploadWarningDataList = new ArrayList<UploadWarningEntity>();
			ArrayList<MergedAlert> mal = AlertUtils.getMergedAlertsBySectionGuid(sectionGuid);
			Collections.sort(mal);
			if (mal != null && mal.size() > 0) {
			    for (MergedAlert ma : mal) {
			    	UploadWarningEntity warningData = new UploadWarningEntity();
			        warningData.setLeijiAlert(ma.getLeijiAlert());
			        warningData.setSulvAlert(ma.getSulvAlert());
					warningData.setSectionCode(warningData.getAlertInfo().getSECTCODE());
					uploadWarningDataList.add(warningData);
			    }
			}
			
			return uploadWarningDataList;
		}

		@Override
		protected void onPostExecute(List<UploadWarningEntity> result) {
			if (mLoadListener != null) {
				mLoadListener.done(result);
			}
		}

	}

	
	/**
	 * 预警上传
	 * @param originalData
	 * @param handlings
	 * @param uploadListener
	 */
	public void uploadWarningData(UploadWarningEntity originalData, List<AlertHandlingList> handlings,WarningUploadListener uploadListener){
		WarningDataSet dataSet = new WarningDataSet();
		dataSet.orginalData = originalData;
		dataSet.handlings = handlings;
		dataSet.uploadListener = uploadListener;
		new DataUploadTask().execute(dataSet);
	}
		
	
	/**
	 * 数据上传
	 *
	 */
	private class DataUploadTask extends AsyncTask<WarningDataSet, Void, Void> {
		
		protected Void doInBackground(WarningDataSet...params) {
			if (params != null && params.length > 0) {
				final WarningDataSet dataSet = params[0];
				if (dataSet != null && dataSet.handlings.size() > 0) {
					DataCounter warningUploadCounter = new DataCounter("WarningUploadCounter", dataSet.handlings.size(), new CounterListener() {
						@Override
						public void done(boolean success) {
							if (dataSet.uploadListener != null) {
								dataSet.uploadListener.done(success,true);
							}
						}
					});
					exceptedUploadAarmDataCount++;
					uploadToServer(dataSet.orginalData,dataSet.handlings,warningUploadCounter);
				}
			}
			return null;
		}
		
		/**
		 * 上传到服务器
		 * @param warningData 预警实体
		 * @param handlings 处理详情
		 * @param warningUploadCounter
		 */
		private void uploadToServer(final UploadWarningEntity warningData,final List<AlertHandlingList> handlings, final DataCounter warningUploadCounter) {
	    	
	    	final AlertInfo alertInfo = warningData.getAlertInfo();
	    	String sectionCode = warningData.getSectionCode();
	    	String pointCode = warningData.getPointCode();
	    	if(sectionCode == null || StringUtils.isEmpty(pointCode)){
	    		notice = "请先上传数据，再上传预警";
	    		warningUploadCounter.increase(false);
	    		return;
	    	}
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
	                	actualUploadAarmDataCount++;
	                	//设置处理详情的上传状态:2表示已上传,默认为1
	                	AlertHandlingInfoDao.defaultDao().updateUploadStatus(curHandingID, 2);
	                	warningUploadCounter.increase(true);	
	                	Log.d(Constant.LOG_TAG_SERVICE, TAG + "AlertHandlingID = "+ curHandingID + "upload success.");
	                }

	                @Override
	                public void onFailed(String reason) {
	                	warningUploadCounter.increase(false);
	                	if(reason == null){
	                		reason = "empty";
	                	}
	                	Log.d(Constant.LOG_TAG_SERVICE, TAG + "AlertHandlingID = "+ curHandingID + "upload failed." + reason);
	                }
	            });
	    	}
	    }
	}
}
