package com.crtb.tunnelmonitor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.network.CrtbWebService;
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
    		return -1 ;
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
    	
    	return -1 ;
    }
    
    public static String getExcavateMethodByStr(String method){
    	
    	if(method == null){
    		return "无" ;
    	}
    	
    	//DT-台阶法
    	//ST-三台阶法
    	//QD-全断面法
    	//HX-环行开挖
    	//ZG-中壁
    	//JC-交叉
    	//SC-双侧壁法
    	//DB-地表下沉
    	
    	if(method.equals("全断面法") 
    			|| method.equals("QD")){
    		return "全断面法" ;
    	} else if(method.equals("台阶法")
    			|| method.equals("DT")){
    		return "台阶法" ;
    	} else if(method.equals("三台阶法")
    			|| method.equals("ST")){
    		return "三台阶法" ;
    	} else if(method.equals("双侧壁法")
    			|| method.equals("SC")){
    		return "双侧壁法" ;
    	}
    	
    	return "无" ;
    }
    
    public static void fillSectionParamter(TunnelCrossSectionIndex section,SectionUploadParamter outParamter){
    	if(section == null || outParamter == null){
    		return ;
    	}
    	outParamter.setSectionName(section.getSectionName());
    	CrtbAppConfig config = CrtbAppConfig.getInstance();
    	int sectionSequence = config.getSectionSequence() + 1;
    	config.setSectionSequence(sectionSequence);
    	String sectionCode = CrtbWebService.getInstance().getSiteCode() + String.format("%04d",  sectionSequence);
    	outParamter.setSectioCode(sectionCode);
    	//使用自己编码的方式
    	String pointList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#" 
    	+ sectionCode + "SL02" + "/" + sectionCode + "SL03" + "#" + sectionCode + "SL04";
    	//outParamter.setPointList(section.getSurveyPntName());
    	outParamter.setPointList(pointList);
    	//TODO: 值要保持一至
    	//outParamter.setChainage(String.valueOf(section.getChainage()));
    	outParamter.setChainage(section.getSectionName());
    	//TODO:类型编码不对
    	//outParamter.setDigMethod(String.valueOf(section.getExcavateMethod()));
    	outParamter.setDigMethod("QD");
    	outParamter.setWidth(section.getWidth());
    	//TODO:暂时取不到数据,使用"50.0f"
    	//outParamter.setTotalU0Limit(section.getGDU0());
    	outParamter.setTotalU0Limit(50.0f);
    	//TODO:暂时取不到数据,使用当前时间
    	//outParamter.setModifiedTime(section.getGDU0Time());
    	outParamter.setModifiedTime(new Date());
    	//TODO:暂时取不到数据,使用"xxx"
    	//outParamter.setU0Remark(section.getGDU0Description());
    	outParamter.setU0Remark("xxx");
    	//TODO: 暂时取不到数据，使用固定值3
    	//outParamter.setWallRockLevel(Integer.valueOf(section.getLithologi()));
    	outParamter.setWallRockLevel(3);
    	//TODO:暂时取不到数据,使用当前时间
    	//outParamter.setFirstMeasureDate(section.getInBuiltTime());
    	outParamter.setFirstMeasureDate(new Date());
    	outParamter.setRemark(section.getInfo());
    }
    
    public static void fillSectionParamter(SubsidenceCrossSectionIndex section,SectionUploadParamter outParamter){
    	
    	if(section == null || outParamter == null){
    		return ;
    	}
    	outParamter.setSectionName(section.getSectionName());
    	//outParamter.setSectioCode(section.get) // ?
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
