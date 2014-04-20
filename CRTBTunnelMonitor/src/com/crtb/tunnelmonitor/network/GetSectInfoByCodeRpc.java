package com.crtb.tunnelmonitor.network;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;
import org.zw.android.framework.util.DateUtils;

import android.util.Log;

import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

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
		String sectionCode = (String) mParameters.get(KEY_SECTION_CODE);
		Log.d(LOG_TAG, "response( " + sectionCode + " ): " + response);
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
			TunnelCrossSectionIndex section = new TunnelCrossSectionIndex();
			try {
				String name = data.getPropertyAsString(i++);
				section.setSectionName(name);
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String chainage = data.getPropertyAsString(i++);
				String[] chainageInfo = chainage.substring(2).split("\\+");
				final float total = Float.parseFloat(chainageInfo[0]) * 1000 + Float.parseFloat(chainageInfo[1]);
				section.setChainage(total);
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String digMehtod = data.getPropertyAsString(i++);
				section.setExcavateMethod(digMehtod);
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String width = data.getPropertyAsString(i++);
				section.setWidth(Float.parseFloat(width));
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String u0Value = data.getPropertyAsString(i++);
				section.setGDU0(Float.parseFloat(u0Value));
				section.setSLU0(Float.parseFloat(u0Value));
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String u0ModifiedTime = data.getPropertyAsString(i++);
				section.setGDU0Time(DateUtils.toDate(u0ModifiedTime));
				section.setSLU0Time(DateUtils.toDate(u0ModifiedTime));
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String u0Remark = data.getPropertyAsString(i++);
				section.setGDU0Description(u0Remark);
				section.setSLU0Description(u0Remark);
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String wallRockLevel = data.getPropertyAsString(i++);
				section.setROCKGRADE(wallRockLevel);
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String pointCodeList = data.getPropertyAsString(i++);
				section.setSurveyPntName(pointCodeList.replace("/", ","));
			} catch (NullPointerException e) {
				// ignore
			}
			try {
				String remark = data.getPropertyAsString(i++);
				section.setInfo(remark);
			} catch (NullPointerException e) {
				// ignore
			}
			notifySuccess(new TunnelCrossSectionIndex[] { section });
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
