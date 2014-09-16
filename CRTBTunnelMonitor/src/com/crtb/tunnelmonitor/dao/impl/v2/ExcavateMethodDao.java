package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import android.util.Log;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.entity.ExcavateMethodEnum;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;

/**
 * 开挖方法对应的DAO
 * 
 * @author zhouwei
 * 
 */
public final class ExcavateMethodDao extends AbstractDao<TunnelCrossSectionParameter> {

	private static ExcavateMethodDao _instance;

	private ExcavateMethodDao() {
		
	}

	public static ExcavateMethodDao defaultDao() {

		if (_instance == null) {
			_instance = new ExcavateMethodDao();
		}

		return _instance;
	}

	public List<TunnelCrossSectionParameter> queryExcavateMethod() {

		final IAccessDatabase mDatabase = getCurrentDb();

		if (mDatabase == null) {
			return null;
		}
		
		// 开挖方法降序
		return mDatabase.queryObjects("select * from TunnelCrossSectionParameter order by ExcavateMethod ASC", TunnelCrossSectionParameter.class);
	}
	
	public int getExcavateMethodValue(){
		
		List<TunnelCrossSectionParameter> list = queryExcavateMethod();
		
		if(list == null || list.isEmpty()) return Constant.CUSTOM_METHOD_START_INDEX ;
		
		return list.get(list.size() - 1).getExcavateMethod() + 1 ;
	}

    private TunnelCrossSectionParameter queryByStableExcavateMethod(int excavateMethod){
//    	DT("台阶法",1, 5),
//    	ST("三台阶法",2, 7),
//    	QD("全断面法",3, 3),
//    	HX("环行开发法",4, 0),
//    	CD("中壁",5, 10),
//    	CRD("中壁",6, 10),
//    	SC("双侧壁法",7, 7),
//    	UNKOWN("未知",-10, -1) ;
//    	DB-地表下沉
    	
    	// 全断面法(A,S1(1,2))
    	// 台阶法(A,S1(1,2),S2(1,2))
    	// 三台阶法(A,S1(1,2),S2(1,2),S3(1,2))
    	    	
    	TunnelCrossSectionParameter sectionParameter = null;
    	
		if (excavateMethod == ExcavateMethodEnum.QD.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
			sectionParameter.setCrownPointNumber(1);
			sectionParameter.setSurveyLineNumber(1);
			sectionParameter.setSurveyLinePointName("S1,1,2");
		} else if (excavateMethod == ExcavateMethodEnum.DT.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
			sectionParameter.setCrownPointNumber(1);
			sectionParameter.setSurveyLineNumber(2);
			sectionParameter.setSurveyLinePointName("S1,1,2/S2,3,4");
		}else if (excavateMethod == ExcavateMethodEnum.ST.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
			sectionParameter.setCrownPointNumber(1);
			sectionParameter.setSurveyLineNumber(3);
			sectionParameter.setSurveyLinePointName("S1,1,2/S2,3,4/S3,5,6");
		}else if (excavateMethod == ExcavateMethodEnum.CD.getCode()
			   || excavateMethod == ExcavateMethodEnum.CRD.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
    		sectionParameter.setCrownPointNumber(2);
    		sectionParameter.setSurveyLineNumber(4);
    		sectionParameter.setSurveyLinePointName("S1,1,2/S2,3,4/S3,5,6/S4,7,8");
		} else if(excavateMethod == ExcavateMethodEnum.SC.getCode()){
    		sectionParameter = new TunnelCrossSectionParameter();
    		sectionParameter.setCrownPointNumber(1);
    		sectionParameter.setSurveyLineNumber(3);
    		sectionParameter.setSurveyLinePointName("S1,1,2/S2,3,4/S3,5,6");
    	} 
		
    	return sectionParameter;    	
    }
    
    private TunnelCrossSectionParameter queryCustomExcavateMethod(int customExcavateMethod){
    	Log.d(TAG, "TunnelCrossSectionParameterDao queryOneByCustomExcavateMethod, customExcavateMethod: " + customExcavateMethod);

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelCrossSectionParameter where ExcavateMethod =?";
        String[] args = new String[] { String.valueOf(customExcavateMethod) };

        return mDatabase.queryObject(sql, args, TunnelCrossSectionParameter.class);
    	
    }
    
    /**
     * 获取开挖方法对应的测点个数
     * @param excavateMethod
     * @return
     */
    public int getPointsByExcavateMethod(int excavateMethod){
    	int points = 0;
    	TunnelCrossSectionParameter para = queryOneByExcavateMethod(excavateMethod);
    	if(para != null){
    		points += para.getCrownPointNumber();
    		points += para.getSurveyLineNumber() * 2;
    	}
    	return points;
    }
    
    /**
     * 获取开挖方法对应的实体
     * @param excavateMethod
     * @return
     */
    public TunnelCrossSectionParameter queryOneByExcavateMethod(int excavateMethod){
    	TunnelCrossSectionParameter parameter = null;
		if (excavateMethod < Constant.CUSTOM_METHOD_START_INDEX) {
			parameter = queryByStableExcavateMethod(excavateMethod);
		} else {
			parameter = queryCustomExcavateMethod(excavateMethod);
		}
    	return parameter;
    }

