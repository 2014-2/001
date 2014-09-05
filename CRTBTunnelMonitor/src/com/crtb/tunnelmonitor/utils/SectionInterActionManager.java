package com.crtb.tunnelmonitor.utils;

import java.util.ArrayList;
import java.util.List;
import ICT.utils.RSACoder;
import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionExIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionIndexDao;
import com.crtb.tunnelmonitor.dao.impl.v2.TunnelCrossSectionParameterDao;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionExIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionIndex;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;
import com.crtb.tunnelmonitor.entity.TunnelSettlementTotalData;

public class SectionInterActionManager {
	private static final String LOG_TAG = "SectionInterActionManager";
	private static final String UPLOAD_CROWN_GD_NAME = "GD";
	private static final String UPLOAD_TEST_POINT_NAME = "SL";
	private static final String UPLOAD_ITEM_SEPARATOR = "/";
	private static final String UPLOAD_COORDINATE_SEPARATOR ="#";
	private static final String CROWN_PREFIX = "A";
	private static final String ITEM_SEPARATOR = "/";
	private static final String POINT_NAME_SEPARATOR = ",";
	private static final String ITEM_CONNECTOR = "-";
	private TunnelCrossSectionParameterDao sectionParamDao;
	private TunnelCrossSectionParameter sectionParam = null;
	private ArrayList<String> crownList;
	private ArrayList<String> pointList;
			
	public SectionInterActionManager(int excavateMethod){
		init(excavateMethod);
	}
	
	public SectionInterActionManager(String sectionCode){
		if(sectionCode == null || sectionCode.length() < 1){
			Log.i(LOG_TAG, "sectionCode null");
			return;
		}
		//获取断面开挖方法
		TunnelCrossSectionExIndex sectionEx = TunnelCrossSectionExIndexDao.defaultDao().queryOneBySectionCode(sectionCode); 
		if(sectionEx == null){
			Log.i(LOG_TAG, sectionCode + "no sectionEx");
			return;
		}
		TunnelCrossSectionIndex section	= TunnelCrossSectionIndexDao.defaultDao().querySectionById(sectionEx.getSECT_ID());
		if(section == null){
			Log.i(LOG_TAG, sectionCode + "no section");
			return;
		}
		init(section.getExcavateMethod());

	}
	
	public SectionInterActionManager(String noUse,String sectionGuid){
		//根据GUID获取断面开挖方法	
		TunnelCrossSectionIndex section	= TunnelCrossSectionIndexDao.defaultDao().querySectionByGuid(sectionGuid);
		if(section == null){
			Log.i(LOG_TAG, "guid:"+sectionGuid + "no sectionEx");
			return;
		}
		init(section.getExcavateMethod());
	}
	
	/**
	 * 获取拱顶列表
	 * @return 拱顶列表,如：列表A1，A2……
	 */
	public ArrayList<String> getCrownPointList(){
		return crownList;
	}
	
	/**
	 * 获取测线列表
	 * @return 测线列表，如：列表S1-1,S1-2,S2-1,S2-2
	 */
	public ArrayList<String> getSurveyLinePointList(){
		return pointList;
	}
	
	/**
	 * 获取拱顶列表
	 * @return 拱顶列表,如：列表A1，A2……
	 */
	private ArrayList<String> getAllCrownPointNumberName() {
		ArrayList<String> crownList = new ArrayList<String>();
		int count = 0;
		int crownPointNumber = 0;
		
		if(sectionParam != null){
			crownPointNumber= sectionParam.getCrownPointNumber();
		}
		
		if (crownPointNumber < 1) {
			return null;
		}
		
		count = crownPointNumber + 1;
		for (int i = 1; i < count; i++) {
			crownList.add(CROWN_PREFIX + i);
		}
		return crownList;
	}

