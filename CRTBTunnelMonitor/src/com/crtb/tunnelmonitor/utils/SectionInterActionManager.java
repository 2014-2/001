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
	private static final String LINE_PREFIX = "S";
	private static final String CROWN_PREFIX = "A";
	private static final String POINT_NAME_SEPARATOR = ",";
	private static final String ITEM_CONNECTOR = "-";
	private TunnelCrossSectionParameterDao sectionParamDao;
	private TunnelCrossSectionParameter sectionParam = null;
	
	public SectionInterActionManager(int excavateMethod){
		sectionParamDao = TunnelCrossSectionParameterDao.defaultDao();
		sectionParam = sectionParamDao.queryOneByExcavateMethod(excavateMethod);
	}
	
		
	/**
	 * 根据拱顶个数获取拱顶点列表
	 * @return 拱顶列表,如：列表A,或者A1，A2，A3……
	 */
	public ArrayList<String> getCrownPointListByNumber() {
		ArrayList<String> crownList = new ArrayList<String>();
		int count = 0;
		int crownPointNumber = 0;
		
		if(sectionParam != null){
			crownPointNumber= sectionParam.getCrownPointNumber();
		}
		
		if (crownPointNumber < 1) {
			return null;
		}
		if(crownPointNumber == 1){
			crownList.add(CROWN_PREFIX);
		} else {
			count = crownPointNumber + 1;
			for (int i = 1; i < count; i++) {
				crownList.add(CROWN_PREFIX + i);
			}
		}
		return crownList;
	}

	
	/**
	 * 根据测线条数，获取测线的点名称列表
	 * @return 测线列表，如：列表S1-1,S1-2,S2-1,S2-2
	 */
 	public ArrayList<String> getSurveyPointNameByNumber() {
		ArrayList<String> pointList = new ArrayList<String>();
		int LineNumber = 0;
		
		if(sectionParam != null){
			LineNumber = sectionParam.getSurveyLineNumber() + 1;
			for(int lineIndex = 1;lineIndex < LineNumber ;lineIndex ++){
				pointList.add(LINE_PREFIX + lineIndex + ITEM_CONNECTOR + 1);
				pointList.add(LINE_PREFIX + lineIndex + ITEM_CONNECTOR + 2);
			}
		}
		return pointList;
	}


 	/**
	 * 根据断面编码、开挖方法，获取断面的测点序列
	 * @param sectionCode：断面编码
	 * @param excavateMethod：开挖方法
	 * @return 如包含一个拱顶，两条测线:XLGC123456780001GD01/XLGC123456780001SL01#XLGC123456780001SL02/XLGC123456780001SL03#XLGC123456780001SL04
	 *         如包含两个拱顶，一条测线:XLGC123456780001GD01/XLGC123456780001GD02/XLGC123456780001SL01#XLGC123456780001SL02
	 */
    public static String getPointCodeListBySectionCode(String sectionCode,int excavateMethod){
    	TunnelCrossSectionParameterDao sectionParamDao = TunnelCrossSectionParameterDao.defaultDao();
    	TunnelCrossSectionParameter sectionParam = sectionParamDao.queryOneByExcavateMethod(excavateMethod);
    	String crownContent = "";
    	String pointContent = "";
    	int start = 0;
		int end = 0;
		int crownCount = sectionParam.getCrownPointNumber() + 1;
		int lineCount = sectionParam.getSurveyLineNumber() + 1;
		
    	for (int crownIndex = 1; crownIndex < crownCount; crownIndex++) {
			crownContent += sectionCode + UPLOAD_CROWN_GD_NAME + String.format("%02d", crownIndex) + UPLOAD_ITEM_SEPARATOR;
		}
		
		for(int pIndex = 1; pIndex < lineCount;pIndex ++){
			end = 2 * pIndex;
			start = end - 1;
			pointContent += sectionCode + UPLOAD_TEST_POINT_NAME + String.format("%02d", start) + UPLOAD_COORDINATE_SEPARATOR 
					      + sectionCode + UPLOAD_TEST_POINT_NAME + String.format("%02d", end) + UPLOAD_ITEM_SEPARATOR;
		}
		pointContent = crownContent + pointContent;
		pointContent = pointContent.substring(0, pointContent.length() - 1);
		return pointContent;
    }

	/**
	 * 根据点的类型，获取上传数据的测点序列
	 * @param pointType 点的类型，如A1，或者S2，或者S4
	 * @return XLGC123456780001GD01,或者XLGC123456780001SL03#XLGC123456780001SL04，或者 XLGC123456780001SL07#XLGC123456780001SL08
	 */
	public static String getOneLineDetailsByPointType(String sectionCode,String pointType) {
		
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
		pointType = pointType.replace("S", "");
		int start = 0;
		int end = 0;
		int lineIndex = 0;
		try{
			lineIndex = Integer.valueOf(pointType);	
		} catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
		end = lineIndex * 2;
		start = end - 1;

		pointInfo = sectionCode + UPLOAD_TEST_POINT_NAME
				+ String.format("%02d", start) + UPLOAD_COORDINATE_SEPARATOR
				+ sectionCode + UPLOAD_TEST_POINT_NAME
				+ String.format("%02d", end);

		return pointInfo;
	}

    /**
     * 根据测量数据，获取上传数据的量测坐标序列
     * @param mtunnelTestPoints 测量数据,一个拱顶，或者是测点的两个点，顺序如:A,或者S2-1、S2-2
     * @return 坐标序列如拱顶：100#100#20，测线：100#100#20#200#100#20
     */
    public static String getCoordinateList(List<TunnelSettlementTotalData> mtunnelTestPoints) {
    	String corrdinate = "";
    	String p1Content = "";
    	String p2Content = "";
    	TunnelSettlementTotalData p;
    	TunnelSettlementTotalData p1;
    	TunnelSettlementTotalData p2;
    	
    	int tpCount = mtunnelTestPoints.size();
    	
    	//拱顶
    	if(tpCount == 1){
    		p = mtunnelTestPoints.get(0);			
    		corrdinate = p.getCoordinate().replace(POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR) 
					+ UPLOAD_ITEM_SEPARATOR;
    	} 
    	//测线
    	else if(tpCount == 2){
    		p1 = mtunnelTestPoints.get(0);
    		p2 = mtunnelTestPoints.get(1);
    		if(p1 != null && p2 != null) {
				p1Content = p1.getCoordinate().replace(POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR);
				p2Content = p2.getCoordinate().replace(POINT_NAME_SEPARATOR, UPLOAD_COORDINATE_SEPARATOR);
				corrdinate = p1Content + UPLOAD_COORDINATE_SEPARATOR + p2Content + UPLOAD_ITEM_SEPARATOR;
			}		
		} else{
			Log.i(LOG_TAG,"没有测量数据");
			return "";
		}
    
    	corrdinate = corrdinate.substring(0, corrdinate.length() - 1);
    
		Log.i(LOG_TAG, "coordinate: " + corrdinate);
		corrdinate = RSACoder.encnryptDes(corrdinate, Constant.testDeskey);
		return corrdinate;
	}

    /**
     * 根据测量数据，获取上传数据的量测值
     * @param mtunnelTestPoints 测量数据
     * @return 测点的量测值
     */
	public static String getValueList(List<TunnelSettlementTotalData> mtunnelTestPoints) {

		String value = "";
		TunnelSettlementTotalData p;
		TunnelSettlementTotalData p1 = null;
		TunnelSettlementTotalData p2 = null;

		int tpCount = mtunnelTestPoints.size();

		// 拱顶
		if (tpCount == 1) {
			p = mtunnelTestPoints.get(0);
			if (p != null) {
				value = p.getCoordinate().split(POINT_NAME_SEPARATOR)[2]
						+ UPLOAD_ITEM_SEPARATOR;
			}
		}
		// 测线
		else if (tpCount == 2) {
			p1 = mtunnelTestPoints.get(0);
			p2 = mtunnelTestPoints.get(1);

			if (p1 != null && p2 != null) {
				value = AlertUtils.getLineLength(p1, p2)
						+ UPLOAD_ITEM_SEPARATOR;
			}
		} else {
			Log.i(LOG_TAG, "没有测量值");
			return "";
		}

		value = value.substring(0, value.length() - 1);
		Log.i(LOG_TAG, "value: " + value);
		return value;
	}	
}
