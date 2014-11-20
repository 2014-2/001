package com.crtb.tunnelmonitor.network;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.task.WorkSite;
import com.crtb.tunnelmonitor.task.WorkZone;

class GetZoneAndSiteCodeRpc extends AbstractRpc {
	private static final String TAG = "GetZoneAndSiteCodeRpc：";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getZoneAndSiteCode";
	
	private Map<String, Long> mParameters = new HashMap<String, Long>();
	private RpcCallback mCallback;
	
	GetZoneAndSiteCodeRpc(long randomCode, RpcCallback callback) {
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
		Log.d(Constant.LOG_TAG_SERVICE, TAG + "response: " + response);
		try {
			//getZoneAndSiteCodeResponse{return=anyType{item=XPCL01SG05GQ01#一工区; item=XPCL01SD0001#跃龙门隧道; }; }
			SoapObject result = (SoapObject) response;
			SoapObject data = (SoapObject) result.getProperty(0);
			WorkZone workZone = new WorkZone();
			final int totalCount = data.getPropertyCount();
			if (totalCount > 0) {
				int markerIndex = 0;
				String info = null;
				String code = null;
				String name = null;
				info = data.getPropertyAsString(0);
				markerIndex = info.indexOf("#");
				if (markerIndex < 1) {
					notifyFailed("Exception Data: no # " + info);
					return;
				}
				code = info.substring(0, markerIndex);
				name = info.substring(markerIndex + 1);
				workZone.setZoneCode(code);
				workZone.setZoneName(name);
				
				for(int i = 1; i < totalCount; i++) {
					WorkSite workSite = new WorkSite();
					info = data.getPropertyAsString(i);
					markerIndex = info.indexOf("#");
					if (markerIndex < 1) {
						notifyFailed("Exception Data: no # " + info);
						return;
					}
					code = info.substring(0, markerIndex);
					name = info.substring(markerIndex + 1);
					workSite.setSiteCode(code);
					workSite.setSiteName(name);
					workZone.addWorkSite(workSite);
				}
			}
			notifySuccess(new WorkZone[]{workZone});
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
