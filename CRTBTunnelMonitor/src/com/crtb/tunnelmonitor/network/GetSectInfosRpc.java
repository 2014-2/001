package com.crtb.tunnelmonitor.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.crtb.tunnelmonitor.common.Constant;

import android.util.Log;

class GetSectInfosRpc extends AbstractRpc {
	private static final String TAG = "GetSectInfosRpc：";
	private static final String KEY_SITE_CODE = "工点编号";
	private static final String KEY_SECTION_STATUS = "断面状态";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getSectInfos";

	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;

	GetSectInfosRpc(String siteCode, int sectionStatus, long randomCode,
			RpcCallback callback) {
		mParameters.put(KEY_SITE_CODE, siteCode);
		mParameters.put(KEY_SECTION_STATUS, sectionStatus);
		mParameters.put(KEY_RANDOM_CODE, randomCode);
		mCallback = callback;
	}

	@Override
	public SoapObject getRpcMessage(String namesapce) {
		SoapObject message = new SoapObject(namesapce, KEY_ACTION);
		for (String key : mParameters.keySet()) {
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
			notifyFailed("Unknown reponse type: "
					+ response.getClass().getName());
			return;
		}
		Log.d(Constant.LOG_TAG_SERVICE, TAG + "response: " + response);
		try {
			/** 
			 * getSectInfosResponse{
			 *	return=anyType{item=XPCL01SD00010005#DK0+160; 
			 *	item=XPCL01SD00010004#DK0+180; item=XPCL01SD00010003#DK0+185; 
			 *	item=XPCL01SD00010002#DK0+190; item=XPCL01SD00010001#DK0+195; }; } 
			 */
			SoapObject result = (SoapObject) response;
			SoapObject data = (SoapObject) result.getProperty(0);
			final int count = data.getPropertyCount();
			List<String> codeList = new ArrayList<String>();
			for(int i = 0; i < count; i++) {
				String[] sectionInfo = data.getPropertyAsString(i).split("#");
				codeList.add(sectionInfo[0]);
				Log.d(Constant.LOG_TAG_SERVICE, TAG + "section code: " + sectionInfo[0] + ", total: " + sectionInfo[1]);
			}
			if (codeList.size() > 0) {
				notifySuccess(codeList.toArray(new String[codeList.size()]));
			} else {
				notifyFailed("empty data");
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