    /**
     * 根据自定义开挖方法，查询基本模板
     * @param customExcavateMethod
     * @return
     */
    public int queryBaseModelByCustomExcavateMethod(int customExcavateMethod){
    	int excavateMethod = 0;
    	TunnelCrossSectionParameter param =  queryCustomExcavateMethod(customExcavateMethod);
    	if(param != null){
    		excavateMethod = param.getType();
    	}
    	return excavateMethod;
    }

    /**
     * 检查Db是否已经包含基本的自定义模板
     */
    public void checkBaseCustomExcavateMethod(){
//    	开挖名称  拱顶 测线点对 测线条数 详细 
//    	基础CD法	    2	4	6	S1,1,2/S2,3,4/S3,5,6/S4,7,8/S5,1,6/S6,3,8
//    	基础CRD法	    2	4	6	S1,1,2/S2,3,4/S3,5,6/S4,7,8/S5,1,6/S6,3,8
//    	基础双侧壁	    3	2	4	S1,A2,A3/S2,1,2/S3,3,4/S4,1,4
//    	双侧壁双台阶法	3	4	7	S1,A2,A3/S2,1,2/S3,3,4/S4,5,6/S5,7,8/S6,1,6/S7,3,8
		final String LOG_TAG = "TunnelCrossSectionParameterDao";
		int excavateMethod = Constant.CUSTOM_METHOD_START_INDEX;
		long enter = System.currentTimeMillis();
		TunnelCrossSectionParameter baseCD = new TunnelCrossSectionParameter();
		TunnelCrossSectionParameter baseCRD = new TunnelCrossSectionParameter();
		TunnelCrossSectionParameter baseSC = new TunnelCrossSectionParameter();
		TunnelCrossSectionParameter baseSCDT = new TunnelCrossSectionParameter();
		 
		if (queryOneByExcavateMethod(excavateMethod) == null) {
			baseCD.setType(ExcavateMethodEnum.CD.getCode());
			baseCD.setExcavateMethod(excavateMethod);
			baseCD.setMethodName("基础CD法");
			baseCD.setCrownPointNumber(2);
			baseCD.setSurveyLinePointNumber(4);
			baseCD.setSurveyLineNumber(6);
			baseCD.setSurveyLinePointName("S1,1,2/S2,3,4/S3,5,6/S4,7,8/S5,1,6/S6,3,8");
			if (insert(baseCD) != DB_EXECUTE_SUCCESS) {
				Log.i(LOG_TAG, "save base cd error");
			}
		}
		
		++excavateMethod;
		if (queryOneByExcavateMethod(excavateMethod) == null) {
			baseCRD.setType(ExcavateMethodEnum.CRD.getCode());
			baseCRD.setExcavateMethod(excavateMethod);
			baseCRD.setMethodName("基础CRD法");
			baseCRD.setCrownPointNumber(2);
			baseCRD.setSurveyLinePointNumber(4);
			baseCRD.setSurveyLineNumber(6);
			baseCRD.setSurveyLinePointName("S1,1,2/S2,3,4/S3,5,6/S4,7,8/S5,1,6/S6,3,8");
			if (insert(baseCRD) != DB_EXECUTE_SUCCESS) {
				Log.i(LOG_TAG, "save base crd error");
			}
		}
		
		++excavateMethod;
		if (queryOneByExcavateMethod(excavateMethod) == null) {
			baseSC.setType(ExcavateMethodEnum.SC.getCode());
			baseSC.setExcavateMethod(excavateMethod);
			baseSC.setMethodName("基础双侧壁");
			baseSC.setCrownPointNumber(3);
			baseSC.setSurveyLinePointNumber(2);
			baseSC.setSurveyLineNumber(4);
			baseSC.setSurveyLinePointName("S1,A2,A3/S2,1,2/S3,3,4/S4,1,4");
			if (insert(baseSC) != DB_EXECUTE_SUCCESS) {
				Log.i(LOG_TAG, "save base sc error");
			}
		}

		++excavateMethod;
		if (queryOneByExcavateMethod(excavateMethod) == null) {
			baseSCDT.setType(ExcavateMethodEnum.SC.getCode());
			baseSCDT.setExcavateMethod(excavateMethod);
			baseSCDT.setMethodName("双侧壁双台阶法");
			baseSCDT.setCrownPointNumber(3);
			baseSCDT.setSurveyLinePointNumber(4);
			baseSCDT.setSurveyLineNumber(7);
			baseSCDT.setSurveyLinePointName("S1,A2,A3/S2,1,2/S3,3,4/S4,5,6/S5,7,8/S6,1,6/S7,3,8");
			if (insert(baseSCDT) != DB_EXECUTE_SUCCESS) {
				Log.i(LOG_TAG, "save base sc_dt error");
			}
		}
		
		Log.i(LOG_TAG, " checkBaseCustomExcavateMethod:耗时 "+(System.currentTimeMillis() - enter));
    }

}
