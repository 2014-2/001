package com.crtb.tunnelmonitor.service;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

class GetSectInfoByCodeRpc extends AbstractRpc {
	private static final String LOG_TAG = "GetSectInfoByCodeRpc";
	private static final String KEY_SECTION_CODE = "断面编号";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getSectInfoByCode";

	private Map<String, Object> mParameters = new HashMap<String, Object>();
	private RpcCallback mCallback;
	
	GetSectInfoByCodeRpc(String sectionCode, long randomCode, RpcCallback callback) {
		mParameters.put(KEY_SECTION_CODE, sectionCode);
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
			 * getSectInfoByCodeResponse{return=anyType
			 * {
			 *	item=DK0+195; 断面名称
			 *	item=DK0+195; 断面里程
			 *	item=02; 　　　断面开挖方法类型编码
			 *	item=7.5; 　　断面宽度
			 *	item=50.0; 　　U0值
			 *	item=2014-01-03 11:41:14.0; U0值修改时间
			 *	item=null; 　　U0值的备注
			 *	item=null; 　　围岩级别
			 *	item=XPCL01SD00010001GD01/XPCL01SD00010001SL01#XPCL01SD00010001SL02/XPCL01SD00010001SL03#XPCL01SD00010001SL04;　测点的编码序列 
			 *	item= ; 　　　　备注
			 *	item=null; 
			 *	item=null; }; }
			 */
			SoapObject result = (SoapObject) response;
			SoapObject data = (SoapObject) result.getProperty(0);
			int i = 0;
			String name = "";
			try {
				name = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String chainage = "";
			try {
				chainage = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String digMehtod = "";
			try {
				digMehtod = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String width = "";
			try {
				width = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String u0Value = "";
			try {
				u0Value = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String u0ModifiedTime = "";
			try {
				u0ModifiedTime = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String u0Remark = "";
			try {
				u0Remark = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String wallRockLevel = "";
			try {
				wallRockLevel = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String pointCodeList = "";
			try {
				pointCodeList = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			String remark = "";
			try {
				remark = data.getPropertyAsString(i++);
			} catch (NullPointerException e) {
				// ignore
			}
			Log.d(LOG_TAG, "name: " + name + ", chainage: " + chainage + ", digMehtod: " + digMehtod + ", width: " + width + ", u0Value: " + u0Value + ", u0ModifiedTime: " + u0ModifiedTime + ", u0Remark: " + u0Remark + ", wallRockLevel: " + wallRockLevel + ", pointCodeList: " + pointCodeList + ", remark: " + remark);
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
