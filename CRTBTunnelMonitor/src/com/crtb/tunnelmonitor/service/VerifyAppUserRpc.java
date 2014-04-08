package com.crtb.tunnelmonitor.service;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.text.TextUtils;
import android.util.Log;

class VerifyAppUserRpc extends AbstractRpc {
	private static final String LOG_TAG = "VerifyAppUserRpc";
	private static final String KEY_ACCOUNT = "登陆账号";
	private static final String KEY_PASSWORD = "登陆密码";
	private static final String KEY_MAC_ADDRESS = "设备物理地址";
	private static final String KEY_PUBLIC_KEY = "加密后密钥";
	private static final String KEY_ACTION = "verifyAppUser";
	
	private Map<String, String> mParameters = new HashMap<String, String>();
	private RpcCallback mCallback;
	
	VerifyAppUserRpc(String account, String password, String macAddress, String publicKey, RpcCallback callback) {
		mParameters.put(KEY_ACCOUNT, account);
		mParameters.put(KEY_PASSWORD, password);
		mParameters.put(KEY_MAC_ADDRESS, macAddress);
		mParameters.put(KEY_PUBLIC_KEY, publicKey);
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
			String randomCode = result.getPropertyAsString(0);
			if (TextUtils.isEmpty(randomCode)) {
				notifyFailed("Invalid random code");
			} else {
				notifySuccess(randomCode);
			}
		} catch (Exception e) {
			notifyFailed("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void notifySuccess(String randomCode) {
		if (mCallback != null) {
			mCallback.onSuccess(new String[] { randomCode });
		}
	}
	
	private void notifyFailed(String reason) {
		if (mCallback != null) {
			mCallback.onFailed();
		}
	}

}
