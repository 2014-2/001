package com.crtb.tunnelmonitor.entity;

import java.io.Serializable;
import java.util.List;

import com.crtb.tunnelmonitor.activity.WarningUploadActivity.WarningUploadData;

public class WaringUploadDataContainer implements Serializable{

	public WaringUploadDataContainer(){
		
	}
	
	private static final long serialVersionUID = 1L;
	public static final String KEY = "WaringUploadDataContainer";
	
	private String curSectionGuid;
	public String getCurSectionGuid() {
		return curSectionGuid;
	}
	public void setCurSectionGuid(String curSectionGuid) {
		this.curSectionGuid = curSectionGuid;
	}
	public List<WarningUploadData> getWaringDataList() {
		return waringDataList;
	}
	public void setWaringDataList(List<WarningUploadData> waringDataList) {
		this.waringDataList = waringDataList;
	}
	private List<WarningUploadData> waringDataList;
}
