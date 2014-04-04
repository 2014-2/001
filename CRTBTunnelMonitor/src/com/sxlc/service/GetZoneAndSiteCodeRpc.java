package com.sxlc.service;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.os.Message;
import android.util.Log;

public class GetZoneAndSiteCodeRpc extends AbstractRpc {
	private static final String LOG_TAG = "GetZoneAndSiteCodeRpc";
	private static final String KEY_NAME = "uname";
	private static final String KEY_PASSWORD = "upassword";
	private static final String KEY_ACTION = "getZoneAndSiteCode";
	private static final String KEY_RESULT = "getZoneAndSiteCodeResult";
	
	private Map<String, String> mParameters = new HashMap<String, String>();
	private RpcCallback mCallback;
	
	public GetZoneAndSiteCodeRpc(String userName, String password, RpcCallback callback) {
		mParameters.put(KEY_NAME, userName);
		mParameters.put(KEY_PASSWORD, password);
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
		Log.d(LOG_TAG, "response: " + response);
		try {
			SoapObject result = (SoapObject) response;
			Object data = result.getProperty(KEY_RESULT);
			if (data == null) {
				notifyFailed("Result data is NULL.");
				return;
			}
			//XKSJ01BD03SD01#第三工区/XKSJ01SD0001#某某隧道名称
			if ("getZoneAndSiteCodeResponse".equals(result.getName())) {
				final String[] temp = ((SoapObject)data).getPropertyAsString(0).split("/");
				final String zone_code = temp[0].split("#")[0];
				final String site_code = temp[1].split("#")[0];
				Message msg = Message.obtain();
				//msg.what = IWebService.MSG_GET_ZONE_AND_SITE_CODE_DONE;
				msg.obj = zone_code + "," + site_code;
				notifySuccess();
				//mUiHandler.sendMessage(msg);
				//Log.d(TAG, "zone_code: " + zone_code + ", " + "site_code: " + site_code);
			} 
		} catch (Exception e) {
			notifyFailed("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void notifySuccess() {
		if (mCallback != null) {
			mCallback.onSuccess();
		}
	}
	
	private void notifyFailed(String reason) {
		if (mCallback != null) {
			mCallback.onFailed();
		}
	}
}