	/**
	 * 获取测线列表
	 * @return 测线列表，如：列表S1-1,S1-2,S2-1,S2-2
	 */
	private ArrayList<String> getAllSurveyLinePointList() {
		// 格式：S1,1,2/S2,3,4/S3,5,6/S4,7,8
		ArrayList<String> pointList = new ArrayList<String>();
		String[] lines = null;
		String[] details = null;
		String surveyLinePointName = "";
		
		if(sectionParam != null){
			surveyLinePointName= sectionParam.getSurveyLinePointName();
		}
		
		if (surveyLinePointName == null || surveyLinePointName.length() < 1) {
			return null;
		}
		lines = getSurveyPointByLineList(surveyLinePointName);
		
		for (String line : lines) {
			details = line.split(POINT_NAME_SEPARATOR);
			pointList.add(details[0] + ITEM_CONNECTOR + 1);
			pointList.add(details[0] + ITEM_CONNECTOR + 2);
		}
		return pointList;
	}
	
	/**
	 * 根据点的类型，获取具体的上传点序列
	 * @param pointType 点的类型，如A1，或者S2，或者S4
	 * @return
	 */
	public String getOneLineDetailsByPointType(String sectionCode,String pointType) {
		
		String pointInfo = "";
		if (pointType == null || pointType.length() < 1) {
			Log.i(LOG_TAG, "point type null");
			return "";
		}
		if(sectionCode == null || sectionCode.length() < 1){
			Log.i(LOG_TAG, "sectionCode null");
			return "";
		}
		//pointType:A2，或者A
		if (pointType.contains(CROWN_PREFIX)) {
			String gdIndex = pointType.replace(CROWN_PREFIX, "");
			int index = 1; //单个拱顶，A后面没有序号时，默认为1
			if(!gdIndex.equals("")){
				index = Integer.valueOf(gdIndex);
			}
			pointInfo =sectionCode + UPLOAD_CROWN_GD_NAME
					+ String.format("%02d", index);
			return pointInfo;
		}

		// pointType:S2,或者S4
		// surveyLinePointName：S1,1,2/S2,3,4/S3,5,6/S4,7,8
		String surveyLinePointName = sectionParam.getSurveyLinePointName();
		String[] lines = getSurveyPointByLineList(surveyLinePointName);
		String[] pointDetails;

		for (String line : lines) {
			pointDetails = line.split(POINT_NAME_SEPARATOR);
			if (pointDetails[0].equals(pointType)) {
				pointInfo =sectionCode + UPLOAD_TEST_POINT_NAME
						+ String.format("%02d", Integer.valueOf(pointDetails[1]))
						+ UPLOAD_COORDINATE_SEPARATOR +sectionCode + UPLOAD_TEST_POINT_NAME
						+ String.format("%02d", Integer.valueOf(pointDetails[2]));
			}
		}
		return pointInfo;
	}

	/**
	 * 根据断面编码，获取上传的测点序列
	 * @param sectionCode：断面编码
	 * @return
	 */
    public String getPointCodeListBySectionCode(String sectionCode){
    	if(crownList == null || pointList == null || crownList.size() < 1 || pointList.size() < 1){
    		return "";    		
    	}
    	int crownCount = crownList.size();
    	
    	String crownContent = "";
    	String pointContent = "";
		for (int crownIndex = 0; crownIndex < crownCount; crownIndex++) {
			crownContent += sectionCode + UPLOAD_CROWN_GD_NAME
					+ String.format("%02d", crownIndex + 1) + UPLOAD_ITEM_SEPARATOR;
		}

		// surveyLinePointName：S1,1,2/S2,3,4/S3,5,6/S4,7,8
		String surveyLinePointName = sectionParam.getSurveyLinePointName();
		String[] lines = getSurveyPointByLineList(surveyLinePointName);
		String[] pointDetails;

		for (String line : lines) {
			pointDetails = line.split(POINT_NAME_SEPARATOR);
			pointContent +=sectionCode + UPLOAD_TEST_POINT_NAME + String.format("%02d", Integer.valueOf(pointDetails[1])) + UPLOAD_COORDINATE_SEPARATOR 
					     + sectionCode + UPLOAD_TEST_POINT_NAME + String.format("%02d", Integer.valueOf(pointDetails[2])) + UPLOAD_ITEM_SEPARATOR;
		}
		pointContent = crownContent + pointContent;
		pointContent = pointContent.substring(0, pointContent.length() - 1);
		return pointContent;
    }

