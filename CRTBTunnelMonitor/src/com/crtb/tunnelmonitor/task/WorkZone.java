package com.crtb.tunnelmonitor.task;

import java.util.ArrayList;
import java.util.List;

public class WorkZone {
	private String zoneCode;
	private String zoneName;
	private List<WorkSite> workSites = new ArrayList<WorkSite>();
	
	public String getZoneCode() {
		return zoneCode;
	}
	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public List<WorkSite> getWorkSites() {
		return workSites;
	}
	
	public void addWorkSite(WorkSite workSite) {
		workSites.add(workSite);
	}
}
