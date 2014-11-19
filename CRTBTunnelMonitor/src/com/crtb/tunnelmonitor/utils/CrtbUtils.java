package com.crtb.tunnelmonitor.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.zw.android.framework.util.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crtb.tunnelmonitor.AppCRTBApplication;
import com.crtb.tunnelmonitor.activity.R;
import com.crtb.tunnelmonitor.activity.SectionStopActivity;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.ExcavateMethodDao;
import com.crtb.tunnelmonitor.dao.impl.v2.ProjectIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SiteProjectMappingDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SubsidenceCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.SurveyerInformationDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.WorkSiteIndexDao;
import com.crtb.tunnelmonitor.entity.CrtbUser;
import com.crtb.tunnelmonitor.entity.ExcavateMethodEnum;
import com.crtb.tunnelmonitor.entity.ProjectIndex;
import com.crtb.tunnelmonitor.entity.SiteProjectMapping;
import com.crtb.tunnelmonitor.entity.SubsidenceCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.SurveyerInformation;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;
import com.crtb.tunnelmonitor.entity.WorkSiteIndex;
import com.crtb.tunnelmonitor.network.CrtbWebService;
import com.crtb.tunnelmonitor.network.PointUploadParameter;
import com.crtb.tunnelmonitor.network.SectionUploadParamter;

/**
 * 所有的浮点数精确到3位
 * 
 * @author zhouwei
 *
 */
public final class CrtbUtils {
	
