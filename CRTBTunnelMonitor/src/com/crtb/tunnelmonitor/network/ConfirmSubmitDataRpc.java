package com.crtb.tunnelmonitor.network;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.crtb.tunnelmonitor.common.Constant;

import android.util.Log;

class ConfirmSubmitDataRpc extends AbstractRpc {
	private static final String TAG = "ConfirmSubmitDataRpc：";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "confirmSubmitData";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	ConfirmSubmitDataRpc(long randomCode, RpcCallback callback) {
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
			Log.d(Constant.LOG_TAG_SERVICE,TAG+"response: " + response);
			SoapObject result = (SoapObject) response;
			int code = Integer.parseInt(result.getPropertyAsString(0));
			if (code == 1) {
				notifySuccess(null);
			} else {
				notifyFailed("confirm test data failed: " + code);
			}
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