    /**
     * 根据测量数据，获取上传坐标序列
     * @param mtunnelTestPoints 测量数据
     * @return 坐标序列
     */
    public String getCoordinateList(List<TunnelSettlementTotalData> mtunnelTestPoints) {
    	if(crownList == null || pointList == null || crownList.size() < 1 || pointList.size() < 1){
    		return "";    		
    	}
    	int crownCount = crownList.size();
    	int pointCount = pointList.size();
    	
    	String crownContent = "";
    	String pointContent = "";
    	String corrdinateContent = "";
    	
    	TunnelSettlementTotalData crown;
    	TunnelSettlementTotalData p;
    	TunnelSettlementTotalData p1 = null;
    	TunnelSettlementTotalData p2 = null;
    	String p1Content;
    	String p2Content;
    	
		for (int crownIndex = 0; crownIndex < crownCount; crownIndex++) {
			crown = getPointByType(mtunnelTestPoints,crownList.get(crownIndex));
			if (crown != null) {
				crownContent += crown.getCoordinate().replace(
						POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR)
						+ UPLOAD_ITEM_SEPARATOR;
			}
		}

		for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
			p = getPointByType(mtunnelTestPoints,pointList.get(pointIndex));
			p1Content = "";
			p2Content = "";
			
			if(p != null){
				if (pointIndex % 2 == 0) {
					p1 = p;
				} else {
					p2 = p;
					if(p1 != null && p2 != null) {
						p1Content = p1.getCoordinate().replace(POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR);
						p2Content = p2.getCoordinate().replace(POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR);
						pointContent += p1Content + UPLOAD_COORDINATE_SEPARATOR + p2Content + UPLOAD_ITEM_SEPARATOR;
					}
				}
			}
		}
		
		corrdinateContent = crownContent + pointContent;
		corrdinateContent = corrdinateContent.substring(0, corrdinateContent.length() - 1);
    
