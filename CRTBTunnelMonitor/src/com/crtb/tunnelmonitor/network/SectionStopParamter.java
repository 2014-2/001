package com.crtb.tunnelmonitor.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

public final class SectionStopParamter {

	
	private static final String KEY_WORK_AREA_CODE = "工区编号";
	private static final String KEY_WORK_SITE_CODE = "隧道工点编号";
	private static final String KEY_STATE     = "断面或测点状态";
	private static final String KEY_SECTION_CODE   = "断面编码或测点编码";
	private static final String KEY_REMARK    = "备注";
	private static final String KEY_RANDOM    = "随机码";
	
    private Map<String, Object> mParameters = new HashMap<String, Object>();
    
	public Map<String, Object> getParameters() {
		return mParameters;
	}
	
	public void setWorkAreaCode(String workAreaCode) {
		mParameters.put(KEY_WORK_AREA_CODE,workAreaCode);
	}

	public void setWorkSiteCode(String workSiteCode) {
		mParameters.put(KEY_WORK_SITE_CODE,workSiteCode);
	}

	public void setSectionState(String sectionState) {
		mParameters.put(KEY_STATE,sectionState);
	}

	public void setSectionCode(String sectionCode) {
		mParameters.put(KEY_SECTION_CODE, sectionCode);
	}

	public void setRemark(String remark) {
		mParameters.put(KEY_REMARK,remark);
	}

	public void setRandom(String random) {
		mParameters.put(KEY_RANDOM, random);
	}	
}
