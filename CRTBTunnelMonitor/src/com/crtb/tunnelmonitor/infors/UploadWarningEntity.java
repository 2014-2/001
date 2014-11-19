package com.crtb.tunnelmonitor.infors;

import java.util.ArrayList;

import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.MergedAlert;
import com.crtb.tunnelmonitor.utils.SectionInterActionManager;

/**
 * 预警上传信息
 * @author xu
 *
 */
public class UploadWarningEntity {
	private AlertInfo mLeijiAlert;
	private AlertInfo mSulvAlert;
	private String mSectionCode;
	    	
	public ArrayList<AlertInfo> getAlertInfos() {
	    ArrayList<AlertInfo> as = new ArrayList<AlertInfo>();
	    as.add(mLeijiAlert);
	    as.add(mSulvAlert);
	    return as;
	}

	public AlertInfo getAlertInfo() {
	    if (mLeijiAlert != null) {
	        return mLeijiAlert;
	    } else {
	        return mSulvAlert;
	    }
	}

	public void setLeijiAlert(AlertInfo alertInfo) {
		mLeijiAlert = alertInfo;
	}
	
	public AlertInfo getLeijiAlert() {
		return mLeijiAlert;
	}
	
	public void setSectionCode(String sectionCode) {
		mSectionCode = sectionCode;
	}
	
	public String getSectionCode() {
		return mSectionCode;
	}
	
	public AlertInfo getSulvAlert() {
        return mSulvAlert;
    }

    public void setSulvAlert(AlertInfo sulvAlert) {
        mSulvAlert = sulvAlert;
    }

    public MergedAlert getMergedAlert() {
        MergedAlert ma = new MergedAlert();
        ma.setLeijiAlert(mLeijiAlert);
        ma.setSulvAlert(mSulvAlert);
        return ma;
    }

    
    public String getPointCode() {
    	//YX  根据开挖方法，获取点或线对应的测点上传序列    	
    	AlertInfo ai = getAlertInfo();
    	return SectionInterActionManager.getOneLineDetailsByPointType(ai.getSECTCODE(),ai.getPntType());
	}
}

