package com.crtb.tunnelmonitor.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

class UploadSectionPointInfoRpc extends AbstractRpc {
	private static final String LOG_TAG = "UploadSectionPointInfoRpc";
	private static final String KEY_ZONE_CODE = "工区编号";
	private static final String KEY_SITE_CODE = "隧道工点编号";
	private static final String KEY_SECTION_NAME = "断面名称";
	private static final String KEY_SECTION_CODE = "断面编码";
	private static final String KEY_SECTION_CHAINAGE = "断面里程";
	private static final String KEY_SECTION_DIG_METHOD = "断面开挖方法类型编码";
	private static final String KEY_SECTION_WIDTH = "断面宽度";
	private static final String KEY_TOTAL_U0_LIMIT = "极限累计位移U0值";
	private static final String KEY_U0_MODIFIED_TIME = "U0值修改时间";
	private static final String KEY_U0_REMARK = "U0值的备注";
	private static final String KEY_WALL_ROCK_LEVEL = "围岩级别";
	private static final String KEY_POINTS_LIST = "测点的编号序列";
	private static final String KEY_FIRST_MEASURE_DATE = "首次测量日期";
	private static final String KEY_REMARK = "备注";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getSectionPointInfo";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	UploadSectionPointInfoRpc(String zoneCode, String siteCode, long randomCode, SectionUploadParamter parameter, RpcCallback callback) {
		mParameters.put(KEY_ZONE_CODE, zoneCode);
		mParameters.put(KEY_SITE_CODE, siteCode);
		if (parameter == null) {
			mParameters.put(KEY_SECTION_NAME, "DK0+150");
			mParameters.put(KEY_SECTION_CODE, "XPCL01SD00010008");
			mParameters.put(KEY_SECTION_CHAINAGE, "DK0+150");
			mParameters.put(KEY_SECTION_DIG_METHOD, "DT");
			mParameters.put(KEY_SECTION_WIDTH, 7.5f);
			mParameters.put(KEY_TOTAL_U0_LIMIT, 50.0f);
			mParameters.put(KEY_U0_MODIFIED_TIME, new Date());
			mParameters.put(KEY_U0_REMARK, "xxx");
			mParameters.put(KEY_WALL_ROCK_LEVEL, 1);
			mParameters.put(KEY_POINTS_LIST, "XPCL01SD00010008GD01/XPCL01SD00010008SL01#XPCL01SD00010008SL02/XPCL01SD00010008SL03#XPCL01SD00010008SL04");
			mParameters.put(KEY_FIRST_MEASURE_DATE, new Date());
			mParameters.put(KEY_REMARK, "yyy");
		} else {
			Map<String, Object> parameterMap = parameter.getParameters();
			for(String key : parameterMap.keySet()) {
				mParameters.put(key, parameterMap.get(key));
			}
		}
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
				notifyFailed("upload section data failed: code = " + code);
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
