
package com.crtb.tunnelmonitor.dao.impl.v2;

import java.util.ArrayList;
import java.util.List;

import org.zw.android.framework.IAccessDatabase;

import android.database.Cursor;
import android.util.Log;

import com.crtb.tunnelmonitor.entity.ExcavateMethodEnum;
import com.crtb.tunnelmonitor.entity.TunnelCrossSectionParameter;

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

    public void createTable() {
        final IAccessDatabase db = getCurrentDb();

        if (db == null) {
            return;
        }
        db.createTable(TunnelCrossSectionParameter.class);
    }

    
    public List<TunnelCrossSectionParameter> queryAll() {

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelCrossSectionParameter";

        return mDatabase.queryObjects(sql, TunnelCrossSectionParameter.class);
    }

    private TunnelCrossSectionParameter queryStableExcavateMethod(int excavateMethod){
//    	DT("台阶法",1, 5),
//    	ST("三台阶法",2, 7),
//    	QD("全断面法",3, 3),
//    	HX("环行开发法",4, 0),
//    	CD("中壁",5, 0),
//    	CRD("中壁",6, 0),
//    	SC("双侧壁法",7, 7),
//    	UNKOWN("未知",-10, -1) ;
//    	DB-地表下沉
    	
    	// 全断面法(A,S1(1,2))
    	// 台阶法(A,S1(1,2),S2(1,2))
    	// 三台阶法(A,S1(1,2),S2(1,2),S3(1,2))
    	
//YX 本处的点要和开挖方法的枚举定义一样，即点的个数，因为计算上传状态状态时，需要判断断面的点数
    	
    	TunnelCrossSectionParameter sectionParameter = null;
    	
		if (excavateMethod == ExcavateMethodEnum.QD.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
			sectionParameter.setCrownPointNumber(1);
			sectionParameter.setSurveyLineNumber(1);
			sectionParameter.setSurveyLinePointNumber(2);
			sectionParameter.setSurveyLinePointName("S1,1,2");
		} else if (excavateMethod == ExcavateMethodEnum.DT.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
			sectionParameter.setCrownPointNumber(1);
			sectionParameter.setSurveyLineNumber(2);
			sectionParameter.setSurveyLinePointNumber(4);
			sectionParameter.setSurveyLinePointName("S1,1,2/S2,3,4");
		}else if (excavateMethod == ExcavateMethodEnum.ST.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
			sectionParameter.setCrownPointNumber(1);
			sectionParameter.setSurveyLineNumber(2);
			sectionParameter.setSurveyLinePointNumber(4);
			sectionParameter.setSurveyLinePointName("S1,1,2/S2,3,4/S3,5,6");
		}else if (excavateMethod == ExcavateMethodEnum.CD.getCode()) {
			sectionParameter = new TunnelCrossSectionParameter();
    		sectionParameter.setCrownPointNumber(1);
    		sectionParameter.setSurveyLineNumber(4);
    		sectionParameter.setSurveyLinePointNumber(8);
    		sectionParameter.setSurveyLinePointName("S1,3,5/S2,4,6/S3,7,9/S4,8,10");
    		//sectionParameter.setSurveyLinePointName("S1,1,2/S2,3,6/S3,7,10");//开挖后变化
    	} else if(excavateMethod == ExcavateMethodEnum.CRD.getCode()){
    		sectionParameter = new TunnelCrossSectionParameter();
    		sectionParameter.setCrownPointNumber(1);
    		sectionParameter.setSurveyLineNumber(4);
    		sectionParameter.setSurveyLinePointNumber(8);
    		sectionParameter.setSurveyLinePointName("S1,3,5/S2,4,6/S3,7,9/S4,8,10");
    	} else if(excavateMethod == ExcavateMethodEnum.SC.getCode()){
    		sectionParameter = new TunnelCrossSectionParameter();
    		sectionParameter.setCrownPointNumber(1);
    		sectionParameter.setSurveyLineNumber(4);
    		sectionParameter.setSurveyLinePointNumber(4);
    		sectionParameter.setSurveyLinePointName("S1,3,5/S2,4,6/S3,3,6");
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
    
    public TunnelCrossSectionParameter queryOneByExcavateMethod(int excavateMethod){
    	TunnelCrossSectionParameter parameter = queryStableExcavateMethod(excavateMethod);
    	if(parameter == null){
    		parameter = queryCustomExcavateMethod(excavateMethod);
    	}
    	return parameter;
    }
    
    public TunnelCrossSectionParameter queryOneById(int id) {
        Log.d(TAG, "TunnelCrossSectionParameterDao queryOneById, id: " + id);

        final IAccessDatabase mDatabase = getCurrentDb();

        if (mDatabase == null) {
            return null;
        }

        String sql = "select * from TunnelCrossSectionParameter where ID=?";
        String[] args = new String[] { String.valueOf(id) };

        return mDatabase.queryObject(sql, args, TunnelCrossSectionParameter.class);
    }

    public String getGuidById(int id) {
    	TunnelCrossSectionParameter al = queryOneById(id);
        return al != null ? al.getGuid() : null;
    }

    public void deleteById(int id) {
        final IAccessDatabase db = getCurrentDb();
        Log.d(TAG, "TunnelCrossSectionParameterDao deleteById, id: " + id);

        if (db == null) {
            return;
        }

        String sql = "delete from TunnelCrossSectionParameter where ID=?";

        String[] args = new String[] { String.valueOf(id) };

        db.execute(sql, args);

    }

    public int insertOrUpdate(TunnelCrossSectionParameter parameter) {

      return 0;
    }

    public Cursor executeQuerySQL(String sql, String[] args) {
        IAccessDatabase db = getCurrentDb();
        if (db != null) {
            Cursor c = db.executeQuerySQL(sql, args);
            return c;
        }
        return null;
    }
    
}
