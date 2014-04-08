package com.crtb.tunnelmonitor.service;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

class GetTestCodesRpc extends AbstractRpc {
	private static final String LOG_TAG = "GetTestCodesRpc";
	private static final String KEY_SECTION_CODE = "断面编号";
	private static final String KEY_POINT_STATUS = "测点状态";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getTestCodes";
	
	private Map<String, String> mParameters = new HashMap<String, String>();
	private RpcCallback mCallback;
	
	GetTestCodesRpc(String sectionCode, String pointStatus, String randomCode, RpcCallback callback) {
		mParameters.put(KEY_SECTION_CODE, sectionCode);
		mParameters.put(KEY_POINT_STATUS, pointStatus);
		mParameters.put(KEY_RANDOM_CODE, randomCode);
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
			//TODO: parse the response
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
			mCallback.onFailed();
		}
	}

}
