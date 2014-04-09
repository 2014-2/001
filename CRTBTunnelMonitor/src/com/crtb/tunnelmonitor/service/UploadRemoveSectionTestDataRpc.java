package com.crtb.tunnelmonitor.service;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.text.TextUtils;
import android.util.Log;

class UploadRemoveSectionTestDataRpc extends AbstractRpc {
	private static final String LOG_TAG = "UploadRemoveSectionTestDataRpc";
	private static final String KEY_ZONE_CODE = "工区编号";
	private static final String KEY_SITE_CODE = "隧道工点编号";
	private static final String KEY_SECTION_OR_POINT_STATUS = "断面或测点状态";
	private static final String KEY_SECTION_OR_POINT_CODE = "断面编码或测点编码";
	private static final String KEY_REMARK = "备注";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getRemoveSectionTestData";
	
	private Map<String, String> mParameters = new HashMap<String, String>();
	private RpcCallback mCallback;
	
	UploadRemoveSectionTestDataRpc(RpcCallback callback) {
		mParameters.put(KEY_ZONE_CODE, "");
		mParameters.put(KEY_SITE_CODE, "");
		mParameters.put(KEY_SECTION_OR_POINT_STATUS, "");
		mParameters.put(KEY_SECTION_OR_POINT_CODE, "");
		mParameters.put(KEY_REMARK, "");
		mParameters.put(KEY_RANDOM_CODE, "");
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
			notifyFailed("Response is NULL.");
			return;
		}
		if (!(response instanceof SoapObject)) {
			notifyFailed("Unknown reponse type: " + response.getClass().getName());
			return;
		}
		try {
			Log.d(LOG_TAG, "response: " + response);
			SoapObject result = (SoapObject) response;
			//TODO: Parse the response
		} catch (Exception e) {
			notifyFailed("Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	private void notifySuccess(Object[] data) {
		if (mCallback != null) {
			mCallback.onSuccess(data);
		}
	}
	
	private void notifyFailed(String reason) {
		if (mCallback != null) {
			mCallback.onFailed(reason);
		}
	}


}
