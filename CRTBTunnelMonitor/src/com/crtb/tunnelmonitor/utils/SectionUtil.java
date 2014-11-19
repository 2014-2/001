package com.crtb.tunnelmonitor.utils;

import com.crtb.tunnelmonitor.dao.impl.v2.*;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class SectionUtil {

	/**
	 * 是否能够封存
	 * @param sectionGuid 断面的Guid
	 * @param type 断面类型：地表=0，隧道内=1
	 * @return 
	 */
	public static boolean canStop(String sectionGuid,int type) {
		
		if(type == 0){
			SubsidenceTotalData sub = SubsidenceTotalDataDao.defaultDao().queryLastOne();
			if(sectionGuid != sub.getGuid()){
				return true;
			}
		} else if(type == 1){
			TunnelSettlementTotalData tunnel = TunnelSettlementTotalDataDao.defaultDao().queryLastOne();
			if(sectionGuid != tunnel.getGuid()){
				return true;
			}
		}
		return false;
	}

}
