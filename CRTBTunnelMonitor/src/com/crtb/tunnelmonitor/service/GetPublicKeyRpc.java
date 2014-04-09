package com.crtb.tunnelmonitor.service;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.text.TextUtils;
import android.util.Log;

class GetPublicKeyRpc extends AbstractRpc {
	private static final String LOG_TAG = "GetPublicKeyRpc";
	private static final String KEY_ACCOUNT = "登陆账号";
	private static final String KEY_MAC_ADDRESS = "设备物理地址";
	private static final String KEY_ACTION = "getPublicKey";
	
	private Map<String, String> mParameters = new HashMap<String, String>();
	private RpcCallback mCallback;
	
	GetPublicKeyRpc(String account, String macAddress, RpcCallback callback) {
		mParameters.put(KEY_ACCOUNT, account);
		mParameters.put(KEY_MAC_ADDRESS, macAddress);
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
			String publicKey = result.getPropertyAsString(0);
			if (TextUtils.isEmpty(publicKey)) {
				notifyFailed("Invalid public key");
			} else {
				notifySuccess(publicKey);
			}
		} catch (Exception e) {
			notifyFailed("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void notifySuccess(String publicKey) {
		if (mCallback != null) {
			mCallback.onSuccess(new String[] { publicKey });
		}
	}
	
	private void notifyFailed(String reason) {
		if (mCallback != null) {
			mCallback.onFailed(reason);
		}
	}
}
