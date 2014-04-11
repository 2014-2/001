package com.crtb.tunnelmonitor.service;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.crtb.tunnelmonitor.common.Constant;

import ICT.utils.RSACoder;
import android.util.Log;

class GetMonitorValueInfoRpc extends AbstractRpc {
	private static final String LOG_TAG = "GetMonitorValueInfoRpc";
	private static final String KEY_POINT_CODE = "测点编码";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getMonitorValueInfo";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	//point code: XPCL01SD00010001SL01#XPCL01SD00010001SL02
	GetMonitorValueInfoRpc(String pointCode, long randomCode, RpcCallback callback) {
		mParameters.put(KEY_POINT_CODE, pointCode);
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
			/**
			 * getMonitorValueInfoResponse{return=anyType{ item=
			 * 6.6701/u3lcHq4AGrpbJ3OUATdW0ETYULVy2qhZjNDijM5hZh112cDSGKzw2fnkkMOHcjMqcZm4cT2cIPS8/
			 * TUvYb3igAVZoKTONlH9/2014-01-03 11:48:36.0/瑞/19/nullnull;
			 */
			Log.d(LOG_TAG, "response: " + response);
			SoapObject result = (SoapObject) response;
			SoapObject data = (SoapObject) result.getProperty(0);
			final int count = data.getPropertyCount();
			for(int i = 0; i < count; i++) {
				String[] pointInfo = data.getPropertyAsString(i).split("/");
				String value = pointInfo[0];
				String xyz = RSACoder.decnryptDes(Constant.testDeskey, pointInfo[1]);
				String deformation = RSACoder.decnryptDes(Constant.testDeskey, pointInfo[2]);
				String time = pointInfo[3];
				String name = pointInfo[4];
				String distance = pointInfo[5];
				String procedure = pointInfo[6];
				Log.d(LOG_TAG, "xyz: " + xyz);
			}
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
