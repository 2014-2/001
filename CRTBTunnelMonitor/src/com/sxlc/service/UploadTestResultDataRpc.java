package com.sxlc.service;

import org.ksoap2.serialization.SoapObject;

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
	
	
	@Override
	public SoapObject getRpcMessage(String namesapce) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onResponse(Object response) {
		// TODO Auto-generated method stub
		
	}


}
