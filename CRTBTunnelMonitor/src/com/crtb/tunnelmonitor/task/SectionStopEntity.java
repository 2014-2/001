package com.crtb.tunnelmonitor.task;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;

public class SectionStopEntity {
	
	public String workAreaCode;
	
	public String workSiteCode;
	
	public String sectionCode;
	
	public String remark = "";
	
	public int sectionOrPointState = 2; //0:删除，1:有效，2:封存
	
	public TunnelCrossSectionIndex tunnel;
	
	public SubsidenceCrossSectionIndex sub;

}
