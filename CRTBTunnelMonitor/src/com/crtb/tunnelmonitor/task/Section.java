package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

import com.crtb.tunnelmonitor.entity.AlertInfo;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

/**
 * 断面
 * 
 * @author tim
 * 
 */
public abstract class Section {
	private TunnelCrossSectionIndex tunnel;
	private SubsidenceCrossSectionIndex sub;
	
	public TunnelCrossSectionIndex getTunnel() {
		return tunnel;
	}

	public void setTunnel(TunnelCrossSectionIndex tunnel) {
		this.tunnel = tunnel;
	}

	public SubsidenceCrossSectionIndex getSub() {
		return sub;
	}

	public void setSub(SubsidenceCrossSectionIndex sub) {
		this.sub = sub;
	}

	
	private int mRowId;
	private String mSectionCode;
	private List<MeasureData> mMeasureData;
	private List<AlertInfo> mAlertInfo;

	public List<AlertInfo> getmAlertInfo() {
		return mAlertInfo;
	}

	public void setmAlertInfo(List<AlertInfo> mAlertInfo) {
		this.mAlertInfo = mAlertInfo;
	}

	public void setSectionCode(String sectionCode) {
		mSectionCode = sectionCode;
	}

	public String getSectionCode() {
		return mSectionCode;
	}

	public void setMeasureData(List<MeasureData> measureDataList) {
		mMeasureData = measureDataList;
	}

	public void addMeasureData(List<MeasureData> measureDataList) {
		if (mMeasureData == null) {
			mMeasureData = new ArrayList<MeasureData>();
		}
		if (measureDataList != null && measureDataList.size() > 0) {
			mMeasureData.addAll(measureDataList);
		}
	}

	public List<MeasureData> getMeasureData() {
		return mMeasureData;
	}

	public boolean needUpload() {
		boolean result = false;
		if (mMeasureData != null && mMeasureData.size() > 0) {
			result = true;
		}
//		if (!isUpload()) {
//			result = true;
//		}
		return result;
	}

	protected abstract int getRowId();

	protected abstract boolean isUpload();
}
