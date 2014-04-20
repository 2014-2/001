package com.crtb.tunnelmonitor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;

public final class CrtbUtils {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String formatSectionName(String pre, float value){
		
		String km = String.valueOf((int)(value / 1000));
		String m = String.valueOf((int)(value % 1000));
		
		return pre + km + "+" + m ;
	}
	
    public static Date parseDate(String text) {
        Date result = null;
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("Date text is empty.");
        }
        try {
            result = DATE_FORMAT.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
    
    public static int getExcavateMethod(String method){
    	
    	if(method == null){
    		return 0 ;
    	}
    	
    	if(method.equals("全断面法")){
    		return 0 ;
    	} else if(method.equals("台阶法")){
    		return 1 ;
    	} else if(method.equals("三台阶法")){
    		return 2 ;
    	} else if(method.equals("双侧壁法")){
    		return 3 ;
    	}
    	
    	return 0 ;
    }
    
    public static void fillSectionParamter(TunnelCrossSectionIndex section,SectionUploadParamter outParamter){
    	
    	if(section == null || outParamter == null){
    		return ;
    	}
    	
    	outParamter.setSectionName(section.getSectionName());
    	// outParamter.setSectioCode(section.get) // ?
    	outParamter.setChainage(String.valueOf(section.getChainage()));
    	outParamter.setDigMethod(String.valueOf(section.getExcavateMethod()));
    	outParamter.setWidth(section.getWidth());
    	outParamter.setTotalU0Limit(section.getGDU0());
    	outParamter.setModifiedTime(section.getGDU0Time());
    	outParamter.setU0Remark(section.getGDU0Description());
    	outParamter.setWallRockLevel(Integer.valueOf(section.getLithologi()));
    	outParamter.setPointList(section.getSurveyPntName());
    	outParamter.setFirstMeasureDate(section.getInBuiltTime());
    	outParamter.setRemark(section.getInfo());
    }
    
    public static void fillSectionParamter(SubsidenceCrossSectionIndex section,SectionUploadParamter outParamter){
    	
    	if(section == null || outParamter == null){
    		return ;
    	}
    	
    	outParamter.setSectionName(section.getSectionName());
    	// outParamter.setSectioCode(section.get) // ?
    	outParamter.setChainage(String.valueOf(section.getChainage()));
    	// outParamter.setDigMethod(String.valueOf(section.getExcavateMethod()));
    	outParamter.setWidth(section.getWidth());
    	outParamter.setTotalU0Limit(section.getDBU0());
    	outParamter.setModifiedTime(section.getDBU0Time());
    	outParamter.setU0Remark(section.getDBU0Description());
    	outParamter.setWallRockLevel(Integer.valueOf(section.getLithologic()));
    	outParamter.setPointList(section.getSurveyPnts());
    	outParamter.setFirstMeasureDate(section.getInbuiltTime());
    	outParamter.setRemark(section.getInfo());
    	
    }
    
    public static void fillTunnelTestRecord(TunnelSettlementTotalData data,PointUploadParameter outParamter){
    	
    	if(data == null || outParamter == null){
    		return ;
    	}
    	
    	// 断面序列号 ？
    	outParamter.setSectionCode(String.valueOf(data.getID()));
    	// outParamter.setPointCodeList(data.get) ?
    	//outParamter.setTunnelFaceDistance(data.getFacedk());
    	//outParamter.setProcedure(data.getFacedescription());
    	// outParamter.setMonitorModel(data.get)
    }
	
}
