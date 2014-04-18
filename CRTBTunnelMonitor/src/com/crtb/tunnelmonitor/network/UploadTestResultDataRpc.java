package com.crtb.tunnelmonitor.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.crtb.tunnelmonitor.common.Constant;

import ICT.utils.RSACoder;
import android.util.Log;

class UploadTestResultDataRpc extends AbstractRpc {
	private static final String LOG_TAG = "UploadTestResultDataRpc";
	private static final String KEY_SECTION_CODE = "断面编号";
	private static final String KEY_POINT_CODE_LIST = "测点的编号序列";
	private static final String KEY_TUNNEL_FACE_DISTANCE = "该断面到掌子面的距离";
	private static final String KEY_PROCEDURE = "当前开挖段施工工序";
	private static final String KEY_MONITOR_MODEL = "量测仪器及型号";
	private static final String KEY_MEASURE_DATE = "测点的量测时间";
	private static final String KEY_POINT_VALUE_LIST = "测点的量测值序列";
	private static final String KEY_POINT_COORDINATE_LIST = "测点的量测坐标序列";
	private static final String KEY_SURVEYOR_NAME = "量测人员姓名";
	private static final String KEY_SURVEYOR_ID = "量测人员身份证";
	private static final String KEY_REMARK = "备注";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getTestResultData";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	UploadTestResultDataRpc(long randomCode, Object testData, RpcCallback callback) {
		mParameters.put(KEY_SECTION_CODE, "XPCL01SD00010003");
		mParameters.put(KEY_POINT_CODE_LIST, "XPCL01SD00010003GD01/XPCL01SD00010003SL01#XPCL01SD00010003SL02/XPCL01SD00010003SL03#XPCL01SD00010003SL04");
		mParameters.put(KEY_TUNNEL_FACE_DISTANCE, 159.0f);
		mParameters.put(KEY_PROCEDURE, "02");
		mParameters.put(KEY_MONITOR_MODEL, "xxx");
		mParameters.put(KEY_MEASURE_DATE, new Date());
		mParameters.put(KEY_POINT_VALUE_LIST, "1/2/3");
		String coordinate = "100#200#300/100#200#300/100#200#300";
		String encncyptCoordinate = RSACoder.encnryptDes(coordinate, Constant.testDeskey);
		mParameters.put(KEY_POINT_COORDINATE_LIST, encncyptCoordinate);
		mParameters.put(KEY_SURVEYOR_NAME, "杨工");
		mParameters.put(KEY_SURVEYOR_ID, "102190198805012891");
		mParameters.put(KEY_REMARK, "hi");
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
