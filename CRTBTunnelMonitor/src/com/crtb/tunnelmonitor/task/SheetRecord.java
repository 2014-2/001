package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.entity.RawSheetIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.utils.CrtbUtils;

/**
 * 记录单(隧道内断面/地表下沉断面)
 *
 */
public class SheetRecord { 
	private RawSheetIndex mRawSheet;
    private List<Section> mUnUploadSections;
    private boolean mIsChecked;

    
    public SheetRecord() {
        mUnUploadSections = new ArrayList<Section>();
    }
    
    public void setRawSheet(RawSheetIndex rawSheet) {
        mRawSheet = rawSheet;
    }

    public RawSheetIndex getRawSheet() {
        return mRawSheet;
    }

    public void setUnUpLoadSection(List<Section> unUploadSections) {
        mUnUploadSections = unUploadSections;
    }

    public List<Section> getUnUploadSections() {
        return mUnUploadSections;
    }

    public boolean needUpload() {
        boolean result = false;
        if (mUnUploadSections != null && mUnUploadSections.size() > 0) {
            result = true;
        }
        return result;
    }
    
    public void setChecked(boolean flag) {
    	mIsChecked = flag;
    }
    
    public boolean isChecked() {
    	return mIsChecked;
    }
    
    public String getCreatedTime() {
        return CrtbUtils.formatDate(mRawSheet.getCreateTime());
    }
    
    public boolean isUploaded() {
        return !needUpload();
    }

}
