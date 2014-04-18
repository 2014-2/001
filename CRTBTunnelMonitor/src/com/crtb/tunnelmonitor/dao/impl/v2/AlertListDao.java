package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import com.crtb.tunnelmonitor.entity.AlertList;
import com.crtb.tunnelmonitor.entity.SubsidenceTotalData;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class AlertListDao extends AbstractDao<AlertList> {

	private static AlertListDao _instance ;
	
	private AlertListDao(){
		
	}
	
	public static AlertListDao defaultDao(){
		
		if(_instance == null){
			_instance	= new AlertListDao() ;
		}
		
		return _instance ;
	}
	
	public List<AlertList> queryAllRawSheetIndex() {
		
		String sql = "select * from AlertList";
		
		return mDatabase.queryObjects(sql, AlertList.class);
	}

    /**
     * @param point 产生预警信息的那次测量的测量点信息
     * @param alertLevel
     * @param Utype
     * @param UValue
     * @param UMax
     * @param originalDataID
     * @return
     */
    public int insertItem(TunnelSettlementTotalData point, int alertLevel, int Utype,
            double UValue, double UMax, String originalDataID) {
        AlertList al = new AlertList();
        String pntType = point.getPntType();
        if (pntType != null && pntType.contains("_")) {// such as "S1_1" or "S1_2"
            pntType = pntType.substring(0, pntType.indexOf("_"));
        }
        al.setSheetID(point.getSheetId());
        al.setCrossSectionID(point.getChainageId());
        al.setPntType(pntType);
        al.setAlertTime(point.getSurveyTime());
        al.setAlertLeverl(alertLevel);
        al.setUtype(Utype);
        al.setUValue(UValue);
        al.setUMax(UMax);
        al.setOriginalDataID(originalDataID);
        return mDatabase.saveObject(al);
    }

    /**
     * @param point 产生预警信息的那次测量的测量点信息
     * @param alertLevel
     * @param Utype
     * @param UValue
     * @param UMax
     * @param originalDataID
     * @return
     */
    public int insertItem(SubsidenceTotalData point, int alertLevel, int Utype,
            double UValue, double UMax, String originalDataID) {
        AlertList al = new AlertList();
        al.setSheetID(point.getSheetId());
        al.setCrossSectionID(point.getChainageId());
        al.setPntType(point.getPntType());
        al.setAlertTime(point.getSurveyTime());
        al.setAlertLeverl(alertLevel);
        al.setUtype(Utype);
        al.setUValue(UValue);
        al.setUMax(UMax);
        al.setOriginalDataID(originalDataID);
        return mDatabase.saveObject(al);
    }
}
