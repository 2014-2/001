package com.crtb.tunnelmonitor.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.crtb.tunnelmonitor.entity.SurveyerInformation;

import android.util.Log;
import android.widget.ArrayAdapter;

class GetSurveyorsRpc extends AbstractRpc {
	private static final String LOG_TAG = "GetSurveyorsRpc";
	private static final String KEY_ZONE_CODE = "工区编号";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getSurveyors";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	GetSurveyorsRpc(String zoneCode, long randomCode, RpcCallback callback) {
		mParameters.put(KEY_ZONE_CODE, zoneCode);
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
		Log.d(LOG_TAG, "response: " + response);
		try {
			//zone_code: 
			//getSurveyorsResponse{return=anyType{item=杨工#102190198805012891; item=石工#310501199001021657; }; }
			SoapObject result = (SoapObject) response;
			SoapObject data = (SoapObject) result.getProperty(0);
			final int count = data.getPropertyCount();
		    SurveyerInformation[] surveyors = new SurveyerInformation[count];
			for(int i = 0 ; i < count; i++) {
				String[] surveyorInfo = data.getPropertyAsString(i).split("#");
				SurveyerInformation surveyor = new SurveyerInformation();
				surveyor.setSurveyerName(surveyorInfo[0]);
				surveyor.setCertificateID(surveyorInfo[1]);
				surveyors[i] = surveyor;
			}
			notifySuccess(surveyors);
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
