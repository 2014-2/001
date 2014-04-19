package com.crtb.tunnelmonitor.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class PointUploadParameter {
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
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	
	public void setSectionCode(String sectionCode) {
		mParameters.put(KEY_SECTION_CODE, sectionCode);
	}
	
	public void setPointCodeList(String pointCodeList) {
		mParameters.put(KEY_POINT_CODE_LIST, pointCodeList);
	}
	
	public void setTunnelFaceDistance(float distance) {
		mParameters.put(KEY_TUNNEL_FACE_DISTANCE, distance);
	}
	
	public void setProcedure(String procedure) {
		mParameters.put(KEY_PROCEDURE, procedure);
	}
	
	public void setMonitorModel(String model) {
		mParameters.put(KEY_MONITOR_MODEL, model);
	}
	
	public void setMeasureDate(Date measureDate) {
		mParameters.put(KEY_MEASURE_DATE, measureDate);
	}

	public void setPointValueList(String valueList) {
		mParameters.put(KEY_POINT_VALUE_LIST, valueList);
	}
	
	public void setPointCoordinateList(String coordinateList) {
		mParameters.put(KEY_POINT_COORDINATE_LIST, coordinateList);
	}
	
	public void setSurveyorName(String name) {
		mParameters.put(KEY_SURVEYOR_NAME, name);
	}
	
	public void setSurveyorId(String id) {
		mParameters.put(KEY_SURVEYOR_ID, id);
	}
	
	public void setRemark(String remark) {
		mParameters.put(KEY_REMARK, remark);
	}

	public Map<String, Object> getParameters() {
		return mParameters;
	}
}
