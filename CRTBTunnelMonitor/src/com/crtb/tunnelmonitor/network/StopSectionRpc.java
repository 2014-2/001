package com.crtb.tunnelmonitor.network;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.crtb.tunnelmonitor.task.SectionStopEntity;

import android.util.Log;

public final class StopSectionRpc extends AbstractRpc {
	//-<message name="getRemoveSectionTestData">
	//
	//<part name="工区编号" type="xsd:string"/>
	//
	//<part name="隧道工点编号" type="xsd:string"/>
	//
	//<part name="断面或测点状态" type="xsd:int"/>
	//
	//<part name="断面编码或测点编码" type="xsd:string"/>
	//
	//<part name="备注" type="xsd:string"/>
	//
	//<part name="随机码" type="xsd:long"/>
	
	private static final String LOG_TAG = "StopSectionRpc";
	private static final String KEY_WORK_AREA_CODE = "工区编号";
	private static final String KEY_WORK_SITE_CODE = "隧道工点编号";
	private static final String KEY_STATE     = "断面或测点状态";
	private static final String KEY_SECTION_CODE   = "断面编码或测点编码";
	private static final String KEY_REMARK    = "备注";
	private static final String KEY_RANDOM    = "随机码";
	private static final String KEY_ACTION = "getRemoveSectionTestData";
	
    private Map<String, Object> mParameters = new HashMap<String, Object>();
	
	private RpcCallback mCallback;
	
	public StopSectionRpc(SectionStopEntity sectionStop, long randomCode, RpcCallback callback) {
		mParameters.put(KEY_WORK_AREA_CODE,sectionStop.workAreaCode);
		mParameters.put(KEY_WORK_SITE_CODE,sectionStop.workSiteCode);
		mParameters.put(KEY_STATE,sectionStop.sectionOrPointState);
		mParameters.put(KEY_SECTION_CODE, sectionStop.sectionCode);
		mParameters.put(KEY_REMARK,sectionStop.remark);
		mParameters.put(KEY_RANDOM, randomCode);
		mCallback = callback;
		
	}
	

	@Override
	public SoapObject getRpcMessage(String namesapce) {
		SoapObject message = new SoapObject(namesapce, KEY_ACTION);
		for(String key : mParameters.keySet()) {
			message.addProperty(key, mParameters.get(key));
		}
		return message;
	}

	@Override
	public void onResponse(Object response) {
		if (response == null) {
			notifyFailed("请检查网络连接");
			return;
		}
		if (!(response instanceof SoapObject)) {
			notifyFailed("服务返回信息格式错误");
			//notifyFailed("Unknown reponse type: " + response.getClass().getName());
			return;
		}
		try {
			Log.d(LOG_TAG, "response: " + response);
			SoapObject result = (SoapObject) response;
			int code = Integer.parseInt(result.getPropertyAsString(0));
			if (code == 1) {
				notifySuccess();
			} else if(code == 0) {
				notifyFailed("封存失败");
			} else if(code == 2){
				notifyFailed("请先登录");
			}
		} catch (Exception e) {
			notifyFailed("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void notifySuccess() {
		if (mCallback != null) {
			mCallback.onSuccess(null);
		}
	}
	
	private void notifyFailed(String reason) {
		if (mCallback != null) {
			mCallback.onFailed(reason);
		}
	}
	
}
