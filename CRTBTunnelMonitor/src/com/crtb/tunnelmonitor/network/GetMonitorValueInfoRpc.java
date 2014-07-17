package com.crtb.tunnelmonitor.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import ICT.utils.RSACoder;
import android.text.TextUtils;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

class GetMonitorValueInfoRpc extends AbstractRpc {
	private static final String LOG_TAG = "GetMonitorValueInfoRpc";
	private static final String KEY_POINT_CODE = "测点编码";
	private static final String KEY_RANDOM_CODE = "随机码";
	private static final String KEY_ACTION = "getMonitorValueInfo";
	private static final int DATA_FILED_NUM = 7;
	
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
			 * 瑞=测量人员，19=身份证id，null=到掌子面距离，null=施工工序
			 */
			String pointCode = (String) mParameters.get(KEY_POINT_CODE);
			Log.d(LOG_TAG, "response(pointCode " + pointCode + "): " + response);
			SoapObject result = (SoapObject) response;
			SoapObject data = (SoapObject) result.getProperty(0);
			final int count = data.getPropertyCount();
			List<TunnelSettlementTotalData> pointTestDataList = new ArrayList<TunnelSettlementTotalData>(); 
			for(int i = 0; i < count; i++) {
				String[] pointInfo = data.getPropertyAsString(i).split("/");
				int index = 0;
				String value = pointInfo[index++];
				String encryptCoordinate = pointInfo[index++];
				final int num = pointInfo.length - DATA_FILED_NUM;
				if (num > 0) {
					for(int k = 0 ; k < num; k++) {
						encryptCoordinate += "/" + pointInfo[index++];
					}
				}
				String coordinate = RSACoder.decnryptDes(encryptCoordinate, Constant.testDeskey);
				String time = pointInfo[index++];
				String surveyorName = pointInfo[index++];
				long surveyorId = Long.parseLong(pointInfo[index++]);
				Log.d(LOG_TAG, "test data: " + coordinate + "pointinfo: " + data.getPropertyAsString(i));
				if (coordinate.contains("#")) {
					String[] coordinateInfo = coordinate.split("#");
					switch (coordinateInfo.length) {
					case 3:
						TunnelSettlementTotalData pointTestData = new TunnelSettlementTotalData();
						Log.d(LOG_TAG, "coordinate： " + coordinate);
						//TODO: 解析剩下的数据
						pointTestData.setCoordinate(coordinate.replace("#", ","));
						pointTestData.setSurveyTime(CrtbUtils.parseDate(time));
						//将测量点数据标记已上传
//						pointTestData.setInfo("2");
						pointTestData.setUploadStatus(2); //表示该测点已上传
						pointTestData.setPntType("A");
						//TODO: 无法获取此数据，暂时用0代替
						pointTestData.setSurveyorID(String.valueOf(0));
						pointTestDataList.add(pointTestData);
						break;
					case 6:
						TunnelSettlementTotalData pointTestData1 = new TunnelSettlementTotalData();
						String coordinate1 = coordinateInfo[0] + "," + coordinateInfo[1] + "," + coordinateInfo[2];
						String coordinate2 = coordinateInfo[3] + "," + coordinateInfo[4] + "," + coordinateInfo[5];
						Log.d(LOG_TAG, "coordinate： " + coordinate);
						//TODO: 解析剩下的数据
						pointTestData1.setCoordinate(coordinate1);
						pointTestData1.setSurveyTime(CrtbUtils.parseDate(time));
						//将测量点数据标记已上传
//						pointTestData1.setInfo("2");
						pointTestData1.setPntType("S1-1");
						//TODO: 无法获取此数据，暂时用0代替
						pointTestData1.setSurveyorID(String.valueOf(0));
						pointTestDataList.add(pointTestData1);

						TunnelSettlementTotalData pointTestData2 = new TunnelSettlementTotalData();
						Log.d(LOG_TAG, "coordinate： " + coordinate);
						//TODO: 解析剩下的数据
						pointTestData2.setCoordinate(coordinate2);
						pointTestData2.setSurveyTime(CrtbUtils.parseDate(time));
						//将测量点数据标记已上传
//						pointTestData2.setInfo("2");
						pointTestData2.setPntType("S1-2");
						//TODO: 无法获取此数据，暂时用0代替
						pointTestData2.setSurveyorID(String.valueOf(0));
						pointTestDataList.add(pointTestData2);
						break;
					default:
						Log.e(LOG_TAG, "exception coordinate: " + coordinate);
						break;
					}
				} else {
					Log.e(LOG_TAG, "exception data： " + coordinate);
				}
			}
			final int dataCount = pointTestDataList.size();
			if (dataCount > 0) {
				notifySuccess(pointTestDataList.toArray(new TunnelSettlementTotalData[dataCount]));
			} else {
				notifySuccess(null);
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
