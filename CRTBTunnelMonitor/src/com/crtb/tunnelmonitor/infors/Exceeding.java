package com.crtb.tunnelmonitor.infors;

import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class Exceeding {
	public int uType;
	public int leijiType = -1;
	public int sulvType = -1;
    
	public double leijiValue = -1;
	public double sulvValue = -1;
	public double accumulativeSubsidence;
	public double originalSulvAlertValue;
    
	public TunnelSettlementTotalData tunnelData;
	
	public SubsidenceTotalData subData;
	
	public boolean isTransfinite;
	
	public String originalDataID;
    
}
