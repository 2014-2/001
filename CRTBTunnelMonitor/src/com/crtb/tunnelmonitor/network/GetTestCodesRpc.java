package com.crtb.tunnelmonitor.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.crtb.tunnelmonitor.common.Constant;

import android.util.Log;

class GetTestCodesRpc extends AbstractRpc {
	private static final String TAG = "GetTestCodesRpc：";
	private static final String KEY_SECTION_CODE = "断面编号";
	private static final String KEY_POINT_STATUS = "测点状态";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getTestCodes";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	GetTestCodesRpc(String sectionCode, int pointStatus, long randomCode, RpcCallback callback) {
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
			/**
			 * getTestCodesResponse{return=anyType{
			 * item=XPCL01SD00010001GD01#GD01; item=XPCL01SD00010001SL01#SL01; 
			 * item=XPCL01SD00010001SL02#SL02; item=XPCL01SD00010001SL03#SL03; 
			 * item=XPCL01SD00010001SL04#SL04; }; }
			 */
			SoapObject result = (SoapObject) response;
			SoapObject data = (SoapObject) result.getProperty(0);
			final int count = data.getPropertyCount();
			List<String> pointList = new ArrayList<String>();
			for(int i = 0; i < count; i++) {
				String[] pointInfo =  data.getPropertyAsString(i).split("#");
				pointList.add(pointInfo[0]);
				Log.d(Constant.LOG_TAG_SERVICE, TAG + "test point: " + pointInfo[0]);
			}
			if (pointList.size() > 0) {
				notifySuccess(pointList.toArray(new String[pointList.size()]));
			} else {
				notifyFailed("empty point");
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
