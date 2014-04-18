package com.crtb.tunnelmonitor.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

class UploadWarningDataRpc extends AbstractRpc {
	private static final String LOG_TAG = "UploadWarningDataRpc";
	private static final String KEY_SECTION_CODE = "断面编号";
	private static final String KEY_POINT_CODE = "测点编码或收敛测点编码对";
	private static final String KEY_WARNING_LEVEL = "预警级别";
	private static final String KEY_TRANSFORM_SPEED = "变形速度值";
	private static final String KEY_WARNING_POINT_VALUE = "预警测点的量测值";
	private static final String KEY_WARNING_TIME = "预警时间";
	private static final String KEY_WARNING_PERSON = "预警处理责任人";
	private static final String KEY_WARNING_DESCRIPTION = "预警处理过程描述";
	private static final String KEY_WARNING_END_TIME = "预警处理完成时间";
	private static final String KEY_WARNING_RESULT = "预警处理结果";
	private static final String KEY_REMARK = "备注";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getWarningData";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	UploadWarningDataRpc(long randomCode, RpcCallback callback) {
		mParameters.put(KEY_SECTION_CODE, "XPCL01SD00010001");
		mParameters.put(KEY_POINT_CODE, "XPCL01SD00010001GD01");
		mParameters.put(KEY_WARNING_LEVEL, 1);
		mParameters.put(KEY_TRANSFORM_SPEED, 6.0f);
		mParameters.put(KEY_WARNING_POINT_VALUE, 1.0f);
		mParameters.put(KEY_WARNING_TIME, new Date());
		mParameters.put(KEY_WARNING_PERSON, "杨工");
		mParameters.put(KEY_WARNING_DESCRIPTION, "abc");
		mParameters.put(KEY_WARNING_END_TIME, new Date());
		mParameters.put(KEY_WARNING_RESULT, 0);
		mParameters.put(KEY_REMARK, "kkk");
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
			SoapObject result = (SoapObject) response;
			int code = Integer.parseInt(result.getPropertyAsString(0));
			if (code == 1) {
				notifySuccess(null);
			} else {
				notifyFailed("upload failed: code = " + code);
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
