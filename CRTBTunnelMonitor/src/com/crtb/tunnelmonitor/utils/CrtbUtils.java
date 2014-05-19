package com.crtb.tunnelmonitor.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;

public final class CrtbUtils {
	
	static DecimalFormat df = new DecimalFormat("#.0000");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static double formatDouble(double value){
		return formatDouble(String.valueOf(value)) ; 
	}
	
	public static double formatDouble(String value){
		BigDecimal b = new BigDecimal(value);
		return b.setScale(4,BigDecimal.ROUND_DOWN).doubleValue() ;
	}
	
	public static float formatFloat(String value){
		BigDecimal b = new BigDecimal(value);
		return b.setScale(4,BigDecimal.ROUND_DOWN).floatValue() ;
	}
	
	public static String formatSectionName(String pre, double value){
		
		String str	= String.valueOf(value);
		double v	= formatDouble(value);
		
		String km 	= String.valueOf((int)(v / 1000));
		String m 	= String.valueOf((int)(v % 1000));
		String desc	= str.indexOf(".") >= 0 ? str.substring(str.indexOf(".")) : "" ;
		
		return pre + km + "+" + m + desc;
	}
	
	public static String generatorGUID(){
		return UUID.randomUUID().toString() ;
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

    public static String getExcavateMethodCode(String method){
        
        if(method == null){
            return "" ;
        }
        
        //DT-台阶法
        //ST-三台阶法
        //QD-全断面法
        //HX-环行开挖
        //ZG-中壁
        //JC-交叉
        //SC-双侧壁法
        //DB-地表下沉
        
        if(method.equals("QD") 
                || method.equals("全断面法")){
            return "QD" ;
        } else if(method.equals("DT")
                || method.equals("台阶法")){
            return "DT" ;
        } else if(method.equals("ST")
                || method.equals("三台阶法")){
            return "ST" ;
        } else if(method.equals("SC")
                || method.equals("双侧壁法")){
            return "SC" ;
        }
        
        return "" ;
    }

    public static void fillSectionParamter(TunnelCrossSectionIndex section,
            SectionUploadParamter outParamter) {
        if (section == null || outParamter == null) {
            return;
        }
        outParamter.setSectionName(section.getSectionName().split("\\.")[0]);
        CrtbAppConfig config = CrtbAppConfig.getInstance();
        int sectionSequence = config.getSectionSequence() + 1;
        config.setSectionSequence(sectionSequence);
        String sectionCode = CrtbWebService.getInstance().getSiteCode()
                + String.format("%04d", sectionSequence);
        outParamter.setSectioCode(sectionCode);
        String digMethod = getExcavateMethodCode(section.getExcavateMethod());
        outParamter.setDigMethod(digMethod);
        String pointList = "";
        if ("QD".equals(digMethod)) {// 全断面法
            pointList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#" + sectionCode
                    + "SL02";
        } else if ("ST".equals(digMethod) || "SC".equals(digMethod)) { // 三台阶法或又侧壁法
            pointList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#" + sectionCode
                    + "SL02" + "/" + sectionCode + "SL03" + "#" + sectionCode + "SL04" + "/"
                    + sectionCode + "SL05" + "#" + sectionCode + "SL06";
        } else if ("DT".equals(digMethod)) {// 台阶法
            pointList = sectionCode + "GD01" + "/" + sectionCode + "SL01" + "#" + sectionCode
                    + "SL02" + "/" + sectionCode + "SL03" + "#" + sectionCode + "SL04";
        } else {
            Log.e("upload", "unknown dig method: " + digMethod);
        }

        outParamter.setPointList(pointList);
        outParamter.setChainage(formatSectionName(getSectionPrefix(), section.getChainage()).split(
                "\\.")[0]);
        outParamter.setWidth((int) section.getWidth());

        // 使用GDU0和SLU0中的较大值
        float gdU0 = section.getGDU0();
        float slU0 = section.getSLU0();
        outParamter.setTotalU0Limit(Math.max(gdU0, slU0));

        // TODO:若取不到数据,使用当前时间
        Date gdU0Date = section.getGDU0Time();
        outParamter.setModifiedTime(gdU0Date != null ? gdU0Date : new Date());

        // 暂时先使用拱顶备注
        String gdU0Des = section.getGDU0Description();
        if (gdU0Des == null) {
            gdU0Des = "";
        }
        outParamter.setU0Remark(gdU0Des);

        // TODO: 暂时取不到数据，使用固定值3
        // outParamter.setWallRockLevel(Integer.valueOf(section.getLithologi()));
        outParamter.setWallRockLevel(3);

        // TODO:若取不到数据,使用当前时间
        Date builtTime = section.getInBuiltTime();
        outParamter.setFirstMeasureDate(builtTime != null ? builtTime : new Date());
        outParamter.setRemark(section.getInfo());
    }

    public static void fillSectionParamter(SubsidenceCrossSectionIndex section,
            SectionUploadParamter outParamter) {
        if (section == null || outParamter == null) {
            return;
        }
        outParamter.setSectionName(section.getSectionName().split("\\.")[0]);
        CrtbAppConfig config = CrtbAppConfig.getInstance();
        int sectionSequence = config.getSectionSequence() + 1;
        config.setSectionSequence(sectionSequence);
        String sectionCode = CrtbWebService.getInstance().getSiteCode()
                + String.format("%04d", sectionSequence);
        outParamter.setSectioCode(sectionCode);
        StringBuilder sb = new StringBuilder();
        final int totalCount = Integer.parseInt(section.getSurveyPnts());
        for (int i = 0; i < totalCount; i++) {
            sb.append(sectionCode + "DB" + String.format("%02d", i) + "/");
        }
        sb.deleteCharAt(sb.lastIndexOf("/"));
        outParamter.setPointList(sb.toString());
        // FIX:需要将里程转换成DK-XXX
        // outParamter.setChainage(String.valueOf(section.getChainage()));
        // outParamter.setDigMethod(String.valueOf(section.getExcavateMethod()));
        outParamter.setChainage(formatSectionName(getSectionPrefix(), section.getChainage()).split(
                "\\.")[0]);
        outParamter.setDigMethod("QD");
        outParamter.setWidth((int) section.getWidth());
        outParamter.setTotalU0Limit(section.getDBU0());
        Date dbU0time = section.getDBU0Time();
        outParamter.setModifiedTime(dbU0time != null ? dbU0time : new Date());

        String dbU0Des = section.getDBU0Description();
        if (dbU0Des == null) {
            dbU0Des = "";
        }
        outParamter.setU0Remark(dbU0Des);
        // TODO: 暂时取不到数据，使用固定值3
        // outParamter.setWallRockLevel(Integer.valueOf(section.getLithologic()));
        outParamter.setWallRockLevel(3);
        Date inBuiltTime = section.getInbuiltTime();
        outParamter.setFirstMeasureDate(inBuiltTime != null ? inBuiltTime : new Date());
        outParamter.setRemark(section.getInfo());
    }

    public static void fillTunnelTestRecord(TunnelSettlementTotalData data, PointUploadParameter outParamter){
    	
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

    public static String getSectionPrefix() {
        String pre = "";
        ProjectIndex currentProject = ProjectIndexDao.defaultWorkPlanDao().queryEditWorkPlan();
        if (currentProject != null) {
            pre = currentProject.getChainagePrefix();
        }

        return pre;
    }

    public static String getSurveyorName() {
        SurveyerInformation s = AppCRTBApplication.getInstance().getCurPerson();
        return s != null ? s.getSurveyerName() : "";
    }

    public static String getSurveyorCertificateID() {
        SurveyerInformation s = AppCRTBApplication.getInstance().getCurPerson();
        return s != null ? s.getCertificateID() : "";
    }

    public static int getAppVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return info == null ? 0 : info.versionCode;
    }

    public static int getCrtbUserTypeByUsername(String username) {
        int type = CrtbUser.LICENSE_TYPE_DEFAULT;
        if (username != null && username.length() > 10) {
            String typeStr = username.substring(username.length() - 10, username.length() - 8);
            if (typeStr != null) {
                if (typeStr.equals("00")) {
                    type = CrtbUser.LICENSE_TYPE_DEFAULT;
                } else if (typeStr.equals("10")) {
                    type = CrtbUser.LICENSE_TYPE_TRIAL;
                }  else if (typeStr.equals("20")) {
                    type = CrtbUser.LICENSE_TYPE_REGISTERED;
                }
            }
        }
        return type;
    }
}