		Log.i(LOG_TAG, "coordinate: " + corrdinateContent);
		corrdinateContent = RSACoder.encnryptDes(corrdinateContent, Constant.testDeskey);
		return corrdinateContent;
	}

    /**
     * 根据测量数据，获取上传测点的量测值
     * @param mtunnelTestPoints 测量数据
     * @return 测点的量测值
     */
	public String getValueList(List<TunnelSettlementTotalData> mtunnelTestPoints) {
		if(crownList == null || pointList == null || crownList.size() < 1 || pointList.size() < 1){
    		return null;    		
    	}
    	int crownCount = crownList.size();
    	int pointCount = pointList.size();
    	   	
    	String crownValue = "";
    	String pointValue = "";
    	String value = "";
    	
    	TunnelSettlementTotalData crown;
    	TunnelSettlementTotalData p;
    	TunnelSettlementTotalData p1 = null;
    	TunnelSettlementTotalData p2 = null;
    	
		for (int crownIndex = 0; crownIndex < crownCount; crownIndex++) {
			crown = getPointByType(mtunnelTestPoints,crownList.get(crownIndex));
			if (crown != null) {
				crownValue += crown.getCoordinate().split(POINT_NAME_SEPARATOR)[2] + UPLOAD_ITEM_SEPARATOR;
			}
		}

		for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
			p = getPointByType(mtunnelTestPoints,pointList.get(pointIndex));
			
			if(p != null){
				if (pointIndex % 2 == 0) {
					p1 = p;
				} else {
					p2 = p;
					if(p1 != null && p2 != null) {
						pointValue += AlertUtils.getLineLength(p1, p2) + UPLOAD_ITEM_SEPARATOR ;
					}
					p1 = null;
					p2 = null;
				}
			}
		}
		
		value =crownValue + pointValue;
		value = value.substring(0, value.length() - 1);
		Log.i(LOG_TAG, "value: " + value);
		return value;
	}
    
	/**
	 * 根据测量数据，获取上传坐标序列+测点的量测值
	 * @param mtunnelTestPoints
	 * @return
	 */
	public TestPointInfo getTestPointInfo(List<TunnelSettlementTotalData> mtunnelTestPoints) {
    	if(crownList == null || pointList == null || crownList.size() < 1 || pointList.size() < 1){
    		return null;    		
    	}
    	int crownCount = crownList.size();
    	int pointCount = pointList.size();
    	
    	String crownCoordinate = "";
    	String pointCoordinate = "";
    	String corrdinate = "";
    	
    	String crownValue = "";
    	String pointValue = "";
    	String value = "";
    	
    	TunnelSettlementTotalData crown;
    	TunnelSettlementTotalData p;
    	TunnelSettlementTotalData p1 = null;
    	TunnelSettlementTotalData p2 = null;
    	String p1Content;
    	String p2Content;
    	
		for (int crownIndex = 0; crownIndex < crownCount; crownIndex++) {
			crown = getPointByType(mtunnelTestPoints,crownList.get(crownIndex));
			if (crown != null) {
				crownCoordinate += crown.getCoordinate().replace(
						POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR)
						+ UPLOAD_ITEM_SEPARATOR;
				crownValue += crown.getCoordinate().split(POINT_NAME_SEPARATOR)[2] + UPLOAD_ITEM_SEPARATOR;
			}
		}

		for (int pointIndex = 0; pointIndex < pointCount; pointIndex++) {
			p = getPointByType(mtunnelTestPoints,pointList.get(pointIndex));
			p1Content = "";
			p2Content = "";
			
			if(p != null){
				if (pointIndex % 2 == 0) {
					p1 = p;
				} else {
					p2 = p;
					if(p1 != null && p2 != null) {
						p1Content = p1.getCoordinate().replace(POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR);
						p2Content = p2.getCoordinate().replace(POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR);
						pointCoordinate += p1Content + UPLOAD_ITEM_SEPARATOR + p2Content + UPLOAD_ITEM_SEPARATOR;
						pointValue += AlertUtils.getLineLength(p1, p2) + UPLOAD_ITEM_SEPARATOR ;
					}
					p1 = null;
					p2 = null;
				}
			}
		}
		
		corrdinate = crownCoordinate + pointCoordinate;
		corrdinate = corrdinate.substring(0, corrdinate.length() - 1);
		corrdinate = RSACoder.encnryptDes(corrdinate, Constant.testDeskey);
		
		value =crownValue + pointValue;
		value = value.substring(0, value.length() - 1);
    
		
		Log.i(LOG_TAG, "coordinate: " + corrdinate);
		
		TestPointInfo uploadPoint = new TestPointInfo();
		uploadPoint.pointCoordinate = corrdinate;
		uploadPoint.pointValue = value;
		return uploadPoint;
	}
	
	/**
	 * 上传坐标序列+测点的量测值
	 * @author xu
	 *
	 */
    public class TestPointInfo{
    	public String pointCoordinate;
    	public String pointValue;
    }
    
	private TunnelSettlementTotalData getPointByType(List<TunnelSettlementTotalData> mtunnelTestPoints,String type) {
		TunnelSettlementTotalData result = null;
		String pntType ;
		for (TunnelSettlementTotalData point : mtunnelTestPoints) {
			pntType = point.getPntType();
			if(pntType.equals(CROWN_PREFIX)){
                //兼容A、A1				
				pntType += 1;
			}
			if (type.equals(pntType)) {
				result = point;
			}
		}
		return result;
	}
    
	private String[] getSurveyPointByLineList(String surveyLinePointName){
		// 格式：S1,1,2/S2,3,4/S3,5,6/S4,7,8
	
		if (surveyLinePointName == null || surveyLinePointName.length() < 1) {
			return null;
		}
	
		return surveyLinePointName.split(ITEM_SEPARATOR);
	}
	
	public void init(int excavateMethod){
		sectionParamDao = TunnelCrossSectionParameterDao.defaultDao();
		sectionParam = sectionParamDao.queryOneByExcavateMethod(excavateMethod);
    	if(sectionParam != null){
    		int crownPointNum = sectionParam.getCrownPointNumber();
    		if(crownPointNum > 0){
    			crownList = getAllCrownPointNumberName();
    		}
    		
    		String pointLineName = sectionParam.getSurveyLinePointName();
    		if(pointLineName != null && pointLineName.length() > 0){
    			pointList = getAllSurveyLinePointList();
    		}
    	}
	}
}
