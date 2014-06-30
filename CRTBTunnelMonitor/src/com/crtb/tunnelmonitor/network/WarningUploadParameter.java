package com.crtb.tunnelmonitor.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class WarningUploadParameter {
	private static final String KEY_SECTION_CODE = "断面编号";
	private static final String KEY_POINT_CODE = "测点编码或收敛测点编码对";
	private static final String KEY_WARNING_LEVEL = "预警级别";
	private static final String KEY_TRANSFORM_SPEED = "变形速度值";
	private static final String KEY_WARNING_POINT_VALUE = "预警测点的变形值";
	private static final String KEY_WARNING_TIME = "预警时间";
	private static final String KEY_WARNING_PERSON = "预警处理责任人";
	private static final String KEY_WARNING_DESCRIPTION = "预警处理过程描述";
	private static final String KEY_WARNING_END_TIME = "预警处理完成时间";
	private static final String KEY_WARNING_RESULT = "预警处理结果";
	private static final String KEY_REMARK = "备注";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	
	public void setSectionCode(String sectionCode) {
		mParameters.put(KEY_SECTION_CODE, sectionCode);
	}
	
	public void setPointCode(String pointCode) {
		mParameters.put(KEY_POINT_CODE, pointCode);
	}
	
	public void setWarningLevel(int level) {
		mParameters.put(KEY_WARNING_LEVEL, level);
	}
	
	public void setTransformSpeed(float speed) {
		mParameters.put(KEY_TRANSFORM_SPEED, speed);
	}
	
	public void setWarningPointValue(float pointValue) {
		mParameters.put(KEY_WARNING_POINT_VALUE, pointValue);
	}
	
	public void setWarningDate(Date warningDate) {
		mParameters.put(KEY_WARNING_TIME, warningDate);
	}
	
	public void setWarningPerson(String person) {
		mParameters.put(KEY_WARNING_PERSON, person);
	}
	
	public void setWarningDescription(String description) {
		mParameters.put(KEY_WARNING_DESCRIPTION, description);
	}
	
	public void setWarningEndTime(Date endTime) {
		mParameters.put(KEY_WARNING_END_TIME, endTime);
	}
	
	public void setWarningResult(int result) {
		mParameters.put(KEY_WARNING_RESULT, result);
	}
	
	public void setRemark(String remark) {
		mParameters.put(KEY_REMARK, remark);
	}
	
	public Map<String, Object> getParameters() {
		return mParameters;
	}

}