	// static DecimalFormat df = new DecimalFormat("#.0000");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static double formatDouble(double value, int scale) {
        return formatDouble(String.valueOf(value), scale);
    }

    public static double formatDouble(String value, int scale) {
        BigDecimal b = new BigDecimal(value);
        return b.setScale(scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public static double formatDouble(double value) {
        return formatDouble(String.valueOf(value));
    }

    public static double formatDouble(String value) {
        return formatDouble(value, 3);
    }

	public static String doubleToString(double value){
		BigDecimal b = new BigDecimal(value);
		return b.setScale(3,BigDecimal.ROUND_HALF_DOWN).toString();
	}

	public static float formatFloat(String value){
		BigDecimal b = new BigDecimal(value);
		return b.setScale(3,BigDecimal.ROUND_HALF_DOWN).floatValue() ;
	}
	
	public static String formatSectionName(String pre, double value){
		
		String str	= doubleToString(value);
		double v	= formatDouble(value);
		
		String km 	= String.valueOf((int)(v / 1000));
		String m 	= String.valueOf((int)(v % 1000));
		String desc	= str.indexOf(".") >= 0 ? str.substring(str.indexOf(".")) : "" ;
		
		return pre + km + "+" + m + desc;
	}
	
	public static String generatorGUID(){
		return UUID.randomUUID().toString() ;
	}
	
	public static int getRockgrade(String str){
		
		if (str == null) {
			return 0;
		}
		
		if(str.equals("I")){
			return 0 ;
		} else if(str.equals("II")){
			return 1 ;
		} else if(str.equals("III")){
			return 2 ;
		} else if(str.equals("IV")){
			return 3 ;
		} else if(str.equals("V")){
			return 4 ;
		} else if(str.equals("VI")){
			return 5 ;
		}
		
		return 0;
	}
	
    public static Date parseDate(String text) {
        Date result = null;
        if (!TextUtils.isEmpty(text)) {
            try {
                result = DATE_FORMAT.parse(text);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }
    
    public static void fillSectionParamter(TunnelCrossSectionIndex section,
            SectionUploadParamter outParamter) {
        if (section == null || outParamter == null) {
            return ;
        }
        outParamter.setSectionName(section.getSectionName().split("\\.")[0]);
        CrtbAppConfig config = CrtbAppConfig.getInstance();
        int sectionSequence = config.getSectionSequence() + 1;
        config.setSectionSequence(sectionSequence);
        String sectionCode = CrtbWebService.getInstance().getSiteCode()
                + String.format("%04d", sectionSequence);
        outParamter.setSectioCode(sectionCode);
        int excavateMethod = section.getExcavateMethod();
        if(excavateMethod >= Constant.CUSTOM_METHOD_START_INDEX){
        	excavateMethod = ExcavateMethodDao.defaultDao().queryBaseModelByCustomExcavateMethod(excavateMethod);
        }
        String digMethod = ExcavateMethodEnum.parser(excavateMethod).toString();
        if(digMethod == "CD"){
        	digMethod = "ZG";
        } else if(digMethod == "CRD"){
        	digMethod = "JC";
        } 
        outParamter.setDigMethod(digMethod);
        String pointList = "";
        
        //YX 生成上传的测点列表数据
        pointList = SectionInterActionManager.getPointCodeListBySectionCode(sectionCode,section.getExcavateMethod());
  
		if (pointList == null || pointList.equals("")) {
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
        outParamter.setWallRockLevel(getRockgrade(section.getROCKGRADE()) + 1);

        // TODO:若取不到数据,使用当前时间
        Date builtTime = section.getInbuiltTime();
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
        final int totalCount = section.getSurveyPnts();
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
        outParamter.setWallRockLevel(getRockgrade(section.getROCKGRADE()) + 1);
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

    public static int getCrtbUserTypeByTypeStr(String typeStr) {
        int type = CrtbUser.LICENSE_TYPE_DEFAULT;
        if (typeStr != null && typeStr.length() == 2) {
            if (typeStr.equals(CrtbUser.USER_LICENSE_TYPE_STR_DEFAULT)) {
                type = CrtbUser.LICENSE_TYPE_DEFAULT;
            } else if (typeStr.equals(CrtbUser.USER_LICENSE_TYPE_STR_TRIAL)) {
                type = CrtbUser.LICENSE_TYPE_TRIAL;
            } else if (typeStr.equals(CrtbUser.USER_LICENSE_TYPE_STR_REGISTERED)) {
                type = CrtbUser.LICENSE_TYPE_REGISTERED;
            }
        }
        return type;
    }
    
    public static int updateNewSectionCodeNumber() {

		String tag = "GenerateSectionCode";
		boolean state = false;
		int maxNo = 0;
		int maxTunnelSectionNo = -2;
		int maxSubSectionNo = -2;


		maxTunnelSectionNo = TunnelCrossSectionExIndexDao.defaultDao()
				.queryMaxTunnelSectionNo();
		maxSubSectionNo = SubsidenceCrossSectionExIndexDao.defaultDao()
				.queryMaxSubsidenceSectionNo();

		if (maxTunnelSectionNo < 0 || maxSubSectionNo < 0) {
			Log.i(tag, "断面编码数据异常");
		} else{
			maxNo = maxTunnelSectionNo > maxSubSectionNo ? maxTunnelSectionNo
					: maxSubSectionNo;
			state = true;
		}
		
		CrtbAppConfig.getInstance().setSectionSequence(maxNo);
		Log.i("CrtbWebService", "更新断面编码:"+maxNo);
		return maxNo;
	}
    
	public static SurveyerInformation getSurveyerInfoBySheetGuid(String sheetGuid){
		String TAG = "CrtbWebServices";
		SurveyerInformationDao surveyerInfoDao = SurveyerInformationDao
				.defaultDao();
		SurveyerInformation surveyer = surveyerInfoDao
				.querySurveyerBySheetGuid(sheetGuid);

		// 相应记录单没有测量员信息
		if (surveyer == null) {
			Log.i(TAG, "RawSheetIndex no Surveyer");
			surveyer = AppCRTBApplication.getInstance().getCurPerson();
		}

		//确保测量员不为null
		if (surveyer == null) {
			surveyer = new SurveyerInformation();
		}
		if (surveyer.getSurveyerName() == null) {
			surveyer.setSurveyerName("");
		}

		if (surveyer.getCertificateID() == null) {
			surveyer.setCertificateID("");
		}
		return surveyer;
	}
	
	public static String[] getWorkSiteInfo() {
		String[] list = new String[2];
		int workId = 0;
		String curProjectName = "";
		String curWorkSiteName = "";
		final String KEY_CURRENT_WORKPLAN = "_key_current_workplan";
		SiteProjectMappingDao siteProjDao = null;
		SiteProjectMapping siteProjMapping = null;
		WorkSiteIndexDao worksiteDao = null;
		WorkSiteIndex worksite = null;
		ProjectIndexDao projIndexDao = null;
		
		projIndexDao = ProjectIndexDao.defaultWorkPlanDao();
		
		ProjectIndex mCurrentWorkPlan = projIndexDao.queryEditWorkPlan();
		if (mCurrentWorkPlan != null) {

			curProjectName = mCurrentWorkPlan.getProjectName();
			siteProjDao = SiteProjectMappingDao.defaultDao();
			siteProjMapping = siteProjDao.queryOneByProjectId(mCurrentWorkPlan
					.getId());
			if (siteProjMapping != null) {
				workId = siteProjMapping.getWorkSiteId();
				worksiteDao = WorkSiteIndexDao.defaultDao();
				worksite = worksiteDao.queryWorkSiteById(workId);
				if (worksite != null) {
					curWorkSiteName = worksite.getSiteName();
				}
			}
		} else {
			curProjectName = "当前还没有打开工作面";
		}
		if (curWorkSiteName == "") {
			curWorkSiteName = "当前工作面没有关联工点";
		}

		list[0] = curProjectName;
		list[1] = curWorkSiteName;

		return list;
	}
	
	public static void updateWorkSiteInfo(ProjectIndex currentProject) {

		WorkSiteIndex workSiteIndex = null;
		
		if (currentProject != null) {
			// 判断是否关联工点
			SiteProjectMapping mapping = SiteProjectMappingDao.defaultDao()
					.queryOneByProjectId(currentProject.getId());
			if (mapping != null) {

				// 获取工点信息、并保存到CrtbWebService中工点信息
				workSiteIndex = WorkSiteIndexDao.defaultDao()
						.queryWorkSiteById(mapping.getWorkSiteId());
			}
		}
		
		if (workSiteIndex == null) {
			// 清空当前工点信息
			CrtbWebService.getInstance().setZoneCode("");
			CrtbWebService.getInstance().setSiteCode("");
		} else {
			// 保存当前工点信息
			CrtbWebService.getInstance().setZoneCode(
					workSiteIndex.getZoneCode());
			CrtbWebService.getInstance().setSiteCode(
					workSiteIndex.getSiteCode());
		}
	}

	public interface UploadCall{
		public void ok();
	}
	
	public static void showDialogWithYesOrNo(Context context,String title, String msg,final UploadCall caller) {
		if(context == null || StringUtils.isEmpty(title) || StringUtils.isEmpty(msg) || caller == null){
			return;
		}
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				caller.ok();
			}
		});
		
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}
	
	public static void showDialogWithYes(Context context,String title, String msg) {
		if(context == null || StringUtils.isEmpty(title) || StringUtils.isEmpty(msg)){
			return;
		}
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	public static ViewGroup getProgressLayout(Activity activity){
		return (LinearLayout)activity.getLayoutInflater().inflate(R.layout.layout_progress_overlay, null);
	}
}
