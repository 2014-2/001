package com.crtb.tunnelmonitor.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class SectionUploadParamter {
	private static final String KEY_SECTION_NAME = "断面名称";
	private static final String KEY_SECTION_CODE = "断面编码";
	private static final String KEY_SECTION_CHAINAGE = "断面里程";
	private static final String KEY_SECTION_DIG_METHOD = "断面开挖方法类型编码";
	private static final String KEY_SECTION_WIDTH = "断面宽度";
	private static final String KEY_TOTAL_U0_LIMIT = "极限累计位移U0值";
	private static final String KEY_U0_MODIFIED_TIME = "U0值修改时间";
	private static final String KEY_U0_REMARK = "U0值的备注";
	private static final String KEY_WALL_ROCK_LEVEL = "围岩级别";
	private static final String KEY_POINTS_LIST = "测点的编号序列";
	private static final String KEY_FIRST_MEASURE_DATE = "首次测量日期";
	private static final String KEY_REMARK = "备注";
	
	private Map<String, Object> mParameters = new HashMap<String, Object>();
	
	public void setSectionName(String sectionName) {
		mParameters.put(KEY_SECTION_NAME, sectionName);
	}
	
	public void setSectioCode(String sectionCode) {
		mParameters.put(KEY_SECTION_CODE, sectionCode);
	}
	
	public void setChainage(String chainage) {
		mParameters.put(KEY_SECTION_CHAINAGE, chainage);
	}

	public void setDigMethod(String digMethod) {
		mParameters.put(KEY_SECTION_DIG_METHOD, digMethod);
	}
	
	public void setWidth(float width) {
		mParameters.put(KEY_SECTION_WIDTH, width);
	}
	
	public void setTotalU0Limit(float limit) {
		mParameters.put(KEY_TOTAL_U0_LIMIT, limit);
	}
	
	public void setModifiedTime(Date time) {
		mParameters.put(KEY_U0_MODIFIED_TIME, time);
	}
	
	public void setU0Remark(String remark) {
		mParameters.put(KEY_U0_REMARK, remark);
	}
	
	public void setWallRockLevel(int level) {
		mParameters.put(KEY_WALL_ROCK_LEVEL, level);
	}
	
	public void setPointList(String pointList) {
		mParameters.put(KEY_POINTS_LIST, pointList);
	}
	
	public void setFirstMeasureDate(Date firstDate) {
		mParameters.put(KEY_FIRST_MEASURE_DATE, firstDate);
	}
	
	public void setRemark(String remark) {
		mParameters.put(KEY_REMARK, remark);
	}
	
	public Map<String, Object> getParameters() {
		return mParameters;
	}
}
