package com.crtb.tunnelmonitor.task;

import java.util.List;

/**
 * 断面
 * 
 * @author tim
 *
 */
public abstract class Section {
    private String mSectionCode;
    private List<MeasureData> mMeasureData;

    public void setSectionCode(String sectionCode) {
    	mSectionCode = sectionCode;
    }
    
    public String getSectionCode() {
    	return mSectionCode;
    }
    
    public void setMeasureData(List<MeasureData> measureDataList) {
    	mMeasureData = measureDataList;
    }

    public List<MeasureData> getMeasureData() {
        return mMeasureData;
    }

    public boolean needUpload() {
        boolean result = false;
        if (!isUpload()) {
        	result = true;
        }
        if (mMeasureData != null && mMeasureData.size() > 0) {
            result = true;
        }
        return result;
    }
    
    protected abstract boolean isUpload();
}
