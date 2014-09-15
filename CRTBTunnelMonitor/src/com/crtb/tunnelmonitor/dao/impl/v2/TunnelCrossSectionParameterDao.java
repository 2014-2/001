
package com.crtb.tunnelmonitor.dao.impl.v2;

import org.zw.android.framework.IAccessDatabase;
import android.util.Log;
import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.entity.ExcavateMethodEnum;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;

/**
 * TunnelCrossSectionParameterDao对应的Dao
 * @author xu
 *
 */

public class TunnelCrossSectionParameterDao extends AbstractDao<TunnelCrossSectionParameter> {

    private static TunnelCrossSectionParameterDao _instance;

    private TunnelCrossSectionParameterDao() {

    }

    public static TunnelCrossSectionParameterDao defaultDao() {

        if (_instance == null) {
            _instance = new TunnelCrossSectionParameterDao();
        }

        return _instance;
    }
   
    private TunnelCrossSectionParameter queryByStableExcavateMethod(int excavateMethod){
//    	DT("台阶法",1, 5),
//    	ST("三台阶法",2, 7),
//    	QD("全断面法",3, 3),
//    	HX("环行开发法",4, 0),
//    	CD("中壁",5, 9),
//    	CRD("中壁",6, 9),
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
    		sectionParameter.setCrownPointNumber(1);
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
}
